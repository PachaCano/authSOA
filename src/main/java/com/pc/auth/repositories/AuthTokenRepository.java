package com.pc.auth.repositories;

import com.pc.auth.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM auth_token WHERE fecha_expiracion < ?", nativeQuery = true)
    void purgeToDate(Date hasta);

    Optional<AuthToken> findFirstByUsername (String username);
}
