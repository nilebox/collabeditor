package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.dao.UserRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.User;

/**
 *
 * @author nile
 */
@Controller
@RequestMapping("/docs/")
public class DocumentController {
	private final static Logger logger = LoggerFactory.getLogger(DocumentController.class);

	@Autowired
	DocumentRepository docRepo;

	@RequestMapping(value = "list.html", method = RequestMethod.GET)
	public ModelAndView getDocuments(Principal principal) {

		ModelAndView mav = new ModelAndView("docs");
		
		//TODO: load only id+title, without contents
		Iterable<Document> documents = docRepo.findAll();
		mav.addObject("docs", documents);

		return mav;
	}
	
	@RequestMapping(value = "doc.html", method = RequestMethod.GET)
	public ModelAndView getDocument(@RequestParam(required=true) Long id, Principal principal) {

		ModelAndView mav = new ModelAndView("doc");
		
		Document document = docRepo.findOne(id);
		mav.addObject("doc", document);

		return mav;
	}
}
