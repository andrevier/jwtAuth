package securityexample.jwtAuth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import securityexample.jwtAuth.entities.Otp;

public interface OtpRepository extends JpaRepository<Otp, String>{
    Optional<Otp> findOtpByUsername(String username);
}
