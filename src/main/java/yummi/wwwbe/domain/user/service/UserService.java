package yummi.wwwbe.domain.user.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yummi.wwwbe.domain.user.dto.UserRequestDTO;
import yummi.wwwbe.domain.user.dto.UserResponseDTO;
import yummi.wwwbe.domain.user.entity.UserEntity;
import yummi.wwwbe.domain.user.entity.UserRoleType;
import yummi.wwwbe.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }


    public Boolean isAccess(String username) {

        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        if ("ROLE_ADMIN".equals(sessionRole)) {
            return true;
        }

        if (username.equals(sessionUsername)) {
            return true;
        }

        return false;
    }

    @Transactional
    public void createOneUser(UserRequestDTO dto) {

        String username = dto.getUsername();
        String password = dto.getPassword();
        String nickname = dto.getNickname();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(bCryptPasswordEncoder.encode(password));
        entity.setNickname(nickname);
        entity.setRole(UserRoleType.USER);

        userRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO readOneUser(String username) {

        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setRole(entity.getRole().toString());

        return dto;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> readAllUsers() {

        List<UserEntity> list = userRepository.findAll(); // list 에 모든 유저 정보가 담김
        List<UserResponseDTO> dtos = new ArrayList<>();

        for (UserEntity user : list) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setRole(user.getRole().toString());

            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRole().toString())
                .build();
    }

    @Transactional
    public void updateOneUser(UserRequestDTO dto, String username) {

        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            entity.setNickname(dto.getNickname());
        }

        userRepository.save(entity);
    }

    @Transactional
    public void deleteOneUser(String username) {

        userRepository.deleteByUsername(username);
    }
}
