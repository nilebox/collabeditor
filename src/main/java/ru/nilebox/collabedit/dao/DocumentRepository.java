package ru.nilebox.collabedit.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Repository
public interface DocumentRepository extends CrudRepository<Document,Long> {

	@Query("SELECT d FROM collab_doc d order by d.modified desc")
	public List<Document> getSortedDocuments();
	
}
