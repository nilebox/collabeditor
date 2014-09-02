package ru.nilebox.collabedit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.DocumentInfo;

/**
 *
 * @author nile
 */
@Controller
public class CollabController {
	private final static Logger logger = LoggerFactory.getLogger(CollabController.class);

	@Autowired
	DocumentRepository docRepo;
	
	private SimpMessagingTemplate template;

    @Autowired
    public CollabController(SimpMessagingTemplate template) {
		this.template = template;
    }

	@MessageMapping("/collab")
    public void update(DocumentInfo info) throws Exception {
        Document document = docRepo.findOne(info.getId());
		//document.setTitle(info.getTitle());
		document.setContents(info.getContents());
		docRepo.save(document);
		template.convertAndSend("/topic/collab/" + info.getId(), info);
    }	
}
