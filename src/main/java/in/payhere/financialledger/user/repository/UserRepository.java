package in.payhere.financialledger.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.payhere.financialledger.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
