package net.engio.common.test.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataInitializationTask  implements IDataInitializationTask{
	
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see net.engio.common.test.IDataInitializationTask#setUp()
	 */
	@Override
	public abstract void setUp();
	
	/* (non-Javadoc)
	 * @see net.engio.common.test.IDataInitializationTask#tearDown()
	 */
	@Override
	public abstract void tearDown();
	
	protected <T> T getRandomElement(List<T> candidates){
		int idx = (int)System.currentTimeMillis() % candidates.size();
		return candidates.get(idx);
	}

}
