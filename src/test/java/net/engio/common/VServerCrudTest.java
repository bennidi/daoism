package net.engio.common;

import net.engio.common.base.SpringAwareCrudTest;
import net.engio.common.domain.Queries;
import net.engio.common.domain.VServerDao;
import net.engio.daoism.dao.ITypedDao;
import net.engio.common.domain.VServer;
import net.engio.daoism.dao.jpa.UnitOfWork;
import net.engio.daoism.dao.query.LockType;
import net.engio.daoism.dao.query.Options;
import net.engio.daoism.dao.query.Query;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.TransactionRequiredException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class VServerCrudTest extends SpringAwareCrudTest<String, VServer> {


    @Autowired
    private VServerDao dao;

    @Override
    protected VServer createValidEntity() {
        VServer vserver = new VServer();
        vserver.setHost(System.currentTimeMillis() + "");
        return vserver;
    }

    @Override
    protected void modifyEntity(VServer which) {
        which.setHost(UUID.randomUUID().toString());
    }

    @Override
    protected ITypedDao<String, VServer> getDao() {
        return dao;
    }

    @Override
    protected void addEntities(List<VServer> entities) {
        entities.add(createValidEntity());
        entities.add(createValidEntity());
        entities.add(createValidEntity());
    }

    public void testPeformance(){
        float count = 10000;
        float firstRun,secondRun, thirdRun;

        final List<VServer> entities = new LinkedList<VServer>();
        for(int i = 0; i < count; i++){
            entities.add(createValidEntity());
        }
        long start = System.currentTimeMillis();
        dao.persistAll(entities);
        long end = System.currentTimeMillis();

        firstRun = (count / (end - start)) * 1000f;


        entities.clear();
        for(int i = 0; i < count; i++){
            entities.add(createValidEntity());
        }
        start = System.currentTimeMillis();
        for(VServer entity : entities)
            dao.persist(entity);
        end = System.currentTimeMillis();

        secondRun = (count / (end - start)) * 1000f;



        entities.clear();
        for(int i = 0; i < count; i++){
            entities.add(createValidEntity());
        }
        start = System.currentTimeMillis();
        dao.runTransactional(new UnitOfWork() {
            @Override
            public void execute() throws Exception {
                int i=0;
                for(VServer entity : entities){
                    dao.persist(entity);
                    if(i++ % 220 == 0)dao.flush();
                }

            }
        });
        end = System.currentTimeMillis();
        thirdRun = (count / (end - start)) * 1000f;

        System.out.println("Performance per entity for " + count + " entities with persistAll : " +  firstRun);
        System.out.println("Performance per entity for " + count + " entities with persist : " +  secondRun );
        System.out.println("Performance per entity for " + count + " entities with persist and flush : " +  thirdRun );

    }


    public void ApiSampleShowCase() {
        VServer vServer1 = createValidEntity();
        VServer vServer2 = createValidEntity();


        dao.persist(vServer1);
        dao.isManaged(vServer1);

        List<VServer> vServers = new LinkedList<VServer>();
        vServers.add(vServer1);
        vServers.add(vServer2);
        vServers.add(createValidEntity());
        vServers.add(createValidEntity());

        vServers = dao.persistAll(vServers);

        vServer1 = dao.findById(vServer1.getId());

        final String vServer1Id = vServer1.getId();
        dao.runTransactional(new UnitOfWork() {
            @Override
            public void execute() throws Exception {
                dao.findById(vServer1Id, Options.Refresh().Lock(LockType.PessimisticWrite));
            }
        });



        dao.findById(vServer1.getId());
        try {
            dao.findById(vServer1Id, Options.Refresh().Lock(LockType.PessimisticWrite));
        } catch (TransactionRequiredException e) {
            // this exception should be thrown because no transaction context is available
        }


        dao.findAll(Queries.VServer.ByUuid(vServer1.getUuid()));
        dao.findAll(Query.Named(Queries.VServer.ByUuid).set("UUID").to(vServer1.getUuid()));
        dao.countAll();


        modifyEntity(vServer1);
        dao.persist(vServer1);
        dao.isSameVersion(vServer1, dao.findById(vServer1.getId()));

    }
}
