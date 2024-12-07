//package com.example.apigatewayservice.config;
//
//import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * spring cloud gateway 프로퍼티 설정
// */
//@Configuration
//public class FilterConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
//                                           RedisRateLimiter redisRateLimiter,
//                                           UserKeyResolver userKeyResolver) {
//        return builder.routes()
//                .route("user-service", r -> r.path("/**")
//                        .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter)
//                                .setKeyResolver(userKeyResolver)))
//                        .uri("lb://user-service"))
//                .build();
//
////        return builder.routes()
////                .route(predicateSpec -> predicateSpec.path("/first-service/**")
////                        .filters(gatewayFilterSpec -> gatewayFilterSpec.addRequestHeader("first-request", "first-request-header")
////                                .addResponseHeader("first-response", "first-response-header"))
////                        .uri("http://localhost:8081/"))
////                .route(predicateSpec -> predicateSpec.path("/second-service/**")
////                        .filters(gatewayFilterSpec -> gatewayFilterSpec.addRequestHeader("second-request", "second-request-header")
////                                .addResponseHeader("second-response", "second-response-header"))
////                        .uri("http://localhost:8082/"))
////                .build();
//    }
//
//}
