package in.payhere.financialledger.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import in.payhere.financialledger.common.security.Role;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockJwtAuthentication {

	String token() default "accessToken";

	long id() default 1L;

	String email() default "test1234@gmail.com";

	Role role() default Role.USER;
}
