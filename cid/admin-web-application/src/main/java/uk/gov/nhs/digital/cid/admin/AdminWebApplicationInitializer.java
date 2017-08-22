package uk.gov.nhs.digital.cid.admin;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.gov.nhs.digital.cid.admin", "org.common"})
public class AdminWebApplicationInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(AdminWebApplicationInitializer.class, args);
	}

    @SuppressWarnings("rawtypes")
	@Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return ((KeycloakPrincipal) request.getUserPrincipal()).getKeycloakSecurityContext().getToken();
    }

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}