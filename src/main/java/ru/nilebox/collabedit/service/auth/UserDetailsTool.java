package ru.nilebox.collabedit.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nilebox.collabedit.model.User;

public class UserDetailsTool {
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
