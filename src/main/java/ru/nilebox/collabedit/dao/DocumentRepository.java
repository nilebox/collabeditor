package ru.nilebox.collabedit.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Repository
public interface DocumentRepository extends CrudRepository<Document,Long> {
	
}
