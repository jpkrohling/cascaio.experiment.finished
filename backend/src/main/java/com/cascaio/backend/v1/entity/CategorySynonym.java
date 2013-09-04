package com.cascaio.backend.v1.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 12:33 PM
 */
@Entity
public class CategorySynonym extends CascaioEntity {

	@ManyToOne
	@NotNull
	private Category category;

	@NotNull
	@Column(unique = true)
	private String synonym;

	protected CategorySynonym() {
	}

	public CategorySynonym(String id, Category category, String synonym) {
		super(id);
		this.category = category;
		this.synonym = synonym;
	}

	public CategorySynonym(Category category, String synonym) {
		this.category = category;
		this.synonym = synonym;
	}

	public String getSynonym() {
		return synonym;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
