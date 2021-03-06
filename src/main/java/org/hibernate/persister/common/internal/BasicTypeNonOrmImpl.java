/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.persister.common.internal;

import org.hibernate.persister.common.spi.SqmTypeImplementor;
import org.hibernate.sqm.domain.BasicType;
import org.hibernate.type.Type;

/**
 * @author Steve Ebersole
 */
public class BasicTypeNonOrmImpl<T> implements BasicType<T>, SqmTypeImplementor {
	private final Class<T> javaType;

	public BasicTypeNonOrmImpl(Class<T> javaType) {
		this.javaType = javaType;
	}

	@Override
	public Type getOrmType() {
		return null;
	}

	@Override
	public Class<T> getJavaType() {
		return javaType;
	}

	@Override
	public String getTypeName() {
		return javaType.getName();
	}
}
