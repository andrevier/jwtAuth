package securityexample.businesslogic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import securityexample.businesslogic.security.filter.InitialAuthenticationFilter;
import securityexample.businesslogic.security.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity  
public class SecurityConfig {

    // @Autowired
    // private OtpAuthenticationProvider otpAuthenticationProvider;

    // @Autowired
    // private UsernameAuthenticationProvider usernameAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) 
    throws Exception {
        http.csrf(csrf -> csrf.disable());
        
        http.addFilterAt(
            new InitialAuthenticationFilter(),
         BasicAuthenticationFilter.class)
        .addFilterAfter(
            new JwtAuthenticationFilter(),
        BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authz -> authz
        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
