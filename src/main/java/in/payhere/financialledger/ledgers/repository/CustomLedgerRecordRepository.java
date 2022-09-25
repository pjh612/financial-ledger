package in.payhere.financialledger.ledgers.repository;

import java.util.Optional;

import in.payhere.financialledger.ledgers.entity.LedgerRecord;

public interface CustomLedgerRecordRepository {

	Optional<LedgerRecord> findByIdAndUserIdAndIsRemoved(Long recordId, Long userId, boolean isRemoved);
}
