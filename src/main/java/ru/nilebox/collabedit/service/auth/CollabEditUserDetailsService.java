package ru.nilebox.collabedit.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nilebox.collabedit.dao.UserRepository;
import ru.nilebox.collabedit.model.User;

@Service
public class CollabEditUserDetailsService implements UserDetailsService {
	private String username;
	private String password;
	
	private static final Logger logger = LoggerFactory.getLogger(CollabEditUserDetailsService.class);
	
	@Autowired
	private UserRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		logger.info("Trying to authorize user with username {}", username);
		User user = authUserFromFileSettings(username);
			
		if (user == null){
			user = repo.findByUsername(username);
		}
		if(null != user){
			logger.info("User logged in as {} with role {}", user.getUsername(), user.getRole());
			return user;
		}
		
		logger.debug(String.format("Username %s not found (%s:%s)", username, this.username, this.password));
		throw new UsernameNotFoundException(String.format("Username %s not found", username));
	}

	private User authUserFromFileSettings(String username) {
		User u = null;
		if (username.equals(this.username)) {
			// try to load user from config to access with admin privileges
			u = new User(username, password, User.ROLE_ADMIN);
			u.setId(Long.MIN_VALUE);
			logger.info("User logged in as a default user with ADMIN privileges. {}", u);

		}
		return u;
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

}
