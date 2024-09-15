package com.example.userservice.user.gayhoon.etc;

/**
 * java 공부할 때 이런 클래스 파일 만들어서 작업해주세요.
 * 실행은 왼쪽 재생 버튼 클릭하면 돼요.
 */
public class JavaLearningSample {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        sb.append("Start").append("\n");

        for (int i = 0; i < 9; i++) {
            sb.append("test").append(i + 1).append("\n");
        }

        sb.append("End");

        System.out.println(sb);
    }

}
