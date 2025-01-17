package com.chatr.user;


import com.chatr.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
