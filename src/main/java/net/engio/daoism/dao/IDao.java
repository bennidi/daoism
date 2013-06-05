package net.engio.daoism.dao;

import net.engio.daoism.Entity;
import net.engio.daoism.dao.query.ISelect;
import net.engio.daoism.dao.query.Options.AccessPlan;
import net.engio.daoism.dao.query.Query;
import net.engio.daoism.dao.query.Query.TypedQuery;
import net.engio.daoism.dao.spex.ISpecification;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * A generic interface for accessing domain objects from an underlying storage. The interface provides methods for the standard
 * persistence operations (Create,Read,Update,Delete) and some more advanced query methods using specifications. * 
 * 
 * Generic type arguments are used in method signature to avoid binding a dao instance to a concrete type of 
 * {@link net.engio.daoism.Entity} such that the same dao instance can be shared among multiple instances of {@link IRepository}.
 * 
 * The intention of a dao implementation is to encapsulate all technology aware code in one reusable and interchangeable artifact. 
 * Thus, a dao implementation of the is always technology aware and coupled to the used persistence technology.
 * Other artifacts of the persistence layer such as {@link ITypedDao} or {@link IRepository} rely on the {@link IDao} implementation
 * to carry out their persistence operations. A change of underlying persistence technology is possible by simply providing a different
 * implementation of {@link IDao}.
 * 
 * 
 * @author Benjamin Diedrichsen
 * 
 */

public interface IDao {

	/**
	 * Deletes a domain object from persistence storage. Detached domain objects are reattached before deletion.
	 * How delete operation is cascaded to referenced objects depends on the persistence technology and on the 
	 * configuration of the persistence model (e.g. annotations on the domain object)
	 * 
	 * @param <E>
	 *            any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass
	 *            the class of domain object that will be deleted
	 * @param entity
	 *            the domain object to delete
	 * @return 
	 */
	<E extends Entity<? extends Serializable>> boolean delete(Class<E> domainClass, E entity);

	
	/**
	 * Deletes a list of domain objects from persistence storage. Conforms to the semantics of the delete method.
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will be deleted
	 * @param entities The domain object to delete
	 * @return 
	 */
	<E extends Entity<? extends Serializable>> boolean deleteAll(Class<E> domainClass, Collection<E> entities);

	/**
	 * Check if there are objects matching the given specification. This query will also consider subtypes (polymorphic).
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will be deleted
	 * @param specification The specification that the domain objects have to conform to
	 * @return True, if there is at least one domain object matchting the given specification, false otherwise
	 */
	<E extends Entity<? extends Serializable>> boolean exists(Class<E> domainClass, ISpecification specification);

	
	/**
	 * Finds all domain objects of type entityClass or one of its sub types (polymorphic)
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The upper bound of the class hierarchy that will be considered in the query
	 * @return A list of all domain objects of type <D> or one of its subtypes
	 */
	<E extends Entity<? extends Serializable>> List<E> findAll(Class<E> domainClass);

	/**
	 * Find a domain object by its primary key.
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will looked up by id
	 * @param id The primary key of the domain object
	 * @return the domain object with the given id or null if such an object does not exist
	 */
	<E extends Entity<? extends Serializable>> E findById(Class<E> domainClass, Serializable id);
	
	/**
	 * Find a domain object by its primary key.
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will looked up by id
	 * @param id The primary key of the domain object
	 * @return the domain object with the given id or null if such an object does not exist
	 */
	<E extends Entity<? extends Serializable>> E findById(Class<E> domainClass, Serializable id, AccessPlan options);
	
	/**
	 * Find domain objects using a {@link net.engio.daoism.dao.query.ISelect} (polymorphic)
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The upper bound of the class hierarchy that will be considered in the query
	 * @param finder The finder object that specifies the query conditions and parameters
	 * @return
	 */
	<E extends Entity<? extends Serializable>> List<E> findAll(Class<E> domainClass, ISelect<E> finder);

	/**
	 * Find objects matching a given specification
	 * 
	 * @param <E>
	 * @param domainClass The upper bound of the class hierarchy that will be considered in the query
	 * @param specification The specification that formulates the conditions each domain objects has to match
	 * @return
	 */
	<E extends Entity<? extends Serializable>> List<E> findAll(Class<E> domainClass, ISpecification specification);
	
	/**
	 * Find first object matching a given specification. If no ordering is specified the result may vary with
	 * eac call even if the same criteria is specified.
	 * 
	 * @param <E>
	 * @param domainClass The upper bound of the class hierarchy that will be considered in the query
	 * @param specification The specification that formulates the conditions each domain objects has to match
	 * @return
	 */
	public <E extends Entity<? extends Serializable>> E find(Class<E> domainClass, ISpecification specification);

	/**
	 * The method is used to persist an instance of {@link net.engio.daoism.Entity} to the underlying storage
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will be persisted
	 * @param entity The domain object to persist
	 * @return The persisted object (after a call to persists, the id is set on the returned persistent object)
	 */
	<E extends Entity<? extends Serializable>> E persist(Class<E> domainClass, E entity);

	/**
	 * The method is used to persist a list of instances of {@link net.engio.daoism.Entity}. It applies the semantics
	 * of the persist operation of a single domain object
	 * 
	 * @param <E> Any subtype of {@link net.engio.daoism.Entity} welcome
	 * @param domainClass The class of domain object that will be persisted
	 * @param entities The domain object to persist
	 * @return A list containing all successfully persisted objects
	 */
	<E extends Entity<? extends Serializable>> List<E> persistAll(Class<E> domainClass, List<E> entities);

	/**
	 * Executes the given unit of work within new transaction boundaries. If the unit of work executes without 
	 * exceptions, then changes to persistent object are committed else all changes are rolled back.
	 * 
	 * @param t The unit of work that will be run in a transaction
	 */
	void runInTransaction(IUnitOfWork t);

	
	/**
	 * Counts all existing entities of type Class<E>
	 * @param domainClass The class type of domain object
	 * @return The number of existing entities
	 */
	<E extends Entity<? extends Serializable>> long count(Class<E> domainClass);

	
	/**
	 * Counts all existing entities of type Class<E> matching the given specification
	 * @param domainClass The class type of domain object
	 * @return The number of existing entities
	 */
	<E extends Entity<? extends Serializable>> long count(Class<E> domainClass, ISpecification specification);


	<E extends Entity<? extends Serializable>> List<E> findAll(Class<E> entityClass, TypedQuery query);

	<E extends Entity<? extends Serializable>> E find(Class<E> entityClass, TypedQuery query);

	<E> List<E> runQuery(Class<E> resultType, Query source);

    <E extends Entity<? extends Serializable>> boolean isManaged(E entity);

    void flush();

}
