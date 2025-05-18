package com.example.scrapeservice.config;

import com.example.scrapeservice.model.AppUser;
import com.example.scrapeservice.model.Role;
import com.example.scrapeservice.repository.UserRepository;
import com.example.scrapeservice.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/cart/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .oauth2Login(oauth2login -> {
                    oauth2login.successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                            Map<String, Object> attributes = oauth2Token.getPrincipal().getAttributes();

                            String email = (String) attributes.get("email");
                            String name = (String) attributes.get("given_name");
                            String surname = (String) attributes.get("family_name");

                            Optional<AppUser> existingUserOptional = userRepository.findByEmail(email);
                            if (existingUserOptional.isPresent()) {
                                AppUser existingUser = existingUserOptional.get();
                                boolean updated = false;

                                if (!name.equals(existingUser.getName())) {
                                    existingUser.setName(name);
                                    updated = true;
                                }
                                if (!surname.equals(existingUser.getSurname())) {
                                    existingUser.setSurname(surname);
                                    updated = true;
                                }

                                if (updated) {
                                    userRepository.save(existingUser);
                                }
                            } else {
                                AppUser newUser = AppUser.builder()
                                        .email(email)
                                        .name(name)
                                        .surname(surname)
                                        .role(Role.USER)
                                        .blacklisted(false)
                                        .active(true)
                                        .build();
                                userRepository.save(newUser);
                            }

                            response.sendRedirect("http://localhost:5173");
                        }
                    });
                })
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
