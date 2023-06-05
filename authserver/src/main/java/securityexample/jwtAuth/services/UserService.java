package securityexample.jwtAuth.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import securityexample.jwtAuth.config.CodeUtility;
import securityexample.jwtAuth.entities.Otp;
import securityexample.jwtAuth.entities.User;
import securityexample.jwtAuth.repositories.OtpRepository;
import securityexample.jwtAuth.repositories.UserRepository;

@Service
@Transactional
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    
    public void auth(User user) {
        // Check authentication of the user and allows the generation of the
        // OTP.
        Optional<User> userFetched = 
            userRepository.findUserByUsername(user.getUsername());
        
        if (userFetched.isPresent()) {
            if (passwordEncoder.matches(
                    user.getPassword(), userFetched.get().getPassword())) {
                renewOtp(userFetched.get());
            } else {
                throw new BadCredentialsException("Bad credentials.");
            }
        } else {
            throw new BadCredentialsException("Bad credentials.");
        }
    }
    
    public void renewOtp(User userConfirmed) {
        // Generate a new OTP or renew it and stores it in the database.
        String code = CodeUtility.generateCode();

        Optional<Otp> userOtp = otpRepository
            .findOtpByUsername(userConfirmed.getUsername());
        
        if (userOtp.isPresent()) {
            userOtp.get().setCode(code);
        } else {
            Otp otp = new Otp();
            otp.setUsername(userConfirmed.getUsername());
            otp.setCode(code);
            otpRepository.save(otp);
        }

    }

    public boolean check(Otp otpConfirm) {
        // Validate an OTP.
        Optional<Otp> userOtp = 
            otpRepository.findOtpByUsername(otpConfirm.getUsername());
        
        if (userOtp.isPresent()) {
            if (otpConfirm.getCode().equals(userOtp.get().getCode())) {
                return true;
            }
        }

        return false;
    }
}
