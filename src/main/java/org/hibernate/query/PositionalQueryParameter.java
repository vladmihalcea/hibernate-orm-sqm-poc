/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query;

import org.hibernate.Incubating;

/**
 * @author Steve Ebersole
 */
@Incubating
public class PositionalQueryParameter implements QueryParameter {
	private final int position;

	public PositionalQueryParameter(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
