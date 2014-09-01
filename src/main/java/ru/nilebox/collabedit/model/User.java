package ru.nilebox.collabedit.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author nile
 */
@Entity(name="collab_user")
public class User implements Serializable, UserDetails {
	private static final long serialVersionUID = -4598702342454423933L;
	
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	Long id;
	String username;
	String password;
	String role;
	Date created = new Date();
	boolean enabled = true;
	Collection<GrantedAuthority> auths;
	
	public User(){}
	
	public User(String username, String password, String role){
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_generator")
	@SequenceGenerator(name = "user_id_seq_generator", sequenceName = "user_id_seq", allocationSize = 1)
//	@Column(columnDefinition = "serial")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Length(min=4, max=20) @Column(unique=true)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		
		List<GrantedAuthority> au = new ArrayList<GrantedAuthority>();
		if(null != auths){
			au.addAll(auths);
		}
		if(null != getRole()){
			au.add(new SimpleGrantedAuthority(getRole()));
		}
		return au;
	}
	
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	@Transient
	public Collection<GrantedAuthority> getAuths() {
		return auths;
	}

	public void setAuths(Collection<GrantedAuthority> auths) {
		this.auths = auths;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [role=").append(role).append(", id=").append(id)
				.append(", username=").append(username)
				.append(", created=").append(created)
				.append(", enabled=").append(enabled).append("]");
		return builder.toString();
	}
	
	
}
