package net.cuodex.passxapi.repository;

import net.cuodex.passxapi.entity.LoginCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CredentialsRepository extends JpaRepository<LoginCredential, Long> {

    @Query("SELECT MAX(cred.id) from LoginCredential as cred")
    Long getMaxId();

    // Get next_val from hibernate_sequence
    @Query(value = "SELECT next_val FROM hibernate_sequence", nativeQuery = true)
    Long getNextVal();


    @Modifying
    @Transactional
    @Query("delete from LoginCredential cred where cred.id = ?1")
    void deleteLoginCredentialById(Long entityId);
}
