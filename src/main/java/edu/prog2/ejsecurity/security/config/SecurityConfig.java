package edu.prog2.ejsecurity.security.config;

import edu.prog2.ejsecurity.security.jwt.JwtAuthFilter;
import edu.prog2.ejsecurity.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled  = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UsuarioServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h
                        .frameOptions(frame -> frame.disable()))
                .build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
/*
@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    // cadena de filtros
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // En APIs stateless no usamos cookies/sesión → se desactiva (lo trae ACTIVO)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // PÚBLICAS: cualquiera, sin login
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // POR ROL: la autorización en acción
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // RESTO: solo usuarios logueados
                        .anyRequest().authenticated()
                )
                //se habilita HTTP Basic usando la configuración predeterminada
                // de Spring Security
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /// super temporal - creamos usuarios en memoria
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        //Builder es un patrón de creación que construye
        // objetos complejos paso a paso, separando la construcción
        // de la representación.
        // User de security ya implementa este patrón en nuestras clases
        // para usarlo usamos @Builder de lombok
        UserDetails estudiante = User.builder()
                .username("ana@ies63lastoscas.edu.ar")
                .password(encoder.encode("user1234"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("profesor@ies63lastoscas.edu.ar")
                .password(encoder.encode("admin1234"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(estudiante, admin);
    }
        // codifica la clave de los usuarios
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
*/