package in.payhere.financialledger.common.security.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public record JwtAuthentication(String token, Long id, String email) {
	public JwtAuthentication {
		checkArgument(isNotEmpty(token), "access token must be provided");
		checkArgument(id != null, "id must be provided");
		checkArgument(isNotBlank(email), "username must be provided");
	}

}
