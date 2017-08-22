package uk.gov.nhs.digital.cid.admin.util;

import java.security.Principal;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class KeycloakUtil {

	public static HttpEntity<?> getHttpEntityWithHeader(Object object, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<?> requestEntity;

		if(object == null) {
			requestEntity = new HttpEntity<>(headers);
		} else {
			requestEntity = new HttpEntity<>(object, headers);
		}
		return requestEntity;
	}

	public static HttpEntity<?> getHttpEntityWithHeader(String accessToken) {
		return getHttpEntityWithHeader(null, accessToken);
	}

	@SuppressWarnings("unchecked")
	public static String getAccessToken(Principal principal) {
		KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
		String accessToken = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
		return accessToken;
	}
}
