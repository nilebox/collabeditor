package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.format.CaretUpdate;
import ru.nilebox.collabedit.format.ClientMessage;
import ru.nilebox.collabedit.format.TitleUpdate;
import ru.nilebox.collabedit.operations.DocumentEditor;
import ru.nilebox.collabedit.operations.DocumentEditorRepository;
import ru.nilebox.collabedit.operations.OperationBatch;
import ru.nilebox.collabedit.operations.TransformationException;
import ru.nilebox.collabedit.format.DocumentChangeNotification;
import ru.nilebox.collabedit.format.DocumentChangeRequest;

/**
 *
 * @author nile
 */
@Controller
public class CollaborationController {
	private final static Logger logger = LoggerFactory.getLogger(CollaborationController.class);

	@Autowired
	private DocumentEditorRepository documentEditorRepo;	
	
	private SimpMessagingTemplate template;

    @Autowired
    public CollaborationController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@MessageMapping("/operation")
	public void applyOperation(DocumentChangeRequest request, Principal principal) {
		logger.info("Received data: " + request);
		try {
			DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(request.getDocumentId());
			OperationBatch batch = OperationBatch.fromDocumentChangeRequest(request);
			documentEditor.applyBatch(request.getDocumentId(), batch);
			documentEditor.updateClientCarets(request.getClientId(), principal.getName(), batch);
			DocumentChangeNotification notification = DocumentChangeNotification.create(request, batch, principal);
			template.convertAndSend("/topic/operation/" + request.getDocumentId(), notification);
		} catch (TransformationException ex) {
			logger.error("Error processing diff: " + request, ex);
		}
	}
	
	@MessageMapping("/title")
	public void updateTitle(TitleUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(update.getDocumentId());
		documentEditor.applyTitle(update);
		template.convertAndSend("/topic/title/" + update.getDocumentId(), update);
	}
	
	@MessageMapping("/caret")
	public void updateCaret(CaretUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		update.setUsername(principal.getName());
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(update.getDocumentId());
		documentEditor.applyClientCaret(update);
		template.convertAndSend("/topic/caret/" + update.getDocumentId(), update);
	}
	
	@MessageMapping("/disconnect")
	public void clientDisconnect(ClientMessage client, Principal principal) {
		logger.info("Received data: " + client);
		client.setUsername(principal.getName());
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(client.getDocumentId());
		documentEditor.removeClient(client);
		template.convertAndSend("/topic/disconnect/" + client.getDocumentId(), client);
	}
	
}
