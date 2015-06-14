/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.antlr.normalization;

import org.hibernate.hql.model.TypeDescriptor;

/**
 * @author Steve Ebersole
 */
public interface FromElement {
	FromElementSpace getContainingSpace();
	String getAlias();
	TypeDescriptor getTypeDescriptor();
}
