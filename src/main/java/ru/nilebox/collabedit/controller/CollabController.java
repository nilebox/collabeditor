package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.TitleUpdate;
import ru.nilebox.collabedit.old.transform.OperationProcessor;
import ru.nilebox.collabedit.old.transform.OperationOld;
import ru.nilebox.collabedit.transform.TransformationException;

/**
 *
 * @author nile
 */
@Controller
public class CollabController {
	private final static Logger logger = LoggerFactory.getLogger(CollabController.class);

	@Autowired
	DocumentRepository docRepo;
	
	@Autowired
	OperationProcessor operationProcessor;	
	
	private SimpMessagingTemplate template;

    @Autowired
    public CollabController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@MessageMapping("/operation")
	public void applyOperation(OperationOld operation, Principal principal) {
		logger.info("Received data: " + operation);
		try {
			operationProcessor.applyOperation(operation);
			operation.setUsername(principal.getName());
			template.convertAndSend("/topic/operation/" + operation.getDocumentId(), operation);
		} catch (TransformationException ex) {
			logger.error("Error processing diff: " + operation, ex);
		}
	}
	
	@MessageMapping("/title")
	public void updateTitle(TitleUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		operationProcessor.applyTitle(update);
		template.convertAndSend("/topic/title/" + update.getDocumentId(), update);
	}
	
}
