package project.dropbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import project.dropbox.models.user.User;
import project.dropbox.repositories.user.UserRepository;
import project.dropbox.utils.AuthenticationFilter;
import project.dropbox.utils.JWTService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTService jwtService,
            UserRepository userRepository
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/oauth2/**").permitAll()
                            .anyRequest().authenticated();
                })

                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {

                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

                            String githubId = oauthUser.getAttribute("id").toString();
                            String username = oauthUser.getAttribute("login");

                            User user = userRepository.findByGithubId(githubId)
                                    .orElseGet(() -> {
                                        User newUser = new User(username, githubId);
                                        return userRepository.save(newUser);
                                    });

                            String token = jwtService.generateToken(user.getUserId());

                            response.setContentType("application/json");
                            response.getWriter()
                                    .write("{\"token\":\"" + token + "\"}");
                        }))
                .addFilterBefore(
                        new AuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}