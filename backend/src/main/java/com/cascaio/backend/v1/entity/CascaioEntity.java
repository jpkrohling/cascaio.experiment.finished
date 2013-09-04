package com.cascaio.backend.v1.entity;

import org.joda.time.DateTime;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.util.UUID;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 10:23 AM
 */
@MappedSuperclass
public abstract class CascaioEntity {
	@Id
	private String id = UUID.randomUUID().toString();

	private DateTime createdAt = new DateTime();
	private DateTime updatedAt = new DateTime();

	protected CascaioEntity() {
	}

	public CascaioEntity(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public DateTime getUpdatedAt() {
		return updatedAt;
	}

	@PreUpdate
	public void setUpdatedAt() {
		this.updatedAt = new DateTime();
	}

	/**
	 * We may want to override this on classes with collections that are important enough to make the object
	 * be understood as modified. For instance, a new transaction into an account means that the account was
	 * last modified when this transaction was added.
	 * @return DateTime representing the last time that the object was changed.
	 */
	public DateTime getLastModifiedAt() {
		return this.getUpdatedAt();
	}
}
