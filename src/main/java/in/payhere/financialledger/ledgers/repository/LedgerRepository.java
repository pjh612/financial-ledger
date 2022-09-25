package in.payhere.financialledger.ledgers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.payhere.financialledger.ledgers.entity.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

	@Query("select l from Ledger l join l.user u where u.id = :userId")
	List<Ledger> findAllByUserId(@Param("userId") Long userId);
}
