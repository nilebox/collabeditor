package ru.nilebox.collabedit.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Principal;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.dao.UserRepository;
import ru.nilebox.collabedit.model.User;

/**
 *
 * @author nile
 */
@Controller
@RequestMapping("/users/")
@SessionAttributes("userForm")
public class UserController {
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepo;

	@RequestMapping(value = "list.html", method = RequestMethod.GET)
	public ModelAndView getUsers(SecurityContextHolderAwareRequestWrapper request,
			Principal principal) {

		ModelAndView mav = new ModelAndView("users");
		
		Iterable<User> users = userRepo.findAll();
		mav.addObject("users", users);

		return mav;
	}
	
	@RequestMapping(value = "edit.html", method = RequestMethod.GET)
	public ModelAndView getProfile(@RequestParam(required=false) Long userId,
			@ModelAttribute("userForm") User user, SecurityContextHolderAwareRequestWrapper request, Principal principal) {

		ModelAndView mav = new ModelAndView("user");
		if(null != userId){
			if(Long.valueOf(-1L).equals(userId)){
				user = new User();
			} else {
				user = userRepo.findOne(userId);
			}
		}

		if((!request.isUserInRole(User.ROLE_ADMIN)) || null == userId ){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if(auth.getPrincipal() instanceof User){
				user = (User) auth.getPrincipal();
			}
		}

		logger.info("User retrieved {}", user);
		mav.addObject("userForm",user);

		return mav;
	}
	@RequestMapping(value = "edit.html", method = RequestMethod.POST)
	public ModelAndView editUser( @ModelAttribute("userForm") @Valid User user, BindingResult result, 
		SessionStatus status, SecurityContextHolderAwareRequestWrapper request, Principal principal) {
		logger.info("Trying to save {}", user);
		ModelAndView mav = new ModelAndView("user");
		User u = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth.getPrincipal() instanceof User){
			u = (User) auth.getPrincipal();
		}
		boolean emptyPassword = false;
		if(null != user && null != user.getId() ){
			if(!request.isUserInRole(User.ROLE_ADMIN)){
				user.setId(u.getId());
			}
			u = userRepo.findOne(user.getId());
			if(null != u){
				if(null == user.getPassword() || user.getPassword().trim().length() == 0){
					emptyPassword = true;
					user.setPassword(u.getPassword());
				}
			}
			//only admin has permissions to change basic fields
			if(!request.isUserInRole(User.ROLE_ADMIN) ){
					user.setUsername(u.getUsername());
					user.setRole(u.getRole());
			}
		}

		try {
			if (!emptyPassword){
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(user.getPassword().getBytes());

				String md5 = new BigInteger(1, md.digest()).toString(16); 
				// leading zero issue 
				while ( md5.length() < 32 ) {
					md5 = "0"+md5;
				}

				user.setPassword(md5);
			}
			user = userRepo.save(user);
			status.setComplete();
			mav.addObject("message","ok");
			logger.info("User {} saved changes in object {}", u, user);
		} catch (Exception e){
			logger.error("Failed", e);
		}
		logger.info("User retrieved {}", user);
		mav.addObject("userForm",user);

		return mav;
	}
	
	

	@ModelAttribute("userForm")
	public User getNew(){
		return new User();

	}	
}
