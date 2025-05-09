package com.example.userservice.api.user.service;

import com.example.userservice.api.role.repository.RoleRepository;
import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.dto.UserDto;
import com.example.userservice.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

//    private final RestTemplate restTemplate;

    /** 역할 repository */
    private final RoleRepository roleRepository;

    /**
     * 사용자 정보 조회 - 로그인
     *
     * @param username 사용자 아이디(email)
     * @return UserDetails
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opUserEntity = userRepository.findByEmail(username);
        User user = opUserEntity.orElseThrow(() -> new UsernameNotFoundException(username + ": not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                true, true, true, true,
                new ArrayList<>());
    }

    /**
     * 사용자 역할 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 역할 목록
     */
    @Transactional(readOnly = true)
    public List<SimpleGrantedAuthority> getAuthorities(String userId) throws UsernameNotFoundException {
        return roleRepository.findRoleNamesByUserId(userId)
                .stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toList());
    }

    /**
     * 사용자 정보 조회
     *
     * @param email 사용자 이메일 (아이디)
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserDto getUserDetailsByEmail(String email) {
        Optional<User> opUserEntity = userRepository.findByEmail(email);
        User user = opUserEntity.orElseThrow(() -> new UsernameNotFoundException(email + ": not found"));

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * 회원가입 - 사용자 신규 등록
     *
     * @param userDto 사용자 등록 정보
     * @return 신규 사용자 정보
     */
    @Transactional
    public User createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPwd()));

        return userRepository.save(user);
    }

    /**
     * 사용자 및 주문 정보 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 및 주문 정보
     */
    @Transactional(readOnly = true)
    public UserDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User not found. ");
        }

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * 모든 사용자 목록 조회
     *
     * @return 모든 사용자 목록
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
