package ru.nilebox.collabedit.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nilebox.collabedit.model.User;

/**
 * Utility class for retrieving current user details
 * @author nile
 */
public class UserDetailsUtil {
	public static User getUserDetailsFromContext() {
		return (User) getCoreUserDetailsFromContext();
	}

	public static UserDetails getCoreUserDetailsFromContext() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null) {
			Authentication auth = ctx.getAuthentication();
			if (auth != null) {
				return (UserDetails) auth.getPrincipal();
			}
		}

		return null;
	}
}
