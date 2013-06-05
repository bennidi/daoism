package net.engio.common.persistence.spec.spex;

import net.engio.common.domain.VServer;
import net.engio.common.domain.VServerDao;
import net.engio.daoism.dao.spex.QueryGenerator;
import net.engio.daoism.test.JUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/common-applicationContext.xml")
public class TestQueryGenerator extends JUnitTest{

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private VServerDao dao;

    @Test
    public void testSimpleQueryGeneration(){

        dao.persist(new VServer());

        QueryGenerator queryGen = new QueryGenerator();
        CriteriaQuery query = queryGen.buildQuery(VServer.Generated.GreaterThan(new Date(System.currentTimeMillis())),  VServer.class, em.getCriteriaBuilder());

        List result = em.createQuery(query).getResultList();

        assertEquals(1, result.size());

    }

    @Test
    public void testGreaterThan(){

        dao.persist(new VServer()
                .setNumberOfNics(10)
                .setNumberOfPorts(10L));

        dao.persist(new VServer()
                .setNumberOfNics(10)
                .setNumberOfPorts(5L));

        QueryGenerator queryGen = new QueryGenerator();

        CriteriaQuery moreNicsThanPorts = queryGen.buildQuery(VServer.Nics.GreaterThan(VServer.Ports),VServer.class, em.getCriteriaBuilder());
        List result = em.createQuery(moreNicsThanPorts).getResultList();
        assertEquals(1, result.size());

        CriteriaQuery morePortsThanNics = queryGen.buildQuery(VServer.Ports.GreaterThan(VServer.Nics), VServer.class, em.getCriteriaBuilder());
        result = em.createQuery(morePortsThanNics).getResultList();
        assertEquals(0, result.size());

        CriteriaQuery moreThanThreePorts = queryGen.buildQuery(VServer.Ports.GreaterThan(3),  VServer.class, em.getCriteriaBuilder());
        result = em.createQuery(moreThanThreePorts).getResultList();
        assertEquals(2, result.size());

    }

    @Test
    public void testOr(){

        dao.persist(new VServer()
                .setNumberOfNics(10)
                .setNumberOfPorts(10L));

        dao.persist(new VServer()
                .setNumberOfNics(10)
                .setNumberOfPorts(5L));

        QueryGenerator queryGen = new QueryGenerator();

        CriteriaQuery moreNicsThanPorts = queryGen.buildQuery(VServer.Nics.GreaterThan(VServer.Ports).Or(VServer.Ports.GreaterThan(8)),  VServer.class,  em.getCriteriaBuilder());
        List result = em.createQuery(moreNicsThanPorts).getResultList();
        assertEquals(2, result.size());
    }

    @Test
    public void testCount(){

        dao.persist(new VServer()
                .setNumberOfNics(9)
                .setNumberOfPorts(10L));

        dao.persist(new VServer()
                .setNumberOfNics(10)
                .setNumberOfPorts(5L));

        Long count = dao.count(VServer.Nics.GreaterThan(VServer.Ports));
        assertEquals(1L, count);
    }
}
