package uk.gov.nhs.digital.cid.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invite {

	private boolean accept;

	public Invite() {
		super();
	}

	public Invite(boolean accept) {
		super();
		this.accept = accept;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	@Override
	public String toString() {
		return "Invite [accept=" + accept + "]";
	}

}