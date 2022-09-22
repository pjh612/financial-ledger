package in.payhere.financialledger.common.config.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "security")
public record SecurityConfigProperties(PatternConfigProperties patterns) {
	public record PatternConfigProperties(Map<String, String[]> ignoring, Map<String, String[]> permitAll) {
	}
}
