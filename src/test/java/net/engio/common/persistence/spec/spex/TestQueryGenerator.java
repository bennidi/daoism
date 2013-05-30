package net.engio.common.persistence.spec.spex;

import net.engio.common.domain.VServer;
import net.engio.common.domain.VServerDao;
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
public class TestQueryGenerator {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private VServerDao dao;

    @Test
    public void testSimpleQueryGeneration(){

        dao.persist(new VServer());

        QueryGenerator queryGen = new QueryGenerator();
        CriteriaQuery query = queryGen.buildQuery(VServer.Generated.GreaterThan(new Date(System.currentTimeMillis())), em, VServer.class);

        List result = em.createQuery(query).getResultList();

        System.out.println("size: " + result.size());

    }
}
