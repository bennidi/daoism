package net.engio.daoism.dao.jpa;

import javax.persistence.EntityManager;

import net.engio.daoism.dao.IUnitOfWork;

/**
 * An implementation of {@link IUnitOfWork} interface for JPA 2.0. It offers access to an instance of entity 
 * manager using the instance variable em.
 * 
 * @author Benjamin Diedrichsen
 * 
 */
public abstract class UnitOfWork implements IUnitOfWork {

	protected EntityManager em;

	@Override
	public abstract void execute() throws Exception;

	public void setEm(EntityManager em) {
		this.em = em;
	}

}
