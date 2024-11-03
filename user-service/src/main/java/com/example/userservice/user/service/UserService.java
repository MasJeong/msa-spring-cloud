package com.example.userservice.user.service;

import com.example.userservice.user.domain.UserEntity;
import com.example.userservice.user.dto.UserDto;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.user.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RestTemplate restTemplate;

    private final Environment env;

    /**
     * 사용자 정보 조회 - 로그인
     * @param username 사용자 아이디(email)
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> opUserEntity = userRepository.findByEmail(username);
        UserEntity userEntity = opUserEntity.orElseThrow(() -> new UsernameNotFoundException(username + ": not found"));

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    /**
     * 사용자 정보 조회
     * @param email 사용자 이메일 (아이디)
     * @return 사용자 정보
     */
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> opUserEntity = userRepository.findByEmail(email);
        UserEntity userEntity = opUserEntity.orElseThrow(() -> new UsernameNotFoundException(email + ": not found"));

        return modelMapper.map(userEntity, UserDto.class);
    }

    /**
     * 회원가입 - 사용자 신규 등록
     * @param userDto 사용자 등록 정보
     * @return 신규 사용자 정보
     */
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        return userDto;
    }

    /**
     * 사용자 및 주문 정보 조회
     * @param userId 사용자 아이디
     * @return 사용자 및 주문 정보
     */
    @Transactional(readOnly = true)
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found. ");
        }

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        // RestTemplate 사용하여 order service 요청
        Optional.ofNullable(env.getProperty("order_service.url")).ifPresent(orderServiceUrl -> {
            String requestOrderUrl = String.format(orderServiceUrl, userId);

            ResponseEntity<List<ResponseOrder>> orderListResponse =
                    restTemplate.exchange(requestOrderUrl,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            List<ResponseOrder> orders = orderListResponse.getBody();
            userDto.setOrders(orders);
        });

        return userDto;
    }

    /**
     * 모든 사용자 목록 조회
     * @return 모든 사용자 목록
     */
    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
