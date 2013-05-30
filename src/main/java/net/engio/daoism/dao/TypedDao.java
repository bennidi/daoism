package net.engio.daoism.dao;

import net.engio.common.spex.ISpecification;
import net.engio.daoism.Entity;
import net.engio.daoism.dao.query.ISelect;
import net.engio.daoism.dao.query.Options.AccessPlan;
import net.engio.daoism.dao.query.Query;
import net.engio.daoism.dao.query.Query.TypedQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * The typed dao is the default implementation of the {@link ITypedDao} interface. It is merely a thin wrapper that delegates all operations
 * to an underlying instance of {@link IDao}. It is completely technology agnostic since all technology specific operations are 
 * implemented in the delegate {@link IDao}.
 * 
 * It's main function is to parametrize all calls to the {@link IDao} with the necessary domain and key classes
 * 
 * @author Benjamin Diedrichsen
 *
 * @param <K>
 * @param <E>
 */
public abstract class TypedDao<K extends Serializable , E extends Entity<K>> implements ITypedDao<K, E> {

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
	protected abstract IDao getDao();

	@Override
	public boolean delete(E entity) {
		return getDao().delete(entityClass, entity);

	}

	@Override
	public boolean deleteAll(Collection<E> entities) {
		return getDao().deleteAll(entityClass, entities);
	}

	@Override
	public boolean exists(ISpecification<?> specification) {
		return getDao().exists(entityClass, specification);
	}

	@Override
	public List<E> findAll() {
		return getDao().findAll(entityClass);
	}

	@Override
	public E findById(K id) {
		return getDao().findById(entityClass, id);
	}
	
	@Override
	public <S extends E> List<S> find(ISelect<S> finder) {
		return getDao().findAll(finder.getQueryRoot(), finder);
	}

	@Override
	public E find(ISpecification<?> specification) {
		return getDao().find(entityClass, specification);
	}
	
	@Override
	public List<E> findAll(ISpecification<?> specification) {
		return getDao().findAll(entityClass, specification);
	}

	
	@Override
	public E persist(E entity) {
		return getDao().persist(entityClass, entity);
	}

	@Override
	public List<E> persistAll(List<E> entities) {
		return getDao().persistAll(entityClass, entities);
	}

	
	@Override
	public void runTransactional(IUnitOfWork t) {
		getDao().runInTransaction(t);

	}

	@Override
	public long countAll() {
		return getDao().count(entityClass);
	}

	@Override
	public long count(ISpecification<?> specification) {
		return getDao().count(entityClass, specification);
	}

    @Override
    public E find(TypedQuery query) {
        List<E> result = getDao().runQuery(entityClass, query);
        return result.isEmpty() ? null : result.get(0);
    }

    public E findById(K id, AccessPlan options) {
		return getDao().findById(entityClass, id, options);
	};
	

    @Override
    public List<E> findAll(TypedQuery query) {
        return getDao().findAll(entityClass, query);
    }

    @Override
    public <KEY extends Serializable, E extends Entity<KEY>> ITypedDao forEntity(Class<KEY> keyType, Class<E> entityType) {
        return new TypedDao(keyType, entityType) {
            @Override
            protected IDao getDao() {
                return getDao();
            }
        };
    }

    @Override
	public <E> List<E> query(Class<E> queryResultType, Query queryToRun) {
		return getDao().runQuery(queryResultType, queryToRun);
	}

    @Override
    public boolean isSameVersion(E one, E other) {
        return one.getId().equals(other.getId()) && one.getVersion() == other.getVersion();
    }

    @Override
    public boolean isManaged(E entity) {
        return getDao().isManaged(entity);
    }

    @Override
    public void flush() {
        getDao().flush();
    }
}
