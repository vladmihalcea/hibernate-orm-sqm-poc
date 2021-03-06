/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.persister.common.internal;

import org.hibernate.persister.common.spi.SqmTypeImplementor;
import org.hibernate.sqm.domain.AnyType;
import org.hibernate.sqm.domain.BasicType;
import org.hibernate.sqm.domain.Type;

/**
 * @author Steve Ebersole
 */
public class AnyTypeImpl implements AnyType, SqmTypeImplementor {
	private final org.hibernate.type.AnyType ormType;
	private final BasicType discriminatorType;
	private final Type identifierType;

	public AnyTypeImpl(org.hibernate.type.AnyType ormType, BasicType discriminatorType, Type identifierType) {
		this.ormType = ormType;
		this.discriminatorType = discriminatorType;
		this.identifierType = identifierType;
	}

	@Override
	public String getTypeName() {
		return "any";
	}

	@Override
	public org.hibernate.type.AnyType getOrmType() {
		return ormType;
	}

	@Override
	public BasicType getDiscriminatorType() {
		return discriminatorType;
	}

	@Override
	public Type getIdentifierType() {
		return identifierType;
	}
}
