package net.engio.common.persistence.dao.query;

import java.io.Serializable;
import java.util.Map;

import net.engio.common.persistence.Entity;
import net.engio.common.spex.ISpecification;
import net.engio.common.spex.attr.IAttribute;


/**
 * 
 * A finder is used to execute queries for domain objects. It uses a {@link ISpecification} to define the query criteria and 
 * offers additional parameters for query execution, such as ordering, result size limits, eager loading of lazy associations etc.
 * 
 * A finder can also be used to query only on the subclass hierarchy of the aggregate root of a given repository. The query root 
 * is defined by the generic type argument T and denotes the upper bound for polymorphic queries. 
 * Instances of superclasses of T will not occur in the query result even if they match the given specification.
 * 
 * @author Benjamin Diedrichsen
 *
 * @param <T> The query root. This class denotes the upper bound of the type hierarchy that the query will scan for matching objects
 */


public interface ISelect<T extends Entity<?>> extends Serializable {

	/**
	 * Enumeration of available sort orders for result set ordering
	 * 
	 * @author Benjamin Diedrichsen
	 *
	 */
	public enum SortOrder {
		Ascending, Descending
	}
	
	/**
	 * If the query root or some super class defines relationships that are lazily initialized by default
	 * but need to be fetched in the query execution, then they can be specified to be fetched eagerly
	 * using this method. Multiple calls for different relations are possible.
	 * 
	 * @param association The attribute that represents the relation that should be fetched eagerly
	 * @return The finder instance
	 */
	public abstract ISelect<T> loadEager(IAttribute<?> association);

	
	/**
	 * Retrieve a map that contains all the associations that are configured to be fetched eagerly
	 * The key of the map contains the name of the association, the value defines the join type
	 * (currently only LEFT JOIN is supported)
	 * @return
	 */
	public abstract Map<String, String> getEagerAssociations();

	public abstract Map<String, SortOrder> getOrderStatements();

	public abstract ISpecification< ?> getSpecification();

	public abstract ISelect<T> OrderBy(IAttribute<?> attribute, SortOrder direction);
	
	public abstract Class<T> getQueryRoot();

}
