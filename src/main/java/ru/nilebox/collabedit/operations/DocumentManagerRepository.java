package ru.nilebox.collabedit.operations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Service
public class DocumentManagerRepository {
	
	@Autowired
	DocumentRepository docRepo;
	
	private ConcurrentMap<Long, DocumentEditor> managers = new ConcurrentHashMap<Long, DocumentEditor>();
	
	public DocumentEditor createDocument() {
		Document document = new Document();
		document = docRepo.save(document);
		DocumentEditor manager = new DocumentEditor(document.getId(), docRepo);
		managers.put(document.getId(), manager);
		return manager;
	}
	
	public DocumentEditor getDocumentManager(Long documentId) {
		DocumentEditor manager = managers.get(documentId);
		if (manager == null) {
			DocumentEditor newManager = new DocumentEditor(documentId, docRepo);
			manager = managers.putIfAbsent(documentId, newManager);
			if (manager == null)
				manager = newManager;
		}
		return manager;
	}	

}
