package com.ptirado.nmviajes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ptirado.nmviajes.constants.SecurityPaths;

/**
 * Configuracion de seguridad de la aplicacion.
 *
 * <p>Define las reglas de autorizacion, configuracion de login/logout,
 * y manejo de excepciones de seguridad.</p>
 *
 * <p>Las rutas publicas y protegidas estan centralizadas en {@link SecurityPaths}.</p>
 *
 * @see SecurityPaths
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estaticos publicos
                .requestMatchers(SecurityPaths.STATIC_RESOURCES).permitAll()

                // Paginas web publicas
                .requestMatchers(SecurityPaths.PUBLIC_PAGES).permitAll()

                // Vistas de catalogo publicas (paquetes y destinos)
                .requestMatchers(SecurityPaths.PUBLIC_CATALOG_VIEWS).permitAll()

                // API publica
                .requestMatchers(SecurityPaths.PUBLIC_API).permitAll()

                // Solo administradores
                .requestMatchers(SecurityPaths.ADMIN_PAGES).hasRole("ADMIN")
                .requestMatchers(SecurityPaths.ADMIN_API).hasRole("ADMIN")

                // Requiere autenticacion para el resto
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(SecurityPaths.CSRF_IGNORED)
            );

        return http.build();
    }
}
