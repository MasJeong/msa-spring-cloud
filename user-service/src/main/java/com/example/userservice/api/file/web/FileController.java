package com.example.userservice.api.file.web;

import com.example.userservice.com.config.WebDAVConfig;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final Sardine sardine;

    private final WebDAVConfig webDAVConfig;

    /**
     * WebDAV 파일 업로드 엔드포인트
     *
     * @param file 업로드할 파일
     * @return 업로드 완료된 파일 경로
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String filePath = buildFilePath(file.getOriginalFilename());
            String webDAVFileUrl = webDAVConfig.getBaseUrl() + "/" + filePath;
            log.info("Uploading file to WebDAV: {}", webDAVFileUrl);

            sardine.put(webDAVFileUrl, inputStream);

            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Sardine file upload failure: ", e);
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }

    }

    /**
     * 파일명을 오늘 날짜(yyyy/MM/dd) 디렉터리 경로와 결합하여 파일 경로 생성.
     * 경로 내에 중복된 슬래시("//")가 있을 경우 하나의 슬래시("/")로 치환.
     *
     * @param filename 파일명 (예: "example.txt")
     * @return 날짜 디렉터리 경로와 결합된 파일 경로 (예: "2024/05/15/example.txt")
     */
    private String buildFilePath(String filename) {
        String path = generateDateDirectory() + "/" + filename;
        return path.replace("//", "/");
    }

    /**
     * 오늘 날짜를 기준으로 연/월/일(yyyy/MM/dd) 형태의 디렉터리 경로 생성.
     *
     * @return 오늘 날짜의 디렉터리 경로 (예: "2024/05/15")
     */
    private String generateDateDirectory() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "/"
                + String.format("%02d", now.getMonthValue()) + "/"
                + String.format("%02d", now.getDayOfMonth());
    }


    /**
     * WebDAV 서버에서 파일을 스트리밍 다운로드하는 엔드포인트
     * ex) GET /api/files/download?filePath=user-docs/2025/05/04/test.jpg
     *
     * @param filePath WebDAV 기본 URL 기준 상대 파일 경로 (예: "user-docs/2025/05/04/test.jpg")
     * @return 파일 스트림이 포함된 ResponseEntity (자동 다운로드 트리거)
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String filePath) throws IOException {

        // 경로 검증
        if (filePath.matches(".*(\\.\\.|/|\\\\).*")) {
            return ResponseEntity.badRequest().build();
        }

        String fullUrl = webDAVConfig.getBaseUrl() + "/" + filePath;

        try {
            InputStream is = sardine.get(fullUrl);
            String filename = extractFilename(filePath);

            // 파일명 인코딩
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(is));

        } catch (SardineException e) {
            if (e.getStatusCode() == 404) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    /**
     * 파일 경로에서 실제 파일명 추출
     *
     * @param filePath 전체 파일 경로 문자열
     * @return 순수 파일명 (예: "test.jpg")
     */
    private String extractFilename(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

}
