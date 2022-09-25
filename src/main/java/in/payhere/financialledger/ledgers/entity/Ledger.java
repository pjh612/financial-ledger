package in.payhere.financialledger.ledgers.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import in.payhere.financialledger.common.BaseEntity;
import in.payhere.financialledger.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ledger extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Size(min = 2, max = 50)
	private String name;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Ledger(String name, User user) {
		this.name = name;
		this.user = user;
	}
}
