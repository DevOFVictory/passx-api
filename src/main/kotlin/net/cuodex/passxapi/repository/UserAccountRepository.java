package net.cuodex.passxapi.repository;

import net.cuodex.passxapi.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
