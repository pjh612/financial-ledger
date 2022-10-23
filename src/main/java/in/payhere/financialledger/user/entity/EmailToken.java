package in.payhere.financialledger.user.entity;

import static java.text.MessageFormat.format;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EmailToken {

	private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 5L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;

	private String token;

	private LocalDateTime expireAt;

	private boolean isVerified;

	private LocalDate sentDate;

	private int sentCount;

	private EmailToken(String email, String token, LocalDateTime expireAt, boolean isVerified, LocalDate sentDate,
		int sentCount) {
		this.email = email;
		this.token = token;
		this.expireAt = expireAt;
		this.isVerified = isVerified;
		this.sentDate = sentDate;
		this.sentCount = sentCount;
	}

	public static EmailToken createEmailToken(String email, String token) {
		return new EmailToken(email, token, LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE), false,
			LocalDate.now(), 1);
	}

	public boolean isVerified() {
		return this.isVerified;
	}

	public void verify(String token) {
		if (this.token.equals(token) && expireAt.isAfter(LocalDateTime.now())) {
			this.isVerified = true;
		}

		throw new BusinessException(ErrorCode.NOT_VALID_VERIFICATION_TOKEN, format("token : {0} is not valid", token));
	}

	public void updateToken(String token) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		boolean isAfterLastSentDate = today.isAfter(this.sentDate);
		if (isAfterLastSentDate) {
			this.sentCount = 1;
			this.sentDate = today;
		} else if (this.expireAt.isAfter(now)) {
			throw new BusinessException(ErrorCode.UPDATE_VERIFICATION_TOKEN_EXCEPTION, "You can send it once every five minutes.");
		} else if (this.sentCount > 2) {
			throw new BusinessException(ErrorCode.UPDATE_VERIFICATION_TOKEN_EXCEPTION, "You can only send three times a day.");
		} else {
			this.sentCount++;
		}

		this.token = token;
		this.isVerified = false;
		this.expireAt = now.plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE);
	}
}
