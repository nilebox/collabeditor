package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.model.CaretUpdate;
import ru.nilebox.collabedit.model.ClientMessage;
import ru.nilebox.collabedit.model.TitleUpdate;
import ru.nilebox.collabedit.operations.DocumentEditor;
import ru.nilebox.collabedit.operations.DocumentManagerRepository;
import ru.nilebox.collabedit.operations.OperationBatch;
import ru.nilebox.collabedit.transform.TransformationException;
import ru.nilebox.collabedit.transform.service.DocumentChangeNotification;
import ru.nilebox.collabedit.transform.service.DocumentChangeRequest;

/**
 *
 * @author nile
 */
@Controller
public class CollabController {
	private final static Logger logger = LoggerFactory.getLogger(CollabController.class);

	@Autowired
	DocumentManagerRepository documentManagerRepo;	
	
	private SimpMessagingTemplate template;

    @Autowired
    public CollabController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@MessageMapping("/operation")
	public void applyOperation(DocumentChangeRequest request, Principal principal) {
		logger.info("Received data: " + request);
		try {
			DocumentEditor documentManager =  documentManagerRepo.getDocumentManager(request.getDocumentId());
			OperationBatch batch = OperationBatch.fromDocumentChangeRequest(request);
			documentManager.applyBatch(request.getDocumentId(), batch);
			documentManager.updateClientCarets(request.getClientId(), principal.getName(), batch);
			DocumentChangeNotification notification = DocumentChangeNotification.create(request, batch, principal);
			template.convertAndSend("/topic/operation/" + request.getDocumentId(), notification);
		} catch (TransformationException ex) {
			logger.error("Error processing diff: " + request, ex);
		}
	}
	
	@MessageMapping("/title")
	public void updateTitle(TitleUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		DocumentEditor documentManager =  documentManagerRepo.getDocumentManager(update.getDocumentId());
		documentManager.applyTitle(update);
		template.convertAndSend("/topic/title/" + update.getDocumentId(), update);
	}
	
	@MessageMapping("/caret")
	public void updateCaret(CaretUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		update.setUsername(principal.getName());
		DocumentEditor documentManager =  documentManagerRepo.getDocumentManager(update.getDocumentId());
		documentManager.applyClientCaret(update);
		template.convertAndSend("/topic/caret/" + update.getDocumentId(), update);
	}
	
	@MessageMapping("/disconnect")
	public void clientDisconnect(ClientMessage client, Principal principal) {
		logger.info("Received data: " + client);
		client.setUsername(principal.getName());
		DocumentEditor documentManager =  documentManagerRepo.getDocumentManager(client.getDocumentId());
		documentManager.removeClient(client);
		template.convertAndSend("/topic/disconnect/" + client.getDocumentId(), client);
	}
	
}
