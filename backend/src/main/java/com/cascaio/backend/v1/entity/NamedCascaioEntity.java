package com.cascaio.backend.v1.entity;

import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 10:23 AM
 */
@MappedSuperclass
public abstract class NamedCascaioEntity extends CascaioEntity {

	private String name;

	protected NamedCascaioEntity() {
	}

	public NamedCascaioEntity(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	public NamedCascaioEntity(String id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
