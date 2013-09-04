@TypeDefs({
		@TypeDef(name = "CurrencyUnit", typeClass = PersistentCurrencyUnit.class),
		@TypeDef(name = "DateWithoutTZ", typeClass = PersistentLocalDate.class),
		@TypeDef(
				name = "EncryptedString",
				typeClass = EncryptedStringType.class,
				parameters = {
						@Parameter(name = "encryptorRegisteredName", value = "defaultStringEncryptor")
				}
		)
}) package com.cascaio.backend.v1.entity;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.jadira.usertype.moneyandcurrency.joda.PersistentCurrencyUnit;
import org.jasypt.hibernate4.type.EncryptedStringType;
