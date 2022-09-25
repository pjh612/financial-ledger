package in.payhere.financialledger.ledgers.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.ledgers.entity.Ledger;
import in.payhere.financialledger.ledgers.entity.LedgerRecord;
import in.payhere.financialledger.ledgers.entity.RecordType;
import in.payhere.financialledger.ledgers.repository.dto.RecordSearchCondition;
import in.payhere.financialledger.ledgers.service.dto.request.CreateLedgerRecordRequest;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerRecordResponse;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.response.LedgerRecordResponse;
import in.payhere.financialledger.user.entity.User;

@SpringBootTest
@Transactional
class LedgerServiceIntegrationTest {

	@Autowired
	LedgerService ledgerService;

	@Autowired
	EntityManager em;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("가계부 생성 성공")
	void createLedgerSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		em.persist(dummyUser);

		//when
		CreateLedgerResponse response = ledgerService.createLedger(dummyUser.getId(), "newLedger");

		//then
		assertThat(response.id()).isNotNull();
	}

	@Test
	@DisplayName("가계부 내역 작성 성공")
	void recordSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		em.persist(dummyUser);
		em.persist(dummyLedger);

		CreateLedgerRecordRequest request = new CreateLedgerRecordRequest(
			dummyLedger.getId(),
			dummyUser.getId(),
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);

		//when
		CreateLedgerRecordResponse response = ledgerService.record(request);

		//then
		assertThat(response.id()).isNotNull();
	}

	@Test
	@DisplayName("가계부 항목 삭제 성공")
	void recordRemoveSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when
		ledgerService.remove(dummyUser.getId(), ledgerRecord.getId());

		//then
		assertThat(ledgerRecord.isRemoved()).isTrue();
	}

	@Test
	@DisplayName("내 가계부 항목이 아니라면 삭제할 수 없다.")
	void notMyRecordRemoveFail() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when, then
		assertThatThrownBy(() -> ledgerService.remove(999L, ledgerRecord.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("records does not contain record ID");
	}

	@Test
	@DisplayName("가계부 항목 복구 성공")
	void recordRestoreSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		ledgerRecord.remove();
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when
		ledgerService.restore(dummyUser.getId(), ledgerRecord.getId());

		//then
		assertThat(ledgerRecord.isRemoved()).isFalse();
	}

	@Test
	@DisplayName("이미 삭제된 내역이라면 삭제할 수 없다.")
	void alreadyRemovedRecordRemoveFail() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		ledgerRecord.remove();
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when, then
		assertThatThrownBy(() -> ledgerService.remove(dummyUser.getId(), ledgerRecord.getId())).isInstanceOf(
				BusinessException.class)
			.hasMessageContaining("not contain");
	}

	@Test
	@DisplayName("이미 복구된 내역이라면 복구할 수 없다.")
	void alreadyRestoredRecordRemoveFail() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when, then
		assertThatThrownBy(() -> ledgerService.restore(dummyUser.getId(), ledgerRecord.getId())).isInstanceOf(
				BusinessException.class)
			.hasMessageContaining("not contain");
	}

	@Test
	@DisplayName("가계부 내역 단건 조회 성공")
	void findOneLedgerRecordSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when
		LedgerRecordResponse ledgerRecordResponse = ledgerService.findOneRecordByUserIdAndRecordId(
			dummyUser.getId(), ledgerRecord.getId());

		//then
		assertThat(ledgerRecordResponse.recordId()).isEqualTo(ledgerRecord.getId());
		assertThat(ledgerRecordResponse.amount()).isEqualTo(ledgerRecord.getAmount());
		assertThat(ledgerRecordResponse.memo()).isEqualTo(ledgerRecord.getMemo());
		assertThat(ledgerRecordResponse.type()).isEqualTo(ledgerRecord.getType());
		assertThat(ledgerRecordResponse.isRemoved()).isEqualTo(ledgerRecord.isRemoved());
		assertThat(ledgerRecordResponse.datetime()).isEqualTo(ledgerRecord.getDatetime());
	}

	@Test
	@DisplayName("내가 작성한 내역이 아니라면 조회에 실패한다.")
	void findNotMyLedgerRecordFail() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		LedgerRecord ledgerRecord = new LedgerRecord(
			dummyLedger,
			2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		em.persist(ledgerRecord);

		//when, then
		assertThatThrownBy(
			() -> ledgerService.findOneRecordByUserIdAndRecordId(999L, ledgerRecord.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("not contain");
	}

	@Test
	@DisplayName("가계부 내역을 페이징한다.")
	void findRecordsByPagingSuccess() {
		//given
		User dummyUser = createDummyUser("test1234@gmail.com");
		Ledger dummyLedger = createDummyLedger("가계부", dummyUser);
		em.persist(dummyUser);
		em.persist(dummyLedger);
		List<LedgerRecord> ledgerRecords = Stream.iterate(1000, n -> n + 200).limit(5)
			.map(amount -> createDummyLedgerRecord(dummyLedger, RecordType.EXPENSE, amount,
				LocalDateTime.now()))
			.peek(em::persist)
			.toList();

		PageRequest pageRequest = PageRequest.of(0, 3);
		RecordSearchCondition condition = new RecordSearchCondition(dummyUser.getId(), dummyLedger.getId(),
			false, RecordType.EXPENSE, null, null);

		//when
		Page<LedgerRecordResponse> foundRecords = ledgerService.findRecordsByPaging(pageRequest, condition);

		//then
		assertThat(foundRecords.getContent().size()).isEqualTo(pageRequest.getPageSize());
		assertThat(foundRecords.getTotalElements()).isEqualTo(ledgerRecords.size());
	}

	@Test
	@DisplayName("페이징 시, 내 가계부가 아닌 내역은 조회되지 않는다.")
	void findRecordsByPagingWithNotMyLedgerFail() {
		//given
		User dummyUserA = createDummyUser("dummya@gmail.com");
		User dummyUserB = createDummyUser("dummyb@gmail.com");
		Ledger dummyLedgerA = createDummyLedger("가계부", dummyUserA);
		Ledger dummyLedgerB = createDummyLedger("가계부", dummyUserB);
		em.persist(dummyUserA);
		em.persist(dummyUserB);
		em.persist(dummyLedgerA);
		em.persist(dummyLedgerB);

		List<LedgerRecord> ledgerRecordsA = Stream.iterate(1000, n -> n + 200).limit(5)
			.map(amount -> createDummyLedgerRecord(dummyLedgerA, RecordType.EXPENSE, amount,
				LocalDateTime.now()))
			.peek(em::persist)
			.toList();
		List<LedgerRecord> ledgerRecordsB = Stream.iterate(1000, n -> n + 200).limit(5)
			.map(amount -> createDummyLedgerRecord(dummyLedgerB, RecordType.EXPENSE, amount,
				LocalDateTime.now()))
			.peek(em::persist)
			.toList();

		PageRequest pageRequest = PageRequest.of(0, 3);
		RecordSearchCondition condition = new RecordSearchCondition(dummyUserA.getId(), dummyLedgerB.getId(),
			false, RecordType.EXPENSE, null, null);

		//when
		Page<LedgerRecordResponse> foundRecords = ledgerService.findRecordsByPaging(pageRequest, condition);

		//then
		assertThat(foundRecords.getContent().size()).isEqualTo(0);
		assertThat(foundRecords.getTotalElements()).isEqualTo(0);
	}

	@Test
	@DisplayName("특정일에 작성된 가계부 내역을 페이징할 수 있다.")
	void findSpecificDateRecordsByPaging() {
		//given
		User dummyUserA = createDummyUser("dummya@gmail.com");
		Ledger dummyLedgerA = createDummyLedger("가계부", dummyUserA);
		em.persist(dummyUserA);
		em.persist(dummyLedgerA);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime differentDate = LocalDateTime.now().minusDays(2);
		Stream.iterate(1000, n -> n + 200).limit(3)
			.map(amount -> createDummyLedgerRecord(dummyLedgerA, RecordType.EXPENSE, amount,
				now))
			.peek(em::persist)
			.toList();
		em.persist(createDummyLedgerRecord(dummyLedgerA, RecordType.EXPENSE, 10000, differentDate));

		PageRequest pageRequest = PageRequest.of(0, 5);
		RecordSearchCondition condition = new RecordSearchCondition(dummyUserA.getId(), dummyLedgerA.getId(),
			false, RecordType.EXPENSE, now, now);

		//when
		Page<LedgerRecordResponse> foundRecords = ledgerService.findRecordsByPaging(pageRequest, condition);

		//then
		assertThat(foundRecords.getContent().size()).isEqualTo(3);
		assertThat(foundRecords.getTotalElements()).isEqualTo(3);
	}

	private User createDummyUser(String email) {
		String encodedPassword = passwordEncoder.encode("test12345");
		return new User(email, encodedPassword);
	}

	private Ledger createDummyLedger(String name, User user) {
		return new Ledger(name, user);
	}

	private LedgerRecord createDummyLedgerRecord(Ledger ledger, RecordType type, int amount,
		LocalDateTime dateTime) {
		return new LedgerRecord(
			ledger,
			amount,
			"memo",
			dateTime,
			type);
	}
}