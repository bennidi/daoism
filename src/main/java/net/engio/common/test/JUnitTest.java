package net.engio.common.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.engio.common.test.tasks.IDataInitializationTask;
import net.engio.common.test.tasks.RequiredDataInitialization;
import net.engio.common.utils.ReflectionUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JUnitTest {

	protected Logger log = LoggerFactory.getLogger(getClass());

	// this junit rule is necessary to get the name of the currently executed
	// test method at runtime
	@Rule
	public TestName currentTest = new TestName();

	private List<IDataInitializationTask> activeDataInitTasks = new ArrayList<IDataInitializationTask>();

	@Before
	public void setUp() {
		long start = System.currentTimeMillis();
		prepareRequiredDataInitTasks();

		try {
			// exceptions in test setup lead to strange behaviour of arquillian
			// so it's necessary to catch them here
			setUpTasks();
		} catch (Exception e) {
			e.printStackTrace();
			fail("setUpTasks failed: " + e.getMessage());
		}
		log.info("Data initialization setup took "
				+ (System.currentTimeMillis() - start) + " ms");
	}

	@After
	public void tearDown() {
		long start = System.currentTimeMillis();
		try {
			// exceptions in test setup lead to strange behaviour of arquillian
			// so it's necessary to catch them here
			tearDownTasks();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		activeDataInitTasks.clear();
		log.info("Data initialization tear down took "
				+ (System.currentTimeMillis() - start) + " ms");
	}

	private void prepareRequiredDataInitTasks() {

		RequiredDataInitialization initTasksForClass = this.getClass()
				.getAnnotation(RequiredDataInitialization.class);

		Method testcase = ReflectionUtils.getMethod(this.getClass(),
				currentTest.getMethodName());
		RequiredDataInitialization initTasksForMethod = testcase
				.getAnnotation(RequiredDataInitialization.class);

		LinkedHashSet<Class<? extends IDataInitializationTask>> allTasks = new LinkedHashSet<Class<? extends IDataInitializationTask>>();
		allTasks.addAll(findRequiredInitTasks(initTasksForClass));
		allTasks.addAll(findRequiredInitTasks(initTasksForMethod));

		createRequiredDataInitTaskInstances(allTasks);

	}

	private void createRequiredDataInitTaskInstances(
			Collection<Class<? extends IDataInitializationTask>> taskDefs) {
		for (Class<? extends IDataInitializationTask> task : taskDefs) {
			try {
				activeDataInitTasks.add(getInstance(task));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected IDataInitializationTask getInstance(Class<? extends IDataInitializationTask> taskDef) throws Exception{
		IDataInitializationTask task = taskDef.newInstance();	
		return task;
	}

	/*
	 * Produces a set of <Class<? extends IDataInitializationTask> instances
	 * which represent all required init task starting from the given
	 * initTasksDefinition. It uses a depth-first decent
	 */
	private Set<Class<? extends IDataInitializationTask>> findRequiredInitTasks(
			RequiredDataInitialization initTasksDefinition) {
		// linked hash set preserves insertion order
		Set<Class<? extends IDataInitializationTask>> allTasks = new LinkedHashSet<Class<? extends IDataInitializationTask>>();
		// process all defined tasks
		if (initTasksDefinition != null) {
			Class<? extends IDataInitializationTask>[] tasks = initTasksDefinition
					.tasks();
			for (int i = 0; i < tasks.length; i++) {
				Class<? extends IDataInitializationTask> task = tasks[i];
				// each task may define required init tasks which need to be run
				// prior to this task
				try {
					// depth-first decent into required init task hierarchy
					allTasks.addAll(findRequiredInitTasks(task
							.getAnnotation(RequiredDataInitialization.class)));
					allTasks.add(task);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return allTasks;
	}

	public void setUpTasks() {
		for (IDataInitializationTask task : activeDataInitTasks) {
			log.info("Running setup of initialization task "
					+ task.getClass().getSimpleName());
			task.setUp();
		}
	}

	public void tearDownTasks() {
		// tear down tasks in inverse order because of potential structural
		// dependencies
		for (int i = activeDataInitTasks.size() - 1; i >= 0; i--) {
			IDataInitializationTask task = activeDataInitTasks.get(i);
			log.info("Running tear down of initialization task "
					+ task.getClass().getSimpleName());
			task.tearDown();
		}
	}

	public void fail(String message) {
		org.junit.Assert.fail(message);
	}

	public void fail() {
		org.junit.Assert.fail();
	}

	public void assertTrue(Boolean condition) {
		org.junit.Assert.assertTrue(condition);
	}

	public void assertTrue(String message, Boolean condition) {
		org.junit.Assert.assertTrue(message, condition);
	}

	public void assertFalse(Boolean condition) {
		org.junit.Assert.assertFalse(condition);
	}

	public void assertNull(Object object) {
		org.junit.Assert.assertNull(object);
	}

	public void assertNotNull(Object object) {
		Assert.assertNotNull(object);
	}

	public void assertFalse(String message, Boolean condition) {
		org.junit.Assert.assertFalse(message, condition);
	}

	public void assertEquals(Object expected, Object actual) {
		org.junit.Assert.assertEquals(expected, actual);
	}

}
