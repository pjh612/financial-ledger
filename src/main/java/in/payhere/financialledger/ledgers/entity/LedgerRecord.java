package in.payhere.financialledger.ledgers.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import in.payhere.financialledger.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LedgerRecord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ledger_id")
	private Ledger ledger;

	@Positive
	private int amount;

	@Size(min = 2, max = 300)
	private String memo;

	private LocalDateTime datetime;

	@Enumerated(EnumType.STRING)
	private RecordType type;

	private boolean isRemoved;

	public LedgerRecord(Ledger ledger, int amount, String memo, LocalDateTime datetime, RecordType type) {
		this(null, ledger, amount, memo, datetime, type, false);
	}

	public LedgerRecord(Long id, Ledger ledger, int amount, String memo, LocalDateTime datetime, RecordType type,
		boolean isRemoved) {
		this.id = id;
		this.ledger = ledger;
		this.amount = amount;
		this.memo = memo;
		this.datetime = datetime;
		this.type = type;
		this.isRemoved = isRemoved;
	}

	public void restore() {
		this.isRemoved = false;
	}

	public void remove() {
		this.isRemoved = true;
	}

	public void updateMemo(String memo) {
		if (memo != null) {
			this.memo = memo;
		}
	}

	public void updateAmount(Integer amount) {
		if (amount != null) {
			this.amount = amount;
		}
	}
}
