package com.skillswap.security;

import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Адаптер между нашей сущностью {@link User} и интерфейсом
 * {@link UserDetailsService} Spring Security. Используется при логине:
 * Spring сам дёргает {@link #loadUserByUsername(String)} и проверяет hash пароля.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User uchetka = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        var prava = uchetka.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                uchetka.getUsername(),
                uchetka.getPasswordHash(),
                uchetka.getIsActive(),
                true, true, true,
                prava
        );
    }
}
