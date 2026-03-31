package com.co2ma.mini_proj.member.dto;

public record AuthResponse(
        String accessToken, // 30~60 분 정도 유지가 되어지는 실제 사용 되는 엑세스 토큰
        String refreshToken // AccessToken의 수명이 다 되었을 때 다시금 Token을 받기 위한 refreshToken
) {
}
