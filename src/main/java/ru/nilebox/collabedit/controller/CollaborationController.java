package ru.nilebox.collabedit.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.messages.CaretUpdate;
import ru.nilebox.collabedit.messages.ClientMessage;
import ru.nilebox.collabedit.messages.TitleUpdate;
import ru.nilebox.collabedit.editor.DocumentEditor;
import ru.nilebox.collabedit.editor.DocumentEditorRepository;
import ru.nilebox.collabedit.editor.operations.OperationBatch;
import ru.nilebox.collabedit.editor.TransformationException;
import ru.nilebox.collabedit.messages.DocumentChangeNotification;
import ru.nilebox.collabedit.messages.DocumentChangeRequest;

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
			synchronized(documentEditor) {
				OperationBatch batch = OperationBatch.fromDocumentChangeRequest(request);
				batch = documentEditor.applyBatch(request.getDocumentId(), batch, principal);
				documentEditor.updateClientCarets(request.getClientId(), principal.getName(), batch);
				DocumentChangeNotification notification = DocumentChangeNotification.create(request, batch, principal);
				template.convertAndSend("/topic/operation/" + request.getDocumentId(), notification);
			}
		} catch (TransformationException ex) {
			logger.error("Error processing diff: " + request, ex);
		}
	}
	
	@MessageMapping("/title")
	public void updateTitle(TitleUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(update.getDocumentId());
		synchronized(documentEditor) {
			documentEditor.applyTitle(update, principal);
			template.convertAndSend("/topic/title/" + update.getDocumentId(), update);
		}
	}
	
	@MessageMapping("/caret")
	public void updateCaret(CaretUpdate update, Principal principal) {
		logger.info("Received data: " + update);
		update.setUsername(principal.getName());
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(update.getDocumentId());
		synchronized(documentEditor) {
			documentEditor.applyClientCaret(update);
			template.convertAndSend("/topic/caret/" + update.getDocumentId(), update);
		}
	}
	
	@MessageMapping("/disconnect")
	public void clientDisconnect(ClientMessage client, Principal principal) {
		logger.info("Received data: " + client);
		client.setUsername(principal.getName());
		DocumentEditor documentEditor =  documentEditorRepo.getDocumentEditor(client.getDocumentId());
		synchronized(documentEditor) {
			documentEditor.removeClient(client);
			template.convertAndSend("/topic/disconnect/" + client.getDocumentId(), client);
		}
	}
	
}
