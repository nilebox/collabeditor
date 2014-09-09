package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.dao.UserRepository;
import ru.nilebox.collabedit.model.User;

/**
 *
 * @author nile
 */
@Controller
@RequestMapping("/users/")
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
}
