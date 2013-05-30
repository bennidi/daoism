package net.engio.common.persistence.dao;

/**
 * 
 * A unit of work is a set of operations that need to be executed as an atomic operation. A unit of work can be executed
 * within its own transaction boundaries (using a dao/repository) such that if an exception occurs during execution, all changes to persistent state
 * of the application are rolled back. If the execution succeeds without any exception then the transaction is committed
 * 
 * @author Benjamin Diedrichsen
 *
 * @param <T>
 */
public interface IUnitOfWork {

	public abstract void execute() throws Exception;

}
