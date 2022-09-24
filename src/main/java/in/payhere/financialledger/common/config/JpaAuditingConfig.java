package in.payhere.financialledger.common.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import in.payhere.financialledger.common.security.jwt.JwtAuthentication;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
	@Bean
	public AuditorAware<String> autAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
				return Optional.empty();
			}

			return Optional.of(((JwtAuthentication)authentication.getPrincipal()).id().toString());

		};
	}
}
