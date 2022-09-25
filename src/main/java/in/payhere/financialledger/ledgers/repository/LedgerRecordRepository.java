package in.payhere.financialledger.ledgers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.payhere.financialledger.ledgers.entity.LedgerRecord;

public interface LedgerRecordRepository extends JpaRepository<LedgerRecord, Long>, CustomLedgerRecordRepository {
}
