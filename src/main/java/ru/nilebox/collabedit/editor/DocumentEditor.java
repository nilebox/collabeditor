package ru.nilebox.collabedit.editor;

import ru.nilebox.collabedit.editor.operations.OperationBatch;
import ru.nilebox.collabedit.messages.ClientInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.messages.CaretUpdate;
import ru.nilebox.collabedit.messages.ClientMessage;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.messages.TitleUpdate;

/**
 * Document editor with operations history and information about clients
 * @author nile
 */
public class DocumentEditor {

	private final Long documentId;
	private final DocumentRepository docRepo;
	private Document document;
	private final OperationBatchHistory history = new OperationBatchHistory();
	private ContentManager contentManager;
	private final Cache<String, ClientInfo> clients = CacheBuilder.newBuilder()
		.maximumSize(100).expireAfterAccess(5, TimeUnit.MINUTES).build();
	
	public DocumentEditor(Long documentId, DocumentRepository docRepo) {
		this.documentId = documentId;
		this.docRepo = docRepo;
	}

	public Long getDocumentId() {
		return documentId;
	}
	
	public void applyTitle(TitleUpdate update, Principal principal) {
		Document doc = getDocument();
		doc.setTitle(update.getTitle());
		doc.setModifiedBy(principal.getName());
		doc = docRepo.save(doc);
		this.document = doc;
	}
	
	public OperationBatch applyBatch(Long documentId, OperationBatch batch, Principal principal) throws TransformationException {
		String clientId = batch.getClientId();
		List<OperationBatch> diffBatches = history.getBatchesForDifference(batch.getBaseDocumentVersion());
		for (OperationBatch b : diffBatches) {
			// Check validity
			if (batch.getClientId().equals(b.getClientId()))
				throw new TransformationException("Found two conflicting batches with the same base version from client");
			// Transform B to B'
			batch = OperationTransformer.transformBatches(batch, b).getFirst();
		}
		
		Document doc = getDocument();
		ContentManager cm = getContentManager();
		cm.applyOperations(batch);

		batch.setBaseDocumentVersion(doc.getVersion());
		batch.setClientId(clientId);
		
		doc.setContents(cm.getContent());
		doc.setVersion(doc.getVersion() + 1);
		doc.setModifiedBy(principal.getName());
		doc = docRepo.save(doc);
		this.document = doc;
		
		// Save to history - may be needed for "merging" edits from multiple clients
		history.addBatch(batch);
		return batch;
	}
	
	public void updateClientCarets(String clientId, String username, OperationBatch batch) {
		setClientCaret(clientId, username, ContentManager.getRemoteCaret(batch));
		for(ClientInfo client : clients.asMap().values()) {
			if (client.getClientId().equals(clientId))
				continue;
			int newCaret = ContentManager.transformCaret(client.getCaretPosition(), batch);
			client.setCaretPosition(newCaret);
		}
	}
	
	public void applyClientCaret(CaretUpdate update) {
		setClientCaret(update.getClientId(), update.getUsername(), update.getCaretPosition());
	}
	
	private ClientInfo setClientCaret(String clientId, String username, int caretPosition) {
		ClientInfo client = getClient(clientId, username);
		client.setCaretPosition(caretPosition);
		client.setLastActivity(new Date());
		
		return client;
	}
	
	public void removeClient(ClientMessage message) {
		clients.invalidate(message.getClientId());
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
		ClientInfo client = clients.getIfPresent(clientId);
		if (client == null) {
			ClientInfo newClient = new ClientInfo(clientId, username);
			clients.put(clientId, newClient);
			client = newClient;
		}
		return client;
	}
	
	public Collection<ClientInfo> getClients() {
		return clients.asMap().values();
	}
	
}
