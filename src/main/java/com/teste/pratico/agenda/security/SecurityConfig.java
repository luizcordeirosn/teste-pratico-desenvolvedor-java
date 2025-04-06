package com.teste.pratico.agenda.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String TEXT_PLAIN = "text/plain";
    private static final String CHARSET_UTF8 = "UTF-8";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(requests -> requests
                        .antMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/auth/**")
                        .permitAll()
                        .antMatchers(HttpMethod.POST, "/api/v1/solicitantes").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(this::customAuthenticationEntryPoint));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void customAuthenticationEntryPoint(HttpServletRequest request, HttpServletResponse response,
            Exception authException) throws IOException {
        response.setContentType(TEXT_PLAIN);
        response.setCharacterEncoding(CHARSET_UTF8);

        if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Usuário não autorizado");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acesso Negado");
        }
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
