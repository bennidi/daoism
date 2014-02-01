package net.engio.common.domain;

import net.engio.daoism.dao.IPersistenceProvider;
import net.engio.daoism.dao.TypedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VServerDao extends TypedDao<String, VServer> {

    @Autowired
    private DbPersistenceProvider persistenceProvider;

    public VServerDao() {
        super(String.class, VServer.class);
    }


    @Override
    protected IPersistenceProvider getPersistenceProvider() {
        return persistenceProvider;
    }
}
