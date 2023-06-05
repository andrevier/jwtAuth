package securityexample.businesslogic.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import securityexample.businesslogic.security.KeyUtil;
import securityexample.businesslogic.security.UsernamePasswordAuthentication;

// This filter validates the JWT by checking the signature, creates an authenticated 
// Authentication object, and adds it to the SecurityContext.
//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.signing.key}")
    private String signingKey;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        logger.info("---JWT filter---");
        logger.info("jwt: " + jwt);

        // SecretKey key = Keys.hmacShaKeyFor(
        //     signingKey.getBytes(StandardCharsets.UTF_8));
        SecretKey key = Keys.hmacShaKeyFor(
            KeyUtil.getKey().getBytes(StandardCharsets.UTF_8));
            
        // Parses the token to obtain the claims and verifies the signature.
        // An exception is thrown if the signature isn’t valid.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        
        String username = String.valueOf(claims.get("username"));
        
        // Creates the Authentication instance that we add to the
        // SecurityContext.
        GrantedAuthority a = new SimpleGrantedAuthority("user");
        var auth = new UsernamePasswordAuthentication(
            username, null, List.of(a));
        
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);        
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) 
    throws ServletException {   
        // Configures this filter not to be triggered on requests for the 
        // /login path.
        return request.getServletPath().equals("/login");
    }
    
}
