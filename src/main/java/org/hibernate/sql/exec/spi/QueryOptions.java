/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.exec.spi;

import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;

/**
 * @author Steve Ebersole
 */
public interface QueryOptions {
	Integer getTimeout();
	FlushMode getFlushMode();
	String getComment();
	List<String> getSqlHints();

	Limit getLimit();
	LockOptions getLockOptions();
	Integer getFetchSize();
	Boolean isReadOnly();
	CacheMode getCacheMode();
	Boolean isResultCachingEnabled();
	String getResultCacheRegionName();
}
