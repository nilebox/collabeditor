package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.nilebox.collabedit.editor.DocumentEditor;
import ru.nilebox.collabedit.editor.DocumentEditorRepository;

/**
 * Controller for working with documents
 * @author nile
 */
@Controller
@RequestMapping("/docs/")
public class DocumentController {
	private final static Logger logger = LoggerFactory.getLogger(DocumentController.class);

	@Autowired
	private DocumentEditorRepository documentEditorRepo;	
	
	@RequestMapping(value = "edit.html", method = RequestMethod.GET)
	public ModelAndView editDocument(@RequestParam(required=true) Long id, Principal principal) {

		ModelAndView mav = new ModelAndView("doc");

		DocumentEditor documentEditor = documentEditorRepo.getDocumentEditor(id);
		mav.addObject("doc", documentEditor.getDocument());
		mav.addObject("clients", documentEditor.getClients());
		mav.addObject("principal", principal);

		return mav;
	}
	
	@RequestMapping(value = "create.html", method = RequestMethod.GET)
	public String createDocument(Principal principal) {
		DocumentEditor documentManager = documentEditorRepo.createDocument();

		return "redirect:/docs/edit.html?id=" + documentManager.getDocumentId();
	}
	
}
