package net.engio.common.test.tasks;


/**
 * A data initialization task can be used in unit tests to setup required data before test
 * execution and tear down of that same data after test execution.
 * The contract of each task is that after successfully running the tear down
 * the state of the database is identical to its state before the task was set up, that is
 * the task must clean up all data it creates.
 * 
 * 
 * @author Benjamin Diedrichsen
 *
 */
public interface IDataInitializationTask {

	
	/**
	 * This method is run prior to test execution and should be used to set up all persistent data
	 */
	public abstract void setUp();

	
	/**
	 * This method is run after test execution and should be used to clean up all remaining data which
	 * has been set up by the tasks setup() method
	 */
	public abstract void tearDown();

}
