package securityexample.jwtAuth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import securityexample.jwtAuth.entities.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findUserByUsername(String username);
}
