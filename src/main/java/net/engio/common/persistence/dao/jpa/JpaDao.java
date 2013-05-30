package net.engio.common.persistence.dao.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import net.engio.common.persistence.Entity;
import net.engio.common.persistence.dao.IDao;
import net.engio.common.persistence.dao.query.ISelect;
import net.engio.common.persistence.dao.IUnitOfWork;
import net.engio.common.persistence.dao.query.Options;
import net.engio.common.persistence.dao.query.Options.AccessPlan;
import net.engio.common.persistence.dao.query.LockType;
import net.engio.common.persistence.dao.query.Query;
import net.engio.common.spex.ISpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An implementation of {@link IDao} interface according to JPA 2.0 specificatio
 * 
 * @author Benjamin Diedrichsen
 * 
 */
public abstract class JpaDao implements IDao{


	private Logger log = LoggerFactory.getLogger(getClass());
	
	public abstract EntityManager entityManager();
	

	@Override
	public <D extends Entity<?>> boolean delete(Class<D> domainClass, D domainObject) {
		return deleteInternal(domainClass, domainObject);
	}

    private <D extends Entity<?>> boolean deleteInternal(Class<D> domainClass, D domainObject) {
        // REVIEW: There is no exception thrown by the entityManager().remove() that
        // could actually be handled by the application
        // even stale objects can be removed without any exception from Hibernate
        boolean hasBeenDeleted = false;
        D tmp = null;
        if (domainObject != null && domainObject.getId() != null)
            tmp = entityManager().find(domainClass, domainObject.getId());
        if (tmp != null) {
            entityManager().remove(tmp);
            hasBeenDeleted = true;
        }
        return hasBeenDeleted;
    }

	@Override
	public <D extends Entity<?>> boolean deleteAll(Class<D> domainClass, Collection<D> domainObjects) {
		boolean allDelected = true;
        for (D domainObject : domainObjects) {
			allDelected = allDelected & deleteInternal(domainClass, domainObject);
		}
		return allDelected;
	}

	@Override
	public <D extends Entity<?>> boolean exists(Class<D> domainClass, ISpecification<?> specification) {
		return count(domainClass, specification) > 0;
	}

	@Override
	public <D extends Entity<?>> List<D> findAll(Class<D> domainClass) {
		return entityManager().createQuery("SELECT en FROM " + domainClass.getName() + " en", domainClass)
			.getResultList();
	}

	@Override
	public <D extends Entity<?>> D findById(Class<D> domainClass, Serializable id) {
		return findById(domainClass, id, Options.Default());
	}

	@Override
	public <D extends Entity<?>> D findById(Class<D> domainClass, Serializable id, AccessPlan options) {
		if (id == null) {
			return null;
		}
		// non-refreshing with default lock is the standard behaviour of entityManager().find
		if (!options.isRefresh() && options.getLockMode().equals(LockType.Default)) {
			return entityManager().find(domainClass, id);
		}
		D domainObject;

        // note: should throw an exception if lock mode other than  NONE is specified and no surrounding
        // transaction is present, this exception will bubble-up because it is essentially an error in the
        // application code to require a lock without providing a transactional context
		switch (options.getLockMode()) {
		case PessimisticWrite:
			domainObject = entityManager().find(domainClass, id, LockModeType.PESSIMISTIC_WRITE);
			break;
		case Optimistic:
			domainObject = entityManager().find(domainClass, id, LockModeType.OPTIMISTIC);
			break;
		default:domainObject = entityManager().find(domainClass, id);

		}
		if (options.isRefresh() && entityManager().contains(domainObject)) {
                entityManager().refresh(domainObject);
		}
		return domainObject;
	}

	@Override
	public <D extends Entity<?>> List<D> findAll(Class<D> domainClass, ISelect<D> finder) {
		return entityManager().createQuery(enrichQuery(entityManager().getCriteriaBuilder()
                .createQuery(domainClass), domainClass, finder))
			.getResultList();
	}
	
	@Override
	public <D extends Entity<?>> D find(Class<D> domainClass,
			ISpecification<?> specification) {
		List<D> results = entityManager().createQuery(enrichQuery(entityManager().getCriteriaBuilder()
                .createQuery(domainClass), domainClass, specification))
				.setMaxResults(1)
				.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	private <D extends Entity<?>> CriteriaQuery<D> enrichQuery(CriteriaQuery query, Class<D> domainClass, ISelect<D> finder) {
		CriteriaBuilder criteriaBuilder = entityManager().getCriteriaBuilder();
		enrichQuery(query, domainClass, finder.getSpecification());
		Root<D> root = (Root<D>) query.getRoots()
			.toArray()[0]; // ugly way of getting the one and only query root

		//process the order statements
		for (Entry<String, ISelect.SortOrder> orderBy : finder.getOrderStatements()
			.entrySet()) {
			if (orderBy.getValue() == ISelect.SortOrder.Ascending) {
				query.orderBy(criteriaBuilder.asc(root.get(orderBy.getKey())));
			} else {
				query.orderBy(criteriaBuilder.desc(root.get(orderBy.getKey())));
			}
		}

		//specify eager loading for the configured associations
		for (Entry<String, String> eagerAssoc : finder.getEagerAssociations()
			.entrySet()) {
			// use left join to not loose the root entity in case of empty relations
			root.fetch(eagerAssoc.getKey(), JoinType.LEFT);
			query.distinct(true); // necessary to get only one result for each distinct root instance
		}
		return query;
	}

	private <D extends Entity<?>> CriteriaQuery<D> enrichQuery(CriteriaQuery query, Class<D> domainClass, ISpecification<?> specification) {
		Root<D> root = query.from(domainClass);
		if (specification != null)
			query.where(specification.toJpaPredicate(entityManager().getCriteriaBuilder(), query, root));
		query.select(root);
		return query;
	}

	@Override
	public <D extends Entity<?>> List<D> findAll(Class<D> domainClass, ISpecification<?> specification) {

		return entityManager().createQuery(enrichQuery(entityManager().getCriteriaBuilder()
                .createQuery(domainClass), domainClass, specification))
			.getResultList();

	}

	@Override
	public <D extends Entity<?>> D persist(Class<D> domainClass, D domainObject) {
		return persistInternal(domainClass, domainObject);
	}

    private <D extends Entity<?>> D persistInternal(Class<D> domainClass, D domainObject) {
        if (domainObject == null)
            return null;
        // check if update or insert
        if (isPersistent(domainObject)) {
            domainObject = entityManager().merge(domainObject);
            entityManager().flush(); //FIXME: Necessary for OrphanRemoval=true to work -> see OrderDirectDebitCreationTest

        } else {
            entityManager().persist(domainObject);
        }
        return domainObject;
    }

    /**
     * Check whether the object is a representation of a persistent object from the database, without actually issuing a
     * query to the database.
     * The result returned by this method might thus not reflect its actual state, since another process
     * could have removed or persisted another copy of this object in a different process.
     *
     * @return true, if persistent. false otherwise
     */
	protected <D extends Entity<?>> boolean isPersistent(D domainObject) {
		return domainObject.getVersion() > -1;
	}

	@Override
	public <D extends Entity<?>> List<D> persistAll(Class<D> domainClass, List<D> domainObjects) {
		List<D> persistentObjects = new ArrayList<D>(domainObjects.size());
		if (domainObjects != null) {
			for (D domainObject : domainObjects) {
				persistentObjects.add(persistInternal(domainClass, domainObject));
			}
		}
		return persistentObjects;
	}

	@Override
	public void runInTransaction(IUnitOfWork t) {
		UnitOfWork unit = (UnitOfWork) t;
		unit.setEm(entityManager());
		try{
			unit.execute();
		} catch (Exception e) {
			throw new RuntimeException(e); // promote exception to caller
		}

	}

	@Override
	public <D extends Entity<?>> long count(Class<D> domainClass) {
		// null is treated like an empty specification
		return count(domainClass, null);
	}

	@Override
	public <D extends Entity<?>> long count(Class<D> domainClass, ISpecification<?> specification) {
		CriteriaBuilder builder = entityManager().getCriteriaBuilder();

		// create the query with result of type long
		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		// add the specification constraints and query root
		enrichQuery(countCriteria, domainClass, specification);
		// get the root (there is only a single root)
		Root<D> entityRoot = (Root<D>) countCriteria.getRoots()
			.toArray()[0];
		// now add the count projection
		countCriteria.select(builder.count(entityRoot));
		return entityManager().createQuery(countCriteria)
			.getSingleResult();
	}
	
	@Override
	public <E extends Entity<?>> List<E> findAll(Class<E> entityClass, Query.TypedQuery query) {
		javax.persistence.TypedQuery<E> jpaQuery = transformQuery(entityClass, query);
		return jpaQuery.getResultList();
	}

	@Override
	public <E extends Entity<? extends Serializable>> E find(Class<E> entityClass, Query.TypedQuery query) {
		javax.persistence.TypedQuery<E> jpaQuery = transformQuery(entityClass, query);
		try {
			return jpaQuery.getSingleResult();
		} catch (NoResultException e) {
			log.warn("No results returned for find(" + entityClass.getSimpleName() + ") with query" + query);
			return null;
		}
	}

	@Override
	public <E> List<E> runQuery(Class<E> resultType, Query source){
		if(source.getType().equals(Query.Type.Native)) {
			javax.persistence.Query nativeQuery;
			nativeQuery = entityManager().createNativeQuery(((Query.NativeQuery) source).getQueryString());
			configureQuery(nativeQuery, (Query.NativeQuery)source);
			return nativeQuery.getResultList();
		}
		else{
			javax.persistence.TypedQuery<E> jpaQuery  = transformQuery(resultType, (Query.TypedQuery)source);
			return jpaQuery.getResultList();
		}
	}

	private <E> javax.persistence.TypedQuery<E> transformQuery(Class<E> resultType, Query.TypedQuery query){
		javax.persistence.TypedQuery<E> jpaQuery = null;
		switch(query.getType()){
			case Jpql:
				jpaQuery =   entityManager().createQuery(((Query.JpqlQuery) query).getQueryString(), resultType);
				configureQuery(jpaQuery, ((Query.JpqlQuery)query));
				break;
			case Named:
				jpaQuery =   entityManager().createNamedQuery(((Query.NamedQuery) query).getName(), resultType);
				configureQuery(jpaQuery, ((Query.NamedQuery)query));
				break;
			default: throw new IllegalArgumentException("Unkown query type [" + query + "]. " +
					"Only named or jpql queries are currently supported");
		}
		return jpaQuery;
	}

	private void configureQuery(javax.persistence.Query query, Query.ParametrizedQuery source){
		for (Query.NamedQuery.QueryParameter param : source.getParameters()) {
			query.setParameter(param.getKey(), param.getValue());
		}
		if (source.hasResultLimit()) query.setMaxResults(source.getMaxResults());
		if(source.isFirstResultSet())query.setFirstResult(source.getFirtResult());
	}

    @Override
    public <D extends Entity<? extends Serializable>> boolean isManaged(D domainObject) {
        return entityManager().contains(domainObject);
    }
}
