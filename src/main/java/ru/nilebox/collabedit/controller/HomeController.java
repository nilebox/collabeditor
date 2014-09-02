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
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Controller
public class HomeController {
	private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	DocumentRepository docRepo;
	
	@RequestMapping(value = "/home.html", method = RequestMethod.GET)
	public ModelAndView home(SecurityContextHolderAwareRequestWrapper request,
			Principal principal) {
		ModelAndView mav = new ModelAndView("home");
		
		//TODO: load only id+title, without contents
		Iterable<Document> documents = docRepo.findAll();
		mav.addObject("docs", documents);
		
		
		return mav;
	}	
}
