// CustomUserDetailsService.java
package com.epam.finaltask.security;

import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", usernameOrEmail);

        User appUser = userRepository.findUserByUsername(usernameOrEmail)
                .or(() -> userRepository.findUserByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    log.warn("User not found: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });

        log.info("User found: {} ({})", appUser.getUsername(), appUser.getEmail());

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + appUser.getRole())
        );

        return new CustomUserDetails(
                appUser.getUsername(),
                appUser.getEmail(),
                appUser.getPassword(),
                authorities,
                appUser.isActive()
        );
    }
}
