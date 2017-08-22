package uk.gov.nhs.digital.cid.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credentials {

	private String type;
	private String value;
	private boolean temporary;

	public Credentials() {
		this.temporary = false;
	}

	public Credentials(String type, String value, boolean temporary) {
		this();
		this.type = type;
		this.value = value;
		this.temporary = temporary;
	}

	public Credentials(String type, String value) {
		this();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}
}