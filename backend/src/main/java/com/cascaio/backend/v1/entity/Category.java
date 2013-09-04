package com.cascaio.backend.v1.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 12:32 PM
 */
@Entity
public class Category extends NamedCascaioEntity {

	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "name")
	private List<Category> subCategories = new ArrayList<>();

	@ManyToOne(optional = true)
	private Category parent;

	protected Category() {
		// to make JPA happy
	}

	public Category(String name) {
		super(name);
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		if (null != this.parent) this.parent.subCategories.remove(this);

		this.parent = parent;
		this.parent.subCategories.add(this);
	}

	public void addSubCategory(Category subCategory) {
		subCategory.parent = this;
		this.subCategories.add(subCategory);
	}

	public List<Category> getSubCategories() {
		return Collections.unmodifiableList(subCategories);
	}
}
