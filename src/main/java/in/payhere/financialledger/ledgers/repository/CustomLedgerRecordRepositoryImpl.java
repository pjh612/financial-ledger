package in.payhere.financialledger.ledgers.repository;

import static in.payhere.financialledger.ledgers.entity.QLedgerRecord.ledgerRecord;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import in.payhere.financialledger.ledgers.entity.LedgerRecord;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomLedgerRecordRepositoryImpl implements CustomLedgerRecordRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<LedgerRecord> findByIdAndUserIdAndIsRemoved(Long recordId, Long userId, boolean isRemoved) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(ledgerRecord)
				.join(ledgerRecord.ledger)
				.where(
					ledgerRecord.id.eq(recordId),
					ledgerRecord.ledger.user.id.eq(userId),
					ledgerRecord.isRemoved.eq(isRemoved))
				.fetchOne());
	}
}
