package securityexample.businesslogic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

@Configuration
public class ProperAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter{
    @Autowired
    private OtpAuthenticationProvider otpAuthenticationProvider;

    @Autowired
    private UsernameAuthenticationProvider usernameAuthenticationProvider;
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .authenticationProvider(otpAuthenticationProvider);
    
        auth
            .authenticationProvider(usernameAuthenticationProvider);

        super.configure(auth);
    }

    
}
