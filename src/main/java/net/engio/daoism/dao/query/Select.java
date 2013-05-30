package net.engio.daoism.dao.query;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.engio.daoism.Entity;
import net.engio.common.spex.ISpecification;
import net.engio.common.spex.attr.IAttribute;


/**
 * The default implementation of the {@link net.engio.daoism.dao.query.ISelect} interface.
 * 
 * @author Benjamin Diedrichsen
 *
 * @param <T>
 */
public class Select<T extends Entity<?>> implements ISelect<T> {

	private static final long serialVersionUID = 1L;

	private Map<String, String> eagerAssociations = new HashMap<String, String>(5);;

	private Map<String, SortOrder> orderBy = new LinkedHashMap<String, SortOrder>(5); // insertion-ordered map;

	private ISpecification< ?> whereClause;
	
	private Class<T> queryRoot; 

	public Select(Class<T> queryRoot, ISpecification< ?> specification) {
		super();
		this.whereClause = specification;
		this.queryRoot = queryRoot;
	}
	
	private Select(Class<T> queryRoot){
		super();
		this.queryRoot = queryRoot;
	}
	
	public Select<T> Where(ISpecification< ?> whereClause){
		this.whereClause = whereClause;
		return this;
	}

	@Override
	public Map<String, String> getEagerAssociations() {
		return eagerAssociations;
	}

	@Override
	public Map<String, SortOrder> getOrderStatements() {
		return orderBy;
	}

	@Override
	public ISpecification< ?> getSpecification() {
		return whereClause;
	}
	
	@Override
	public ISelect<T> OrderBy(IAttribute<?> attribute, SortOrder direction) {
		orderBy.put(attribute.getName(), direction);
		return this;
	}

	@Override
	public Class<T> getQueryRoot() {
		return queryRoot;
	}

	@Override
	public ISelect<T> loadEager(IAttribute<?> relation) {
		eagerAssociations.put(relation.getName(), "LEFT");
		return this;
	}
	
	public static <T extends Entity<?>> Select<T> From(Class<T> queryRoot){
		return new Select<T>(queryRoot);
	}
	

}
