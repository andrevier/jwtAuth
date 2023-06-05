package securityexample.businesslogic.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import securityexample.businesslogic.security.KeyUtil;
import securityexample.businesslogic.security.OtpAuthentication;
import securityexample.businesslogic.security.UsernamePasswordAuthentication;

//@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter{
    // @Autowired
    // private AuthenticationManager manager;
    
    // Takes the value of the key used to sign the JWT token from the 
    // properties file.
    @Value("${jwt.signing.key}")
    private String signingKey;

    Logger logger = LoggerFactory.getLogger(InitialAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException {
        // Get the current AuthenticationManager instance.
        ApplicationContext context = WebApplicationContextUtils
            .getRequiredWebApplicationContext(request.getServletContext());
        
        AuthenticationManager manager = context
            .getBean(AuthenticationManager.class);

        // Get variables from the header.
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        String code = request.getHeader("code");
        logger.info("---initial filter---");
        logger.info("username: " + username);
        logger.info("password: " + password);
        logger.info("code: " + code);
        
        // If the header does not contain the code (OTP), the authentication is
        // in the first step: auth based on username and password.
        if (code.isEmpty()) {
            Authentication auth = 
                new UsernamePasswordAuthentication(username, password);
            manager.authenticate(auth);

        } else {
            // For the second authentication step, the instance of type 
            // OtpAuthentication is sent to the AuthenticationManager.
            Authentication auth = new OtpAuthentication(username, code);
            auth = manager.authenticate(auth);

            // Then, builds a JWT and stores the username of the authenticated
            // user as one of its claims. We use the key to sign the token.
            // SecretKey key = Keys.hmacShaKeyFor(
            //     signingKey.getBytes(StandardCharsets.UTF_8));
            SecretKey key = Keys.hmacShaKeyFor(
                KeyUtil.getKey().getBytes(StandardCharsets.UTF_8));

            String jwt = Jwts.builder()
                .setClaims(Map.of("username", username))
                .signWith(key)
                .compact();
            
            response.setHeader("Authorization", jwt);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) 
    throws ServletException {
        // Filters any request only in the login path. Others, don't.
        return !request.getServletPath().equals("/login");
    }

    
    
}
