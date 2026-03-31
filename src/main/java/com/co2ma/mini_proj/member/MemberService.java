package com.co2ma.mini_proj.member;

import com.co2ma.mini_proj.member.dto.AuthResponse;
import com.co2ma.mini_proj.member.dto.LoginRequest;
import com.co2ma.mini_proj.member.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequest request){
        if(memberRepository.existsByLoginId(request.loginId())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .email(request.email())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    public AuthResponse login(LoginRequest request) {
        // 1. DB에서 아이디로 회원 조회
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        // 2. 비밀번호 일치 여부 확인
        // 주의: 암호화된 비밀번호는 매번 결과가 달라지므로, 반드시 matches() 메서드로 비교해야 합니다.
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 발급 (현재는 뼈대만 잡아두고, 나중에 실제 JWT 생성 로직으로 교체)
        String accessToken = "dummy-access-token";   // 예: jwtProvider.createAccessToken(member.getId(), member.getRole())
        String refreshToken = "dummy-refresh-token"; // 예: jwtProvider.createRefreshToken(member.getId())

        return new AuthResponse(accessToken, refreshToken);
    }


}
