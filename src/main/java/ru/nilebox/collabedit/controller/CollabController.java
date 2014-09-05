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
import ru.nilebox.collabedit.transform.Diff;
import ru.nilebox.collabedit.transform.DiffProcessor;
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
	DiffProcessor diffProcessor;	
	
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
	
	@MessageMapping("/diff")
	public void apply(Diff diff, Principal principal) {
		logger.info("Received data: " + diff);
		try {
			diffProcessor.applyDiff(diff);
			template.convertAndSend("/topic/diff/" + diff.getId(), diff);
			
			//TODO: for testing purposes only
			//sendDocInfo(diff.getId());
		} catch (TransformException ex) {
			logger.error("Error processing diff: " + diff, ex);
		}
	}
	
	private void sendDocInfo(Long id) {
		Document doc = diffProcessor.getDocument(id);
		DocumentInfo info = new DocumentInfo();
		info.setId(id);
		info.setContents(doc.getContents());
		template.convertAndSend("/topic/collab/" + info.getId(), info);
	}
}
