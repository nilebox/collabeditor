package ru.nilebox.collabedit.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import ru.nilebox.collabedit.service.auth.UserDetailsTool;

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
	private String createdBy;
	private Date created;
	private String modifiedBy;
	private Date modified;
	private Integer version = 1;

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Basic(fetch = FetchType.LAZY)
	@Column(name="contents", columnDefinition = "TEXT", unique = false, nullable = true)
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Column
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Column
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Column(columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	@PreUpdate
	@PrePersist
	void preUpdate() {
		User user = UserDetailsTool.getUserDetailsFromContext();
		if (user != null) {
			modifiedBy = user.getUsername();
		}

		modified = new Date();

		if (created == null) {
			created = modified;
			createdBy = modifiedBy;
		}
	}	
	
}
