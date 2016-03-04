/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.orm.internal.mapping;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.boot.MetadataSources;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.sql.ast.QuerySpec;
import org.hibernate.sql.ast.from.EntityTableGroup;
import org.hibernate.sql.gen.BaseUnitTest;
import org.hibernate.sql.gen.internal.FromClauseIndex;
import org.hibernate.sql.gen.internal.SqlAliasBaseManager;
import org.hibernate.sql.orm.internal.sqm.model.EntityTypeImpl;
import org.hibernate.sqm.query.SelectStatement;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Steve Ebersole
 */
public class JoinedEntitySimpleTest extends BaseUnitTest {
	@Test
	public void testSingleSpaceBase() {
		SelectStatement sqm = (SelectStatement) interpret( "from JoinedEntityBase" );

		final EntityTypeImpl entityTypeDescriptor =
				(EntityTypeImpl) getConsumerContext().getDomainMetamodel().resolveEntityType( "JoinedEntityBase" );
		final ImprovedEntityPersister improvedEntityPersister = entityTypeDescriptor.getPersister();
		assertThat( improvedEntityPersister.getEntityPersister(), instanceOf( JoinedSubclassEntityPersister.class ) );

		// interpreter set up
		final QuerySpec querySpec = new QuerySpec();
		final SqlAliasBaseManager aliasBaseManager = new SqlAliasBaseManager();
		final FromClauseIndex fromClauseIndex = new FromClauseIndex();

		final EntityTableGroup result = improvedEntityPersister.getEntityTableGroup(
				sqm.getQuerySpec().getFromClause().getFromElementSpaces().get( 0 ).getRoot(),
				querySpec.getFromClause().makeTableSpace(),
				aliasBaseManager,
				fromClauseIndex
		);
		assertThat( result, notNullValue() );
		assertThat( result.getAliasBase(), equalTo( "j1" ) );

		assertThat( result.getRootTable(), notNullValue() );
		assertThat( result.getRootTable().getTableReference(), instanceOf( PhysicalTable.class ) );
		final PhysicalTable tableSpec = (PhysicalTable) result.getRootTable().getTableReference();
		assertThat( tableSpec.getTableName(), equalTo( "joined_entity_base" ) );
		assertThat( result.getRootTable().getIdentificationVariable(), equalTo( "j1_0" ) );

		assertThat( result.getTableJoins().size(), equalTo( 2 ) );

		assertThat(
				result.getTableJoins().get( 0 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table firstSubclassTable = result.getTableJoins().get( 0 ).getJoinedTable();
		assertThat( firstSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_branch" ) );
		assertThat( firstSubclassTable.getIdentificationVariable(), equalTo( "j1_1" ) );

		assertThat(
				result.getTableJoins().get( 1 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table secondSubclassTable = result.getTableJoins().get( 1 ).getJoinedTable();
		assertThat( secondSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_leaf" ) );
		assertThat( secondSubclassTable.getIdentificationVariable(), equalTo( "j1_2" ) );
	}

	@Test
	public void testSingleSpaceBranch() {
		SelectStatement sqm = (SelectStatement) interpret( "from JoinedEntityBranch" );

		final EntityTypeImpl entityTypeDescriptor =
				(EntityTypeImpl) getConsumerContext().getDomainMetamodel().resolveEntityType( "JoinedEntityBranch" );
		final ImprovedEntityPersister improvedEntityPersister = entityTypeDescriptor.getPersister();
		assertThat( improvedEntityPersister.getEntityPersister(), instanceOf( JoinedSubclassEntityPersister.class ) );

		// interpreter set up
		final QuerySpec querySpec = new QuerySpec();
		final SqlAliasBaseManager aliasBaseManager = new SqlAliasBaseManager();
		final FromClauseIndex fromClauseIndex = new FromClauseIndex();

		final EntityTableGroup result = improvedEntityPersister.getEntityTableGroup(
				sqm.getQuerySpec().getFromClause().getFromElementSpaces().get( 0 ).getRoot(),
				querySpec.getFromClause().makeTableSpace(),
				aliasBaseManager,
				fromClauseIndex
		);
		assertThat( result, notNullValue() );
		assertThat( result.getAliasBase(), equalTo( "j1" ) );

		assertThat( result.getRootTable(), notNullValue() );
		assertThat( result.getRootTable().getTableReference(), instanceOf( PhysicalTable.class ) );
		final org.hibernate.sql.ast.from.Table tableSpec = result.getRootTable();
		assertThat( tableSpec.getTableReference().getTableExpression(), equalTo( "joined_entity_branch" ) );
		assertThat( tableSpec.getIdentificationVariable(), equalTo( "j1_0" ) );

		assertThat( result.getTableJoins().size(), equalTo( 2 ) );

		assertThat(
				result.getTableJoins().get( 0 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table firstSubclassTable = result.getTableJoins().get( 0 ).getJoinedTable();
		assertThat( firstSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_base" ) );
		assertThat( firstSubclassTable.getIdentificationVariable(), equalTo( "j1_1" ) );

		assertThat(
				result.getTableJoins().get( 1 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table secondSubclassTable = result.getTableJoins().get( 1 ).getJoinedTable();
		assertThat( secondSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_leaf" ) );
		assertThat( secondSubclassTable.getIdentificationVariable(), equalTo( "j1_2" ) );
	}

	@Test
	public void testSingleSpaceLeaf() {
		SelectStatement sqm = (SelectStatement) interpret( "from JoinedEntityLeaf" );

		final EntityTypeImpl entityTypeDescriptor =
				(EntityTypeImpl) getConsumerContext().getDomainMetamodel().resolveEntityType( "JoinedEntityLeaf" );
		final ImprovedEntityPersister improvedEntityPersister = entityTypeDescriptor.getPersister();
		assertThat( improvedEntityPersister.getEntityPersister(), instanceOf( JoinedSubclassEntityPersister.class ) );

		// interpreter set up
		final QuerySpec querySpec = new QuerySpec();
		final SqlAliasBaseManager aliasBaseManager = new SqlAliasBaseManager();
		final FromClauseIndex fromClauseIndex = new FromClauseIndex();

		final EntityTableGroup result = improvedEntityPersister.getEntityTableGroup(
				sqm.getQuerySpec().getFromClause().getFromElementSpaces().get( 0 ).getRoot(),
				querySpec.getFromClause().makeTableSpace(),
				aliasBaseManager,
				fromClauseIndex
		);
		assertThat( result, notNullValue() );
		assertThat( result.getAliasBase(), equalTo( "j1" ) );

		assertThat( result.getRootTable(), notNullValue() );
		assertThat( result.getRootTable().getTableReference(), instanceOf( PhysicalTable.class ) );
		final org.hibernate.sql.ast.from.Table tableSpec = result.getRootTable();
		assertThat( tableSpec.getTableReference().getTableExpression(), equalTo( "joined_entity_leaf" ) );
		assertThat( tableSpec.getIdentificationVariable(), equalTo( "j1_0" ) );

		assertThat( result.getTableJoins().size(), equalTo( 2 ) );

		assertThat(
				result.getTableJoins().get( 0 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table firstSubclassTable = result.getTableJoins().get( 0 ).getJoinedTable();
		assertThat( firstSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_branch" ) );
		assertThat( firstSubclassTable.getIdentificationVariable(), equalTo( "j1_1" ) );

		assertThat(
				result.getTableJoins().get( 1 ).getJoinedTable().getTableReference(),
				instanceOf( PhysicalTable.class )
		);
		final org.hibernate.sql.ast.from.Table secondSubclassTable = result.getTableJoins().get( 1 ).getJoinedTable();
		assertThat( secondSubclassTable.getTableReference().getTableExpression(), equalTo( "joined_entity_base" ) );
		assertThat( secondSubclassTable.getIdentificationVariable(), equalTo( "j1_2" ) );
	}


	@Override
	protected void applyMetadataSources(MetadataSources metadataSources) {
		metadataSources.addAnnotatedClass( JoinedEntityBase.class );
		metadataSources.addAnnotatedClass( JoinedEntityBranch.class );
		metadataSources.addAnnotatedClass( JoinedEntityLeaf.class );
	}

	@Entity( name = "JoinedEntityBase" )
	@Table( name = "joined_entity_base" )
	@Inheritance( strategy = InheritanceType.JOINED )
	public static class JoinedEntityBase {
		@Id
		public Integer id;
		public String name;
		public String description;
	}

	@Entity( name = "JoinedEntityBranch" )
	@Table( name = "joined_entity_branch" )
	public static class JoinedEntityBranch extends JoinedEntityBase {
		public String branchSpecificState;
	}

	@Entity( name = "JoinedEntityLeaf" )
	@Table( name = "joined_entity_leaf" )
	public static class JoinedEntityLeaf extends JoinedEntityBranch {
		public String leafSpecificState;
	}
}