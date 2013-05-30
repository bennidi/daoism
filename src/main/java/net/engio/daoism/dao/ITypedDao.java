package net.engio.daoism.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.engio.daoism.Entity;
import net.engio.daoism.dao.query.Options.AccessPlan;
import net.engio.daoism.dao.query.ISelect;
import net.engio.daoism.dao.query.Query;
import net.engio.daoism.dao.query.Query.TypedQuery;
import net.engio.common.spex.ISpecification;

/**
 * A generically typed version of the {@link IDao} interface. It provides type-safe method for domain object persistence. The typed dao can
 * be accessed using an instance of {@link IRepository} in case that it should be necessary to directly access and/or persist instances of
 * {@link net.engio.daoism.Entity}. {@link This} interface offers exactly the same functionality as the {@link IDao}, meaning that the corresponding
 * methods share the same semantics
 * 
 * @author Benjamin Diedrichsen
 * 
 * @param <KEY>
 *            The type of primary key used for the {@link net.engio.daoism.Entity}
 * @param <E>
 *            The type of domain object the instance of {@link ITypedDao} works on
 */

public interface ITypedDao<KEY extends Serializable, E extends Entity<KEY>> {

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	boolean delete(E entity);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @return
	 * @see IDao
	 */
	boolean deleteAll(Collection<E> entities);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	boolean exists(ISpecification<?> specification);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	List<E> findAll();

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	E findById(KEY id);
	
	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	E findById(KEY id, AccessPlan options);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	<S extends E> List<S> find(ISelect<S> finder);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	E find(ISpecification<?> specification);
	
	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	List<E> findAll(ISpecification<?> specification);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	E persist(E entity);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	List<E> persistAll(List<E> entities);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	void runTransactional(IUnitOfWork t);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	long countAll();

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IDao
	 */
	long count(ISpecification<?> specification);
	

	E find(TypedQuery query);
	
	<R> List<R> query(Class<R> queryResultType, Query queryToRun);

    List<E> findAll(TypedQuery query);

    <KEY extends Serializable, E extends Entity<KEY>> ITypedDao forEntity(Class<KEY> keyType, Class<E> entityType);

    boolean isSameVersion(E one, E other);

    boolean isManaged(E entity);
}
