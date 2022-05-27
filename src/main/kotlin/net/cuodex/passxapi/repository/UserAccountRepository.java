package net.cuodex.passxapi.repository;

import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsernameOrEmail(String username, String email);
    Optional<UserAccount> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}