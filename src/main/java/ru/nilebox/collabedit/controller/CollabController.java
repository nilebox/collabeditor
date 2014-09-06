package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.DocumentInfo;
import ru.nilebox.collabedit.model.TitleUpdate;
import ru.nilebox.collabedit.transform.OperationProcessor;
import ru.nilebox.collabedit.transform.Operation;
import ru.nilebox.collabedit.transform.TransformException;

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

	@MessageMapping("/collab")
    public void update(DocumentInfo info, Principal principal) throws Exception {
		logger.info("Received data: " + info);
        Document document = docRepo.findOne(info.getId());
		//document.setTitle(info.getTitle());
		document.setModifiedBy(principal.getName());
		document.setContents(info.getContents());
		docRepo.save(document);
		template.convertAndSend("/topic/collab/" + info.getId(), info);
    }
	
	@MessageMapping("/operation")
	public void applyOperation(Operation operation, Principal principal) {
		logger.info("Received data: " + operation);
		try {
			operationProcessor.applyOperation(operation);
			template.convertAndSend("/topic/operation/" + operation.getDocumentId(), operation);
		} catch (TransformException ex) {
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
