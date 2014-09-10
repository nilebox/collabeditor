package ru.nilebox.collabedit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;

/**
 * Main controller (index)
 * @author nile
 */
@Controller
public class HomeController {
	private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private DocumentRepository docRepo;
	
	@RequestMapping(value = "*", method = RequestMethod.GET)
	public String index() {
		return "redirect:/home.html";
	}	
	
	@RequestMapping(value = "/home.html", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView("home");
		
		Iterable<Document> documents = docRepo.getSortedDocuments();
		mav.addObject("docs", documents);
		
		return mav;
	}	
}
