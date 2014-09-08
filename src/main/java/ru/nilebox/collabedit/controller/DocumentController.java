package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.operations.DocumentEditor;
import ru.nilebox.collabedit.operations.DocumentManagerRepository;

/**
 *
 * @author nile
 */
@Controller
@RequestMapping("/docs/")
public class DocumentController {
	private final static Logger logger = LoggerFactory.getLogger(DocumentController.class);

	@Autowired
	DocumentManagerRepository documentManagerRepo;	
	
	private SimpMessagingTemplate template;

    @Autowired
    public DocumentController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@RequestMapping(value = "edit.html", method = RequestMethod.GET)
	public ModelAndView editDocument(@RequestParam(required=true) Long id, Principal principal) {

		ModelAndView mav = new ModelAndView("doc");

		DocumentEditor documentManager = documentManagerRepo.getDocumentManager(id);
		mav.addObject("doc", documentManager.getDocument());
		mav.addObject("clients", documentManager.getClients());
		mav.addObject("principal", principal);

		return mav;
	}
	
	@RequestMapping(value = "create.html", method = RequestMethod.GET)
	public String createDocument(Principal principal) {
		DocumentEditor documentManager = documentManagerRepo.createDocument();

		return "redirect:/docs/edit.html?id=" + documentManager.getDocumentId();
	}
	
}
