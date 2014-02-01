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
 * The typed dao is the default implementation of the {@link ITypedDao} interface. It is merely a thin wrapper that delegates all operations
 * to an underlying instance of {@link IPersistenceProvider}. It is completely technology agnostic since all technology specific operations are
 * implemented in the delegate {@link IPersistenceProvider}.
 * 
 * It's main function is to parametrize all calls to the {@link IPersistenceProvider} with the necessary domain and key classes
 * 
 * @author Benjamin Diedrichsen
 *
 * @param <K>
 * @param <E>
 */
public abstract class TypedDao<K extends Serializable , E extends Persistent<K>> implements ITypedDao<K, E> {

	protected Class<E> entityClass;

	protected Class<K> keyClass;

	
	public TypedDao(Class<K> keyClass, Class<E> entityClass) {
		super();
		this.keyClass = keyClass;
		this.entityClass = entityClass;
	}

    /**
     * This method is used to provide access to the delegate with access to the configured entity manager
     * @return
     */
	protected abstract IPersistenceProvider getPersistenceProvider();

	@Override
	public boolean delete(E entity) {
		return getPersistenceProvider().delete(entityClass, entity);

	}

	@Override
	public boolean deleteAll(Collection<E> entities) {
		return getPersistenceProvider().deleteAll(entityClass, entities);
	}



	@Override
	public List<E> findAll() {
		return getPersistenceProvider().findAll(entityClass);
	}

	@Override
	public E findById(K id) {
		return getPersistenceProvider().findById(entityClass, id);
	}
	
	@Override
	public <S extends E> List<S> find(ISelect<S> finder) {
		return getPersistenceProvider().findAll(finder.getQueryRoot(), finder);
	}



	
	@Override
	public E persist(E entity) {
		return getPersistenceProvider().persist(entityClass, entity);
	}

	@Override
	public List<E> persistAll(List<E> entities) {
		return getPersistenceProvider().persistAll(entityClass, entities);
	}

	
	@Override
	public void runTransactional(IUnitOfWork t) {
		getPersistenceProvider().runInTransaction(t);

	}

	@Override
	public long countAll() {
		return getPersistenceProvider().count(entityClass);
	}



    @Override
    public E find(TypedQuery query) {
        List<E> result = getPersistenceProvider().runQuery(entityClass, query);
        return result.isEmpty() ? null : result.get(0);
    }

    public E findById(K id, AccessPlan options) {
		return getPersistenceProvider().findById(entityClass, id, options);
	};
	

    @Override
    public List<E> findAll(TypedQuery query) {
        return getPersistenceProvider().findAll(entityClass, query);
    }

    @Override
    public <KEY extends Serializable, E extends Persistent<KEY>> ITypedDao forEntity(Class<KEY> keyType, Class<E> entityType) {
        return new TypedDao(keyType, entityType) {
            @Override
            protected IPersistenceProvider getPersistenceProvider() {
                return getPersistenceProvider();
            }
        };
    }

    @Override
	public <E> List<E> query(Class<E> queryResultType, Query queryToRun) {
		return getPersistenceProvider().runQuery(queryResultType, queryToRun);
	}

    @Override
    public boolean isSameVersion(E one, E other) {
        return one.getId().equals(other.getId()) && one.getVersion() == other.getVersion();
    }

    @Override
    public boolean isManaged(E entity) {
        return getPersistenceProvider().isManaged(entity);
    }

    @Override
    public void flush() {
        getPersistenceProvider().flush();
    }
}
