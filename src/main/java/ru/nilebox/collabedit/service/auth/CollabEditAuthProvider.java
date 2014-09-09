package ru.nilebox.collabedit.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.nilebox.collabedit.dao.UserRepository;
import ru.nilebox.collabedit.model.User;

@Service("collabeditAuthProvider")
public class CollabEditAuthProvider extends
		AbstractUserDetailsAuthenticationProvider {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CollabEditAuthProvider.class);
	
	@Autowired
	private UserRepository userRepo;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	protected UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		//User user = authService.authenticate(username, (String) authentication.getCredentials());
		User user = userRepo.findByUsername(username);
		
		return user;
	}

}
