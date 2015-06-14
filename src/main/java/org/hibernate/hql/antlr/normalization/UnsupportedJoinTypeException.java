/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.antlr.normalization;

import org.hibernate.hql.SemanticException;

/**
 * @author Steve Ebersole
 */
public class UnsupportedJoinTypeException extends SemanticException {
	public UnsupportedJoinTypeException(String message) {
		super( message );
	}

	public UnsupportedJoinTypeException(String message, Throwable cause) {
		super( message, cause );
	}
}
