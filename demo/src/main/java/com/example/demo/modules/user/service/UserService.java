package com.example.demo.modules.user.service;

import com.example.demo.modules.user.dto.UserDto;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    public UserDto.Response createUser(UserDto.CreateRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        log.info("새 사용자 생성됨: {}", savedUser.getEmail());

        return modelMapper.map(savedUser, UserDto.Response.class);
    }

    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        UserDto.Response userResponse = modelMapper.map(user, UserDto.Response.class);

        return UserDto.LoginResponse.builder()
                .accessToken(jwt)
                .user(userResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto.Response getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        return modelMapper.map(user, UserDto.Response.class);
    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> getAllActiveUsers() {
        return userRepository.findAllActiveUsers().stream()
                .map(user -> modelMapper.map(user, UserDto.Response.class))
                .collect(Collectors.toList());
    }

    public UserDto.Response updateUser(Long id, UserDto.UpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("사용자 정보 업데이트됨: {}", updatedUser.getEmail());

        return modelMapper.map(updatedUser, UserDto.Response.class);
    }

    public void changePassword(Long id, UserDto.PasswordChangeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다");
        }

        // 새 비밀번호 설정
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("사용자 비밀번호 변경됨: {}", user.getEmail());
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("사용자 비활성화됨: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> searchUsersByNickname(String nickname) {
        return userRepository.findByNicknameContainingAndIsActiveTrue(nickname).stream()
                .map(user -> modelMapper.map(user, UserDto.Response.class))
                .collect(Collectors.toList());
    }
} 