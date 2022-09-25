package in.payhere.financialledger.ledgers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.payhere.financialledger.ledgers.entity.LedgerRecord;
import in.payhere.financialledger.ledgers.repository.dto.RecordSearchCondition;

public interface CustomLedgerRecordRepository {
	List<LedgerRecord> findByDynamicQuery(RecordSearchCondition condition);

	Page<LedgerRecord> findByDynamicQuery(Pageable pageable, RecordSearchCondition condition);

	Optional<LedgerRecord> findByIdAndUserIdAndIsRemoved(Long recordId, Long userId, boolean isRemoved);
}
