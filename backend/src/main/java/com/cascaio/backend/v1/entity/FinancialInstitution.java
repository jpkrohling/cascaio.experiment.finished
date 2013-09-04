package com.cascaio.backend.v1.entity;

import com.cascaio.backend.v1.control.validation.BIC;
import com.cascaio.backend.v1.control.validation.Bankleitzahl;
import com.cascaio.backend.v1.control.validation.CNPJ;
import com.cascaio.backend.v1.control.validation.Country;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 10:23 AM
 */
@Entity
public class FinancialInstitution extends NamedCascaioEntity {

	private String codCompensacao; // Brazilian Codigo de Compensacao

	@NotNull
	@CNPJ
	private String cnpj; // Brazilian CNPJ

	@NotNull
	@Bankleitzahl
	private String bankleitzahl; // German BLZ

	@NotNull
	@BIC
	private String bic; // as per ISO 9362

	@NotNull
	@Country
	private String country; // 2 chars

	// JPA happy
	protected FinancialInstitution() {
	}

	public FinancialInstitution(String name, String country) {
		super(name);
		this.country = country;
	}

	public FinancialInstitution(String id, String name, String country) {
		super(id, name);
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(String bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getCodCompensacao() {
		return codCompensacao;
	}

	public void setCodCompensacao(String codCompensacao) {
		this.codCompensacao = codCompensacao;
	}
}
