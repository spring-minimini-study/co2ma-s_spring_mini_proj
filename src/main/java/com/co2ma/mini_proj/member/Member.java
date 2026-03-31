package com.co2ma.mini_proj.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동 증가(Auto Increment)
    @Column(name = "id")
    private Long id; // 대리키 (PK)

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname; // 닉네임도 중복 방지

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일도 중복 방지

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private Role role; // USER, ADMIN 등 권한 관리용 필드
}
