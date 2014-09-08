package ru.nilebox.collabedit.operations;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.TitleUpdate;
import ru.nilebox.collabedit.old.transform.OperationHistory;
import ru.nilebox.collabedit.transform.ContentManager;
import ru.nilebox.collabedit.transform.OperationTransformer;
import ru.nilebox.collabedit.transform.OperationBatchHistory;
import ru.nilebox.collabedit.transform.TransformationException;

/**
 *
 * @author nile
 */
public class DocumentEditor {

	private final Long documentId;
	private final DocumentRepository docRepo;
	private Document document;
	private final OperationBatchHistory history = new OperationBatchHistory();
	private ContentManager contentManager;
	private final ConcurrentMap<String, ClientInfo> clients = new ConcurrentHashMap<String, ClientInfo>();
	
	public DocumentEditor(Long documentId, DocumentRepository docRepo) {
		this.documentId = documentId;
		this.docRepo = docRepo;
	}

	public Long getDocumentId() {
		return documentId;
	}
	
	public void applyTitle(TitleUpdate update) {
		Document doc = getDocument();
		synchronized(this) {
			doc.setTitle(update.getTitle());
			doc = docRepo.save(doc);
			this.document = doc;
		}
	}
	
	public void applyBatch(Long documentId, OperationBatch batch) throws TransformationException {
		List<OperationBatch> diffBatches = history.getBatchesForDifference(batch.getBaseDocumentVersion());
		for (OperationBatch b : diffBatches) {
			// transform B to B'
			batch = OperationTransformer.transformBatches(batch, b).getFirst();
		}
		
		ContentManager contentManager = getContentManager();
		contentManager.applyOperations(batch);
		
		Document doc = getDocument();
		synchronized(this) {
			//TODO: bufferize in memory, flush to database by timeout?
			doc.setContents(contentManager.getContent());
			doc.setVersion(doc.getVersion() + 1);
			doc = docRepo.save(doc);
			this.document = doc;
			batch.setBaseDocumentVersion(doc.getVersion() - 1);
		}
		
		// add to history - may be needed for "merging" edits from multiple clients
		history.addBatch(batch);
	}
	
	public void updateClientCarets(String clientId, String username, OperationBatch batch) {
		setClientCaret(clientId, username, ContentManager.getRemoteCaret(batch));
		for(ClientInfo client : clients.values()) {
			if (client.getClientId().equals(clientId))
				continue;
			int newCaret = ContentManager.transformCaret(client.getCaretPosition(), batch);
			client.setCaretPosition(newCaret);
		}
	}
	
	public ClientInfo setClientCaret(String clientId, String username, int caretPosition) {
		ClientInfo client = getClient(clientId, username);
		client.setCaretPosition(caretPosition);
		client.setLastActivity(new Date());
		
		return client;
	}
	
	public Document getDocument() {
		if (document == null) {
			document = docRepo.findOne(documentId);
		}
		return document;
	}	
	
	private ContentManager getContentManager() {
		if (contentManager == null) {
			Document doc = getDocument();
			contentManager = new ContentManager(doc.getContents());
		}
		return contentManager;
	}
	
	public ClientInfo getClient(String clientId, String username) {
		ClientInfo client = clients.get(clientId);
		if (client == null) {
			ClientInfo newClient = new ClientInfo(clientId, username);
			client = clients.putIfAbsent(clientId, newClient);
			if (client == null)
				client = newClient;
		}
		return client;
	}
	
	public Collection<ClientInfo> getClients() {
		return clients.values();
	}
	
}
