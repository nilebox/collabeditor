package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.DocumentInfo;

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
	
	private SimpMessagingTemplate template;

    @Autowired
    public DocumentController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@RequestMapping(value = "edit.html", method = RequestMethod.GET)
	public ModelAndView getDocument(@RequestParam(required=true) Long id, Principal principal) {

		ModelAndView mav = new ModelAndView("doc");
		
		Document document = docRepo.findOne(id);
		mav.addObject("doc", document);

		return mav;
	}
	
	@RequestMapping(value = "create.html", method = RequestMethod.GET)
	public String createDocument(Principal principal) {
		Document document = new Document();
		document = docRepo.save(document);

		return "redirect:/docs/edit.html?id=" + document.getId();
	}
	
}
