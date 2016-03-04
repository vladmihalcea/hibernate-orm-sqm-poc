/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.orm.internal.mapping;

import org.hibernate.sqm.domain.Type;

/**
 * @author Steve Ebersole
 */
public class PluralAttributeIndex {
	// for now just model simple indexes/keys
	private final org.hibernate.type.Type type;
	private final Type sqmType;
	private final Value[] values;

	public PluralAttributeIndex(
			org.hibernate.type.Type type,
			Type sqmType,
			Value[] values) {
		this.type = type;
		this.sqmType = sqmType;
		this.values = values;
	}

	public org.hibernate.type.Type getType() {
		return type;
	}

	public Type getSqmType() {
		return sqmType;
	}

	public Value[] getValues() {
		return values;
	}
}