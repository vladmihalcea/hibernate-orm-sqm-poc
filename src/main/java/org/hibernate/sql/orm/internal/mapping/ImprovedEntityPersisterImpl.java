/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.orm.internal.mapping;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.ast.from.EntityTableGroup;
import org.hibernate.sql.ast.from.TableSpace;
import org.hibernate.sql.ast.from.Table;
import org.hibernate.sql.ast.from.TableJoin;
import org.hibernate.sql.gen.NotYetImplementedException;
import org.hibernate.sql.gen.internal.FromClauseIndex;
import org.hibernate.sql.gen.internal.SqlAliasBaseManager;
import org.hibernate.sql.orm.internal.sqm.model.DomainMetamodelImpl;
import org.hibernate.sql.orm.internal.sqm.model.PseudoIdAttributeImpl;
import org.hibernate.sqm.domain.Attribute;
import org.hibernate.sqm.domain.EntityType;
import org.hibernate.sqm.domain.IdentifiableType;
import org.hibernate.sqm.domain.IdentifierDescriptor;
import org.hibernate.sqm.domain.ManagedType;
import org.hibernate.sqm.domain.SingularAttribute;
import org.hibernate.sqm.domain.Type;
import org.hibernate.sqm.query.JoinType;
import org.hibernate.sqm.query.from.FromElement;
import org.hibernate.type.CollectionType;

/**
 * @author Steve Ebersole
 */
public class ImprovedEntityPersisterImpl implements ImprovedEntityPersister, EntityType {
	private final DatabaseModel databaseModel;
	private final DomainMetamodelImpl domainMetamodel;
	private final EntityPersister persister;
	private final Queryable queryable;

	private final AbstractTable[] tables;

	private final Map<String, AbstractAttributeImpl> attributeMap = new HashMap<String, AbstractAttributeImpl>();

	public ImprovedEntityPersisterImpl(DatabaseModel databaseModel, DomainMetamodelImpl domainMetamodel, EntityPersister persister) {
		this.databaseModel = databaseModel;
		this.domainMetamodel = domainMetamodel;
		this.persister = persister;
		this.queryable = (Queryable) persister;

		// for now we treat super, self and sub attributes here just as EntityPersister does
		// ultimately would be better to split that across the specific persister impls and link them imo
		final int subclassTableCount = Helper.INSTANCE.extractSubclassTableCount( persister );
		this.tables = new AbstractTable[subclassTableCount];

		tables[0] = makeTableReference( databaseModel, queryable.getSubclassTableName( 0 ) );
		for ( int i = 1; i < subclassTableCount; i++ ) {
			tables[i] = makeTableReference( databaseModel, queryable.getSubclassTableName( i ) );
		}

		// todo : attributes need to be built in a second phase after all entity persisters are available, just like we do for the "walking spi"
		// for now just build them...
		afterInit( databaseModel, domainMetamodel );

	}

	private void afterInit(DatabaseModel databaseModel, DomainMetamodelImpl domainMetamodel) {

		// todo : deal with ids too

		final OuterJoinLoadable ojlPersister = (OuterJoinLoadable) persister;

		final int fullAttributeCount = ( ojlPersister ).countSubclassProperties();
		for ( int attributeNumber = 0; attributeNumber < fullAttributeCount; attributeNumber++ ) {
			final org.hibernate.type.Type attributeType = ojlPersister.getSubclassPropertyType( attributeNumber );

			final AbstractTable containingTable = tables[ Helper.INSTANCE.getSubclassPropertyTableNumber( persister, attributeNumber ) ];
			final String [] columns = Helper.INSTANCE.getSubclassPropertyColumnExpressions( persister, attributeNumber );
			final String [] formulas = Helper.INSTANCE.getSubclassPropertyFormulaExpressions( persister, attributeNumber );
			final Value[] values = Helper.makeValues( containingTable, attributeType, columns, formulas );

			final AbstractAttributeImpl attribute;
			if ( attributeType.isCollectionType() ) {
				attribute = buildPluralAttribute(
						databaseModel,
						domainMetamodel,
						ojlPersister.getSubclassPropertyName( attributeNumber ),
						attributeType,
						values
				);
			}
			else {

			}
		}
	}

	private AbstractTable makeTableReference(DatabaseModel databaseModel, String tableExpression) {
		// fugly, but when moved into persister we would know from mapping metamodel which type.
		if ( tableExpression.trim().startsWith( "select" ) ) {
			return databaseModel.createDerivedTable( tableExpression );
		}
		else {
			return databaseModel.findOrCreatePhysicalTable( tableExpression );
		}
	}

	private AbstractAttributeImpl buildPluralAttribute(
			DatabaseModel databaseModel,
			DomainMetamodelImpl domainMetamodel,
			String subclassPropertyName,
			org.hibernate.type.Type attributeType,
			Value[] values) {
		final CollectionType collectionType = (CollectionType) attributeType;
		final CollectionPersister collectionPersister = domainMetamodel.getSessionFactory().getCollectionPersister( collectionType.getRole() );

		return new ImprovedCollectionPersisterImpl(
				databaseModel,
				domainMetamodel,
				this,
				subclassPropertyName,
				collectionPersister,
				values
		);
	}

	@Override
	public EntityPersister getEntityPersister() {
		return persister;
	}

	@Override
	public EntityTableGroup getEntityTableGroup(
			FromElement fromElement,
			TableSpace tableSpace,
			SqlAliasBaseManager sqlAliasBaseManager,
			FromClauseIndex fromClauseIndex) {

		// todo : limit inclusion of subclass tables.
		// 		we should only include subclass tables in very specific circumstances (such
		// 		as handling persister reference in select clause, JPQL TYPE cast, subclass attribute
		// 		de-reference, etc).  In other cases it is an unnecessary overhead to include those
		// 		table joins
		//
		// however... the easiest way to accomplish this is during the SQM building to have each FromElement
		//		keep track of all needed subclass references.  The problem is that that gets tricky with the
		// 		design goal of having SQM be completely independent from ORM.  It basically means we will end
		// 		up needing to expose more model and mapping information in the org.hibernate.sqm.domain.ModelMetadata
		// 		contracts
		//
		// Another option would be to have exposed methods on TableSpecificationGroup to "register"
		//		path dereferences as we interpret SQM.  The idea being that we'd capture the need for
		//		certain subclasses as we interpret the SQM into SQL-AST via this registration.  However
		//		since

		final EntityTableGroup group = new EntityTableGroup(
				tableSpace,
				sqlAliasBaseManager.getSqlAliasBase( fromElement ),
				persister
		);

		fromClauseIndex.crossReference( fromElement, group );

		final Table drivingTable = new Table( tables[0], group.getAliasBase() + '_' + 0 );
		group.setRootTable( drivingTable );

		// todo : determine proper join type
		JoinType joinType = JoinType.LEFT;

		for ( int i = 1; i < tables.length; i++ ) {
			final Table table = new Table( tables[i], group.getAliasBase() + '_' + i );
			group.addTableSpecificationJoin( new TableJoin( joinType, table, null ) );
		}

		return group;
	}



	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// SQM EntityType impl


	@Override
	public String getName() {
		return getTypeName();
	}

	@Override
	public Type getBoundType() {
		return this;
	}

	@Override
	public ManagedType asManagedType() {
		return this;
	}

	@Override
	public IdentifiableType getSuperType() {
		// todo : implement
		throw new NotYetImplementedException();
	}

	@Override
	public IdentifierDescriptor getIdentifierDescriptor() {
		// todo : implement
		throw new NotYetImplementedException();
	}

	@Override
	public SingularAttribute getVersionAttribute() {
		// todo : implement
		throw new NotYetImplementedException();
	}

	@Override
	public Attribute findAttribute(String name) {
		if ( attributeMap.containsKey( name ) ) {
			return attributeMap.get( name );
		}

		// todo : id should be handled explicitly on init

		if ( "id".equals( name ) ) {
			return new PseudoIdAttributeImpl(
					this,
					domainMetamodel.toSqmType( persister.getEntityPersister().getIdentifierType() ),
					org.hibernate.sql.orm.internal.sqm.model.Helper.interpretIdentifierClassification( persister.getEntityPersister().getIdentifierType() )
			);
		}

		return null;
	}

	@Override
	public Attribute findDeclaredAttribute(String name) {
		// todo : implement
		throw new NotYetImplementedException();
	}

	@Override
	public String getTypeName() {
		return persister.getEntityName();
	}
}