/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.type.basic;

import javax.persistence.AttributeConverter;

import org.hibernate.boot.spi.AttributeConverterDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * Designed to act as a key in the registry of basic type instances in {@link BasicTypeFactory}
 *
 * @author Steve Ebersole
 */
class RegistryKey {
	private final Class javaTypeClass;
	private final int jdbcCode;
	private final Class attributeConverterClass;

	static RegistryKey from(
			JavaTypeDescriptor javaTypeDescriptor,
			SqlTypeDescriptor sqlTypeDescriptor,
			Object converterReference) {
		Class converterClass = null;
		if ( converterReference != null ) {
			if ( converterReference instanceof AttributeConverterDescriptor ) {
				converterClass = ( (AttributeConverterDescriptor) converterReference ).getAttributeConverter().getClass();
			}
			else if ( converterReference instanceof AttributeConverter ) {
				converterClass = converterReference.getClass();
			}
		}
		return new RegistryKey(
				javaTypeDescriptor.getJavaTypeClass(),
				sqlTypeDescriptor.getSqlType(),
				converterClass
		);
	}

	private RegistryKey(Class javaTypeClass, int jdbcCode, Class attributeConverterClass) {
		assert javaTypeClass != null;

		this.javaTypeClass = javaTypeClass;
		this.jdbcCode = jdbcCode;
		this.attributeConverterClass = attributeConverterClass;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof RegistryKey ) ) {
			return false;
		}

		final RegistryKey that = (RegistryKey) o;
		return jdbcCode == that.jdbcCode
				&& javaTypeClass.equals( that.javaTypeClass )
				&& sameConversion( attributeConverterClass, that.attributeConverterClass );
	}

	private boolean sameConversion(Class mine, Class yours) {
		if ( mine == null ) {
			return yours == null;
		}
		else {
			return mine.equals( yours );
		}
	}

	@Override
	public int hashCode() {
		int result = javaTypeClass.hashCode();
		result = 31 * result + jdbcCode;
		result = 31 * result + ( attributeConverterClass != null ? attributeConverterClass.hashCode() : 0 );
		return result;
	}
}
