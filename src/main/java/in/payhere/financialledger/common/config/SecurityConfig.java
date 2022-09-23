package in.payhere.financialledger.common.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import in.payhere.financialledger.common.config.properties.SecurityConfigProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableConfigurationProperties({SecurityConfigProperties.class, JwtConfigureProperties.class})
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final SecurityConfigProperties securityConfigProperties;
	private final JwtConfigureProperties jwtConfigureProperties;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Algorithm algorithm() {
		return Algorithm.HMAC512(this.jwtConfigureProperties.clientSecret());
	}

	@Bean
	public JWTVerifier jwtVerifier(Algorithm algorithm) {
		return JWT.require(algorithm)
			.withIssuer(this.jwtConfigureProperties.issuer())
			.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers(HttpMethod.GET, this.securityConfigProperties.patterns().ignoring().get("GET"))
			.antMatchers(HttpMethod.POST, this.securityConfigProperties.patterns().ignoring().get("POST"))
			.antMatchers(HttpMethod.PATCH, this.securityConfigProperties.patterns().ignoring().get("PATCH"))
			.antMatchers(HttpMethod.DELETE, this.securityConfigProperties.patterns().ignoring().get("DELETE"))
			.antMatchers(HttpMethod.PUT, this.securityConfigProperties.patterns().ignoring().get("PUT"))
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	AuthenticationEntryPoint authenticationEntryPoint() {
		return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, this.securityConfigProperties.patterns().permitAll().get("GET"))
			.permitAll()
			.antMatchers(HttpMethod.POST, this.securityConfigProperties.patterns().permitAll().get("POST"))
			.permitAll()
			.antMatchers(HttpMethod.PATCH, this.securityConfigProperties.patterns().permitAll().get("PATCH"))
			.permitAll()
			.antMatchers(HttpMethod.DELETE, this.securityConfigProperties.patterns().permitAll().get("DELETE"))
			.permitAll()
			.antMatchers(HttpMethod.PUT, this.securityConfigProperties.patterns().permitAll().get("PUT"))
			.permitAll()
			.antMatchers(HttpMethod.OPTIONS, this.securityConfigProperties.patterns().permitAll().get("OPTIONS"))
			.permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().disable()
			.csrf().disable()
			.httpBasic().disable()
			.rememberMe().disable()
			.logout().disable()
			.headers().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint());

		return http.build();
	}
}

