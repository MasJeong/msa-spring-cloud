package com.example.fileservice.com.util;

import java.time.LocalDate;

public class FileUtil {

    /**
     * 파일명을 오늘 날짜(yyyy/MM/dd) 디렉터리 경로와 결합하여 파일 경로 생성.
     * 경로 내에 중복된 슬래시("//")가 있을 경우 하나의 슬래시("/")로 치환.
     *
     * @param filename 파일명 (예: "example.txt")
     * @return 날짜 디렉터리 경로와 결합된 파일 경로 (예: "2025/05/15/example.txt")
     */
    public static String buildFilePath(String filename) {
        String path = generateDateDirectory() + "/" + filename;
        return path.replace("//", "/");
    }

    /**
     * 오늘 날짜를 기준으로 연/월/일(yyyy/MM/dd) 형태의 디렉터리 경로 생성.
     *
     * @return 오늘 날짜의 디렉터리 경로 (예: "2025/05/15")
     */
    public static String generateDateDirectory() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "/"
                + String.format("%02d", now.getMonthValue()) + "/"
                + String.format("%02d", now.getDayOfMonth());
    }

    /**
     * 파일 경로에서 실제 파일명 추출
     *
     * @param filePath 전체 파일 경로 문자열
     * @return 순수 파일명 (예: "test.jpg")
     */
    public static String extractFilename(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }
}
