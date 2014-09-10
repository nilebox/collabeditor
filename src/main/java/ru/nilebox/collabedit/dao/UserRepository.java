package ru.nilebox.collabedit.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.nilebox.collabedit.model.User;

/**
 * JPA repository for working with user storage
 * @author nile
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long> {
	
	public User findByUsername(String username);

}
