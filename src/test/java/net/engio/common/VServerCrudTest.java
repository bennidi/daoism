package net.engio.common;

import net.engio.common.base.SpringAwareCrudTest;
import net.engio.common.domain.Queries;
import net.engio.common.domain.VServerDao;
import net.engio.common.persistence.dao.ITypedDao;
import net.engio.common.domain.VServer;
import net.engio.common.persistence.dao.jpa.UnitOfWork;
import net.engio.common.persistence.dao.query.LockType;
import net.engio.common.persistence.dao.query.Options;
import net.engio.common.persistence.dao.query.Query;
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

    @Test
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
