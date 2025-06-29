package org.example.backend.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//public class jwttest {
//    public static void main(String[] args) throws IOException, InterruptedException {
//        Jwt jwt = new Jwt();
//        String token = jwt.generateJwtToken("1", "zhangsan");
//        System.out.println("New Token: " + token);
//
//        // 使用 HttpClient 发送测试请求
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/Back_end_war_exploded/admin/manage/add"))
//                .POST(HttpRequest.BodyPublishers.ofString("{\"fullName\":\"John Doe\",\"loginName\":\"johndoe\",\"password\":\"password123\",\"phone\":\"123456789\",\"departmentId\":\"1\"}"))
//                .header("Content-Type", "application/json")
//                .header("Cookie", "jwtToken=" + token)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Response: " + response.body());
//    }
//}