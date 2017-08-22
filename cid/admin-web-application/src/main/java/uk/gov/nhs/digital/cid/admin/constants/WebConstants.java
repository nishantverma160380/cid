package uk.gov.nhs.digital.cid.admin.constants;

public interface WebConstants {

	interface SessionVariables {
		String LOGGED_IN_USER = "loggedInUser";
	}

	interface AdminUser {
		String USER_ID = "000000000000";
		String USERNAME = "ADMIN";
	}

	interface Operations {
		String SAVE = "Save ";
		String READ = "Read ";
		String READ_ALL = "Read all ";
		String DELETE = "Delete ";
	}
}
