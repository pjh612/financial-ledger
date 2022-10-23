package in.payhere.financialledger.user.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import in.payhere.financialledger.user.entity.EmailToken;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
	Optional<EmailToken> findByEmail(@Param("email") String email);
}
