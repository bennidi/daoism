package net.engio.daoism.dao;

import net.engio.daoism.Persistent;
import net.engio.daoism.dao.query.ISelect;
import net.engio.daoism.dao.query.Options.AccessPlan;
import net.engio.daoism.dao.query.Query;
import net.engio.daoism.dao.query.Query.TypedQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * A typed version of the {@link IPersistenceProvider} interface. It provides type-safe method for domain object persistence.
 *
 * 
 * @author Benjamin Diedrichsen
 * 
 * @param <KEY>
 *            The type of primary key used for the {@link net.engio.daoism.Persistent}
 * @param <E>
 *            The type of domain object the instance of {@link ITypedDao} works on
 */

public interface ITypedDao<KEY extends Serializable, E extends Persistent<KEY>> {

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	boolean delete(E entity);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @return
	 * @see IPersistenceProvider
	 */
	boolean deleteAll(Collection<E> entities);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	List<E> findAll();

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	E findById(KEY id);
	
	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	E findById(KEY id, AccessPlan options);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	<S extends E> List<S> find(ISelect<S> finder);


	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	E persist(E entity);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	List<E> persistAll(List<E> entities);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	void runTransactional(IUnitOfWork t);

	/**
	 * See corresponding method in IDao.
	 * 
	 * @see IPersistenceProvider
	 */
	long countAll();


	E find(TypedQuery query);
	
	<R> List<R> query(Class<R> queryResultType, Query queryToRun);

    List<E> findAll(TypedQuery query);

    <KEY extends Serializable, E extends Persistent<KEY>> ITypedDao forEntity(Class<KEY> keyType, Class<E> entityType);

    boolean isSameVersion(E one, E other);

    boolean isManaged(E entity);

    void flush();
}
