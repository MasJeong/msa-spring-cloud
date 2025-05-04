package com.example.userservice.api.file.web;

import com.example.userservice.com.config.WebDAVConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final Sardine sardine;

    private final String baseUrl;

    public FileController(Sardine sardine, WebDAVConfig config) {
        this.sardine = sardine;
        this.baseUrl = config.getBaseUrl();
    }

    /**
     * WebDAV 파일 업로드 엔드포인트
     * @param file 업로드할 파일
     * @return 업로드 완료된 파일 경로
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String filePath = buildFilePath(file.getOriginalFilename());
            sardine.put(filePath, file.getInputStream());
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    private String buildFilePath(String filename) {
        String path = generateDateDirectory() + "/" + filename;

        return path.replace("//", "/");
    }

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
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) throws IOException {
        String fullUrl = baseUrl + "/" + filePath;

        // 파일 존재 여부 사전 확인
        if (!sardine.exists(fullUrl)) {
            return ResponseEntity.notFound().build();
        }

        // WebDAV 스트림 리소스 획득
        InputStream inputStream = sardine.get(fullUrl);
        Resource resource = new InputStreamResource(inputStream);

        // 다운로드 헤더 설정
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + extractFilename(filePath) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
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
