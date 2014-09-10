package ru.nilebox.collabedit.editor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Service
public class DocumentEditorRepository {
	private final static Logger logger = LoggerFactory.getLogger(DocumentEditorRepository.class);
	
	@Autowired
	private DocumentRepository docRepo;
	
	private final LoadingCache<Long, DocumentEditor> cache = CacheBuilder.newBuilder()
		.maximumSize(100).expireAfterAccess(5, TimeUnit.MINUTES)
		.build(new CacheLoader<Long, DocumentEditor>() {
			@Override
			public DocumentEditor load(Long documentId) {
				return createDocumentEditor(documentId);
			}
		});

	public DocumentEditor createDocument() {
		Document document = new Document();
		document = docRepo.save(document);
		DocumentEditor manager = new DocumentEditor(document.getId(), docRepo);
		cache.put(document.getId(), manager);
		return manager;
	}

	public DocumentEditor createDocumentEditor(Long documentId) {
		return new DocumentEditor(documentId, docRepo);
	}

	public synchronized DocumentEditor getDocumentEditor(Long documentId) {
		try {
			return cache.get(documentId);
		} catch (ExecutionException e) {
			logger.error("Error while retrieving document editor", e);
			return null;
		}
	}
}
