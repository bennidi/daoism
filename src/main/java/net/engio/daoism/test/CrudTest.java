package net.engio.daoism.test;

import net.engio.daoism.Persistent;
import net.engio.daoism.dao.ITypedDao;
import org.junit.Test;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This test implements generic test methods for the basic CRUD operations. After successfully running a test method the state of the
 * database will not have changed, every modification will have been reverted.
 * 
 * 
 * @author Benjamin Diedrichsen
 * 
 * @param <KEY>
 * @param <EN>
 */
public abstract class CrudTest<KEY extends Serializable, EN extends Persistent<KEY>> extends JUnitTest {

	private long entityCount;
	
	
	/**
	 * This method is used by generic CRUD tests and should return a valid, non-persistent instance of {@code A}. Every call to this method
	 * should result in the same output!
	 * 
	 * @return a valid non-persistent instance of {@link net.engio.daoism.Persistent}
	 */
	protected abstract EN createValidEntity();

    /**
     * This method is used to emulate modifications on persistent entities. It should be implemented with a randomized
     * behavior such that each call results in a (slightly) different modification
     * @param which
     */
    protected abstract void modifyEntity(EN which);


	/**
	 * Factory method to obtain a reference to the DAO that manages the tests entities
	 * 
	 * @return an instance of the DAO that manages the {@link net.engio.daoism.Persistent} of the tests
	 */
	protected abstract ITypedDao<KEY, EN> getDao();
	
	/**
	 * Create and add any number of new entities and add them to the given collection. The collection is used
     * to test persistence and removal of multiple entities as well as counting.
	 */
	protected abstract void addEntities(List<EN> entities);
	
	private List<EN> createInitialEntities(){
		List<EN> aggregates = new LinkedList<EN>();
		addEntities(aggregates);
		return aggregates;
	}

	@Override
	public void setUp() {
		entityCount = getDao().countAll();
		super.setUp();
	}

	@Override
	public void tearDown() {
		super.tearDown();
		long difference = getDao().countAll() - entityCount;
		if (difference != 0)
			log.error("Test method did not leave database in its initial state! Difference is: " + difference);
	}

	protected boolean remove(EN aggregate) {
		return getDao().delete(aggregate);
	}

	protected void removeAll(List<EN> aggregates) {
		getDao().deleteAll(aggregates);
	}

	@Test
	public void findAll() {
		List<EN> entities = createInitialEntities();
		entities = getDao().persistAll(entities);
		List<EN> allAggregates = getDao().findAll();

		// check whether findAll returned all existing entities
		assertEquals(getDao().countAll(), (long) allAggregates.size());

		// ensure that all created entities are contained
		for (EN aggregate : entities) {
			assertTrue(aggregate +  " is not returned by findAll()", allAggregates.contains(aggregate));
		}
		// remove created entities
		removeAll(entities);
	}

	@Test
	public void count() {
		long start, created, end;
		start = getDao().countAll();
		List<EN> entities = createInitialEntities();
		entities = getDao().persistAll(entities);
		created = entities.size();
		end = getDao().countAll();
		assertEquals(start + created, end);
		removeAll(entities);
		assertEquals(start, getDao().countAll());
	}

	/**
	 * Test persistence and removal of a single aggregate
	 */
	@Test
	public void singlePersist() {
		try {
			EN entity = createPersistentEntity();
			assertNotNull(entity);
			assertNotNull(entity.getId());
			assertNotNull(getDao().findById(entity.getId()));
			assertEquals(entity.getId(), getDao().findById(entity.getId())
                    .getId());
			remove(entity);
			assertNull(getDao().findById(entity.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	/**
	 * Test persistence and removal of a set of aggregates. All created aggregates will be removed if the test succeeds.
	 */
	@Test
	public void persistAll() {
		List<EN> allEntities = createInitialEntities();
		allEntities = getDao().persistAll(allEntities);
		for (EN aggregate : allEntities) {
			assertNotNull(getDao().findById(aggregate.getId()));
		}
		removeAll(allEntities);
		for (EN aggregate : allEntities) {
			assertNull(getDao().findById(aggregate.getId()));
		}

	}

	@Test
	public void findById() {
		List<EN> allEntities = getDao().findAll();
		allEntities = getRandomSublist(allEntities);
		boolean removable = false;
		// work with existing aggregates if possible
		if (allEntities.isEmpty()) {
			allEntities = getDao().persistAll(createInitialEntities());
			removable = true;
		}
		for (EN entity : allEntities) {
			EN sameEntity = getDao().findById(entity.getId());
			assertNotNull(sameEntity);
			assertTrue(sameEntity.getId()
                    .equals(entity.getId()));
			// existing aggregates may not be removed
			if (removable) {
				assertTrue(remove(entity));
				assertNull(getDao().findById(entity.getId()));
			}
		}
	}

	@Test
	public void singleDelete() {
		EN entity = createPersistentEntity();
		assertNotNull(getDao().findById(entity.getId()));
		remove(entity);
		assertNull(getDao().findById(entity.getId()));
	}

	@Test
	public void deleteAll() {
		List<EN> allEntities = createInitialEntities();
		allEntities = getDao().persistAll(allEntities);
		for (EN entity : allEntities)
			assertNotNull(getDao().findById(entity.getId()));
		removeAll(allEntities);
		for (EN entity : allEntities)
			assertNull(getDao().findById(entity.getId()));
	}

	private EN createPersistentEntity() {
		EN entity = createValidEntity();
		return getDao().persist(entity);
	}

    @Test
    public void testPersistSemantics() {
        final EN entityOne = createValidEntity();
        EN entityTwo = createValidEntity();

        // first persist returns same object
        assertTrue(getDao().persist(entityOne) == entityOne);
        // following persists will call merge() and since the entity is not managed
        // because there is no surrounding transaction, a new one will be created
        assertFalse(getDao().persist(entityOne) == entityOne);


        List<EN> entities = new LinkedList<EN>();
        entities.add(entityOne);
        entities.add(entityTwo);
        entities.add(createValidEntity());
        entities.add(createValidEntity());

        entities = getDao().persistAll(entities);

        // it's the first persist for entity two
        assertTrue(entities.get(1) == entityTwo);

        modifyEntity(entityTwo);
        assertFalse(getDao().isSameVersion(entityTwo, getDao().persist(entityTwo)));
    }

    /*
    @Test
    public void testIsManaged() {

        EN entityOne = createValidEntity();
        assertFalse(getDao().isManaged(entityOne));
        getDao().persist(entityOne);
        assertFalse(getDao().isManaged(entityOne));

        UnitOfWork createAndCheck = new UnitOfWork() {
            @Override
            public void execute() throws Exception {
                EN entityOne = createValidEntity();
                assertFalse(getDao().isManaged(entityOne));
                getDao().persist(entityOne);
                assertTrue(getDao().isManaged(entityOne));
            }
        };
        getDao().runTransactional(createAndCheck);
    } */

    private <E> List<E> getRandomSublist(List<E> from){
        if(from.size() < 5) return from;
        int fromIdx = getRandomInt(0, from.size() - 2);
        int toIdx = getRandomInt(fromIdx + 1, from.size());
        return from.subList(fromIdx, toIdx);
    }

    private int getRandomInt(int lowerBound, int upperbound){
       Random rand = new Random();
       int result;
        while((result = rand.nextInt() % upperbound) < lowerBound);
        return result;
    }

}
