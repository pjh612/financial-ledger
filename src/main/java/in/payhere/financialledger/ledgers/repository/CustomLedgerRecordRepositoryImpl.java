package in.payhere.financialledger.ledgers.repository;

import static in.payhere.financialledger.ledgers.entity.QLedgerRecord.ledgerRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import in.payhere.financialledger.ledgers.entity.LedgerRecord;
import in.payhere.financialledger.ledgers.entity.RecordType;
import in.payhere.financialledger.ledgers.repository.dto.RecordSearchCondition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomLedgerRecordRepositoryImpl implements CustomLedgerRecordRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<LedgerRecord> findByDynamicQuery(RecordSearchCondition condition) {
		return jpaQueryFactory.selectFrom(ledgerRecord)
			.join(ledgerRecord.ledger)
			.where(
				eqLedgerId(condition.ledgerId()),
				ledgerRecord.ledger.user.id.eq(condition.userId()),
				eqIsRemoved(condition.isRemoved()),
				eqRecordType(condition.type()),
				betweenDateTime(condition.startAt(), condition.endAt()))
			.fetch();
	}

	@Override
	public Page<LedgerRecord> findByDynamicQuery(Pageable pageable, RecordSearchCondition condition) {
		List<LedgerRecord> contents = jpaQueryFactory.selectFrom(ledgerRecord)
			.join(ledgerRecord.ledger)
			.where(
				eqLedgerId(condition.ledgerId()),
				ledgerRecord.ledger.user.id.eq(condition.userId()),
				eqIsRemoved(condition.isRemoved()),
				eqRecordType(condition.type()),
				goeDateTime(condition.startAt()),
				loeDateTime(condition.endAt()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		LongSupplier countSupplier = () -> (long)jpaQueryFactory.selectFrom(ledgerRecord)
			.join(ledgerRecord.ledger)
			.where(
				eqLedgerId(condition.ledgerId()),
				ledgerRecord.ledger.user.id.eq(condition.userId()),
				eqIsRemoved(condition.isRemoved()),
				eqRecordType(condition.type()),
				goeDateTime(condition.startAt()),
				loeDateTime(condition.endAt()))
			.fetch().size();

		return PageableExecutionUtils.getPage(contents, pageable, countSupplier);
	}

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

	private BooleanExpression eqLedgerId(Long ledgerId) {
		return ledgerId == null ? null : ledgerRecord.ledger.id.eq(ledgerId);
	}

	private BooleanExpression eqUserId(Long userId) {
		return userId == null ? null : ledgerRecord.ledger.user.id.eq(userId);
	}

	private BooleanExpression eqIsRemoved(Boolean isRemoved) {
		return isRemoved == null ? null : ledgerRecord.isRemoved.eq(isRemoved);
	}

	private BooleanExpression eqRecordType(RecordType type) {
		return type == null ? null : ledgerRecord.type.eq(type);
	}

	private BooleanExpression goeDateTime(LocalDateTime dateTime) {
		return dateTime == null ? null : ledgerRecord.datetime.goe(dateTime);
	}

	private BooleanExpression loeDateTime(LocalDateTime dateTime) {
		return dateTime == null ? null : ledgerRecord.datetime.loe(dateTime);
	}

	private BooleanExpression betweenDateTime(LocalDateTime startAt, LocalDateTime endAt) {
		return goeDateTime(startAt).and(loeDateTime(endAt));
	}
}
