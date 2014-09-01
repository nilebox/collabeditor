package ru.nilebox.collabedit.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author nile
 */
@Entity(name="collab_doc")
public class Document implements Serializable {
	private static final long serialVersionUID = 7106040624441944871L;
	
	Long id;
	String title;
	String contents;
	Date created = new Date();

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_id_seq_generator")
	@SequenceGenerator(name = "document_id_seq_generator", sequenceName = "document_id_seq", allocationSize = 1)	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="contents", columnDefinition = "TEXT not null", unique = false, nullable = true)
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
}
