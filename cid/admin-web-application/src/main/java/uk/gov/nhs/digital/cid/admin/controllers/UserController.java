package uk.gov.nhs.digital.cid.admin.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.common.exception.impl.DefaultWrappedException;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.gov.nhs.digital.cid.admin.domain.Credentials;
import uk.gov.nhs.digital.cid.admin.domain.Invite;
import uk.gov.nhs.digital.cid.admin.domain.Role;
import uk.gov.nhs.digital.cid.admin.domain.User;
import uk.gov.nhs.digital.cid.admin.util.KeycloakUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Value(value = "${rest.api.keycloak.user.operations.url}")
	private String keycloakUserOperationURL;

	@Autowired
	@Value(value = "${rest.api.keycloak.url}")
	private String keycloakURL;

	@Autowired
	@Value(value = "${nhs.gps.redirect.url}")
	private String redirectURL;

	@Autowired
	@Value(value = "${keycloak.resource}")
	private String clientId;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getRecord(Principal principal, ModelAndView modelAndView, HttpServletRequest request) {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		ResponseEntity<User[]> responseEntity = this.restTemplate.exchange(keycloakUserOperationURL, HttpMethod.GET,
				requestEntity, User[].class);
		modelAndView.addObject("userlist", Arrays.asList(responseEntity.getBody()));
		modelAndView.setViewName("users");
		return modelAndView;
	}

	@RequestMapping(value = "/serviceInvite", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView serviceInvite(ModelAndView modelAndView, final HttpServletRequest request)
			throws DefaultWrappedException {
		modelAndView.addObject("invite", new Invite(true));
		modelAndView.setViewName("serviceInvite");
		return modelAndView;
	}

	@RequestMapping(value = "/soleEmailAccess", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView soleEmailAccess(@ModelAttribute Invite invite, ModelAndView modelAndView,
			final HttpServletRequest request) throws DefaultWrappedException {
		if (invite.isAccept()) {
			modelAndView.addObject("invite", new Invite(true));
			modelAndView.setViewName("soleEmailAccess");
		} else {
			modelAndView.setViewName("notAllowed");
		}
		return modelAndView;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView showForm(@ModelAttribute Invite invite, Principal principal, ModelAndView modelAndView,
			final HttpServletRequest request) throws DefaultWrappedException {
		if (invite.isAccept()) {
			KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
			String loggedInUserId = keycloakPrincipal.getKeycloakSecurityContext().getIdToken().getSubject();
			modelAndView.addObject("user", new User(loggedInUserId));
			modelAndView.setViewName("user");
		} else {
			modelAndView.setViewName("notAllowed");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView createUser(Principal principal, @ModelAttribute User user, ModelAndView modelAndView,
			RedirectAttributes redirectAttributes) throws RestClientException, IOException {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		user.fillAttributes();

		// Add headers to rest template
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(user, accessToken);

		// Make rest call to create user
		String location = this.restTemplate
				.exchange(keycloakUserOperationURL, HttpMethod.POST, requestEntity, User.class).getHeaders()
				.getLocation().getPath();
		String userId = StringUtils.substringAfterLast(location, "/");

		// Make rest call to get user
		requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		User savedUser = this.restTemplate
				.exchange(keycloakUserOperationURL + "/" + userId, HttpMethod.GET, requestEntity, User.class).getBody();
		savedUser.populateAttributes();

		// Make rest call to reset password
		Credentials credentials = new Credentials("password", user.getPassword());
		requestEntity = KeycloakUtil.getHttpEntityWithHeader(credentials, accessToken);
		this.restTemplate.exchange(keycloakUserOperationURL + "/" + userId + "/reset-password", HttpMethod.PUT,
				requestEntity, User.class);

		// Make rest call to assign face-to-face-done role to the user
		Role requiredRole = getRole(accessToken, "face-to-face-done");

		if (null != requiredRole) {
			requestEntity = KeycloakUtil.getHttpEntityWithHeader(new Role[] { requiredRole }, accessToken);
			this.restTemplate.exchange(keycloakUserOperationURL + "/" + userId + "/role-mappings/realm",
					HttpMethod.POST, requestEntity, User.class);
		}
		sendVerificationEmail(accessToken, userId);

		redirectAttributes.addFlashAttribute("savedUser", savedUser);
		modelAndView.setViewName("redirect:");
		return modelAndView;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getUser(Principal principal, @PathVariable("id") String userId, ModelAndView modelAndView) {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		User user = this.restTemplate
				.exchange(keycloakUserOperationURL + "/" + userId, HttpMethod.GET, requestEntity, User.class).getBody();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("viewUser");
		return modelAndView;
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getProfile(Principal principal, ModelAndView modelAndView, final HttpServletRequest request) {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		ResponseEntity<User[]> responseEntity = this.restTemplate.exchange(keycloakUserOperationURL, HttpMethod.GET,
				requestEntity, User[].class);
		List<User> userlist = Arrays.asList(responseEntity.getBody());
		User currentUser = userlist.stream().filter(user -> principal.getName().equals(user.getUsername())).findAny()
				.orElse(null);
		modelAndView.addObject("user", currentUser);
		modelAndView.setViewName("profile");
		return modelAndView;
	}

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView verify(ModelAndView modelAndView, HttpServletRequest request) throws DefaultWrappedException {
		modelAndView.setViewName("verify");
		return modelAndView;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/verify-success", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView verifySuccess(Principal principal, ModelAndView modelAndView, HttpServletRequest request)
			throws DefaultWrappedException {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		// Make rest call to assign face-to-face-done role to the user
		Role requiredRole = getRole(accessToken, "verify-done");

		if (null != requiredRole) {
			KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
			String userId = keycloakPrincipal.getKeycloakSecurityContext().getIdToken().getSubject();
			HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(new Role[] { requiredRole },
					accessToken);
			this.restTemplate.exchange(keycloakUserOperationURL + "/" + userId + "/role-mappings/realm",
					HttpMethod.POST, requestEntity, User.class);
		}
		modelAndView.setViewName("verifySuccess");
		return modelAndView;
	}

	private Role getRole(String accessToken, String roleName) {
		Role requiredRole = null;
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		List<Role> roles = this.restTemplate.exchange(keycloakURL + "/roles", HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<Role>>() {
				}).getBody();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				requiredRole = role;
				break;
			}
		}
		return requiredRole;
	}

	private void sendVerificationEmail(String accessToken, String userId) {
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		this.restTemplate.exchange(keycloakUserOperationURL + "/" + userId + "/send-verify-email?client_id=" + clientId
				+ "&redirect_uri=" + redirectURL, HttpMethod.PUT, requestEntity, User.class);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView deleteUser(Principal principal, @PathVariable("id") String userId, ModelAndView modelAndView) {
		String accessToken = KeycloakUtil.getAccessToken(principal);
		// Add headers to rest template
		HttpEntity<?> requestEntity = KeycloakUtil.getHttpEntityWithHeader(accessToken);
		HttpHeaders header = this.restTemplate
				.exchange(keycloakUserOperationURL + "/" + userId, HttpMethod.DELETE, requestEntity, HttpDelete.class)
				.getHeaders();
		modelAndView.setViewName("users");
		return modelAndView;
	}
}