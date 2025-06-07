package com.freelancer.backend.config;

import com.freelancer.backend.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration                                    // Marca esta classe como configuração do Spring
@EnableWebSecurity                               // Habilita o suporte a configurações Web do Spring Security
@EnableMethodSecurity                           // Habilita @PreAuthorize em métodos
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;    // Trata 401

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;     // Filtro que valida o JWT

    @Autowired
    private UserDetailsServiceImpl userDetailsService;         // Carrega UserDetails

    // Bean responsável por codificar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expõe o AuthenticationManager para uso em AuthController
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configuração principal das regras de segurança HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // desabilita CORS/CSRF (você pode personalizar CORS aqui, se precisar)
                .cors().and().csrf().disable()
                // trata exceções de autenticação não autorizada
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                // sem uso de sessão de servidor; usamos JWT stateless
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // regras de acesso para as rotas:
                .authorizeHttpRequests()
                // endpoints de auth e swagger são públicos
                .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // listar projetos (GET) também é público
                .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                // qualquer outro endpoint exige autenticação
                .anyRequest().authenticated()
                .and()
                // adiciona o filtro de JWT antes do filtro de autenticação padrão
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
