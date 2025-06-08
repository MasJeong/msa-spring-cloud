package com.example.fileservice.api.file.web;

import com.example.fileservice.api.file.constants.FileConstant;
import com.example.fileservice.com.config.WebDAVConfig;
import com.example.fileservice.com.util.FileUtil;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final Sardine sardine;

    private final WebDAVConfig webDAVConfig;

    /**
     * WebDAV 파일 업로드
     *
     * @param file 업로드할 파일
     * @return 업로드 완료된 파일 경로
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (file.getSize() > FileConstant.MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size exceeds limit (1GB)");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String filePath = FileUtil.buildFilePath(file.getOriginalFilename());
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
     * WebDAV 서버에서 파일을 스트리밍 다운로드하는 엔드포인트
     * ex) GET /api/files/download?filePath=user-docs/2025/05/04/test.jpg
     *
     * @param filePath WebDAV 기본 URL 기준 상대 파일 경로 (예: "user-docs/2025/05/04/test.jpg")
     * @return 파일 스트림이 포함된 ResponseEntity (자동 다운로드 트리거)
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String filePath) {

        if (filePath.contains("..") || filePath.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }

        String fullUrl = webDAVConfig.getBaseUrl() + "/" + filePath;

        try (InputStream is = sardine.get(fullUrl)) {
            String filename = FileUtil.extractFilename(filePath);
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

            log.error("Sardine file download failure: ", e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IOException e) {
            log.error("File download failure: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
