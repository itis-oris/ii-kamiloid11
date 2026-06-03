package com.skillswap.service;

import com.skillswap.dto.UserDto;
import com.skillswap.entity.Role;
import com.skillswap.entity.User;
import com.skillswap.exception.UserAlreadyExistsException;
import com.skillswap.form.ProfileForm;
import com.skillswap.form.RegistrationForm;
import com.skillswap.repository.ReviewRepository;
import com.skillswap.repository.RoleRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegistrationForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new UserAlreadyExistsException("Логин уже занят: " + form.getUsername());
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new UserAlreadyExistsException("E-mail уже зарегистрирован: " + form.getEmail());
        }

        User novyyUchastnik = new User();
        novyyUchastnik.setUsername(form.getUsername());
        novyyUchastnik.setEmail(form.getEmail());
        novyyUchastnik.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        novyyUchastnik.setFirstName(form.getFirstName());
        novyyUchastnik.setLastName(form.getLastName());

        Role bazovayaRol = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Базовая роль не найдена"));
        novyyUchastnik.getRoles().add(bazovayaRol);

        return userRepository.save(novyyUchastnik);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserDto toDto(User user) {
        Double sredniyReyting = reviewRepository.findAverageRatingByUserId(user.getId());
        Set<String> nazvaniyaRoley = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getBio(),
                user.getAvatarUrl(), user.getCreatedAt(), user.getIsActive(),
                nazvaniyaRoley, sredniyReyting
        );
    }

    @Transactional
    public void updateProfile(String username, ProfileForm form) {
        User profilDlyaPravki = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        profilDlyaPravki.setFirstName(form.getFirstName());
        profilDlyaPravki.setLastName(form.getLastName());
        profilDlyaPravki.setBio(form.getBio());
        profilDlyaPravki.setAvatarUrl(form.getAvatarUrl());
        userRepository.save(profilDlyaPravki);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void toggleActive(Long userId) {
        User uchetkaPolzovatelya = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        uchetkaPolzovatelya.setIsActive(!uchetkaPolzovatelya.getIsActive());
        userRepository.save(uchetkaPolzovatelya);
    }
}
