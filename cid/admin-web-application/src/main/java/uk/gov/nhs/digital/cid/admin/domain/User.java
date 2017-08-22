package uk.gov.nhs.digital.cid.admin.domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	@JsonProperty("id")
	private String userId;
	private String username;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private String firstName;

	@JsonIgnore
	private String lastName;
	private String email;

	@JsonIgnore
	private String nhsNumber;

	@JsonIgnore
	private String careWorkerAccessId;

	private Map<String, List<String>> attributes;
	private boolean enabled;

	public User() {
		this.enabled = true;
		this.attributes = new LinkedHashMap<String, List<String>>();
		//Temporary hardcoding of care worker access id
		this.email = "cid.user2@gmail.com";
		this.careWorkerAccessId = "temp-access-id";
		this.nhsNumber = "742 345 1200";
	}

	public User(String userId, String username, String password, String firstName, String lastName, String email, String nhsNumber, String careWorkerAccessId, boolean enabled) {
		this();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.nhsNumber = nhsNumber;
		this.careWorkerAccessId = careWorkerAccessId;
		this.enabled = enabled;
	}

	public User(String careWorkerAccessId) {
		this();
		this.careWorkerAccessId = careWorkerAccessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNhsNumber() {
		return nhsNumber;
	}

	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<String, List<String>> getAttributes() {
		return attributes;
	}

	public void addAttribute(String key, String value) {
		List<String> values = Arrays.asList(value);
		this.attributes.put(key, values);
	}

	public String getCareWorkerAccessId() {
		return careWorkerAccessId;
	}

	public void setCareWorkerAccessId(String careWorkerAccessId) {
		this.careWorkerAccessId = careWorkerAccessId;
	}

	public void fillAttributes() {
		if(StringUtils.isNotEmpty(this.email)) {
			this.username = StringUtils.split(this.email, "@")[0];
		}
		if(StringUtils.isNotEmpty(this.nhsNumber)) {
			addAttribute("nhsNumber", this.nhsNumber);
		}
		if(StringUtils.isNotEmpty(this.careWorkerAccessId)) {
			addAttribute("careWorkerAccessId", this.careWorkerAccessId);
		}

		//Hardcoding initial password for now
		this.password = "Welcome123";
	}

	public void populateAttributes() {
		for(String key : this.attributes.keySet()) {
			if(StringUtils.equals("nhsNumber", key)) {
				List<String> values = this.attributes.get(key);
				if(!values.isEmpty() && StringUtils.isNotEmpty(values.get(0))) {
					this.nhsNumber = values.get(0);
				}
			} else if(StringUtils.equals("careWorkerAccessId", key)) {
				List<String> values = this.attributes.get(key);
				if(!values.isEmpty() && StringUtils.isNotEmpty(values.get(0))) {
					this.careWorkerAccessId = values.get(0);
				}
			}
		}
	}
}