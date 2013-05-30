package net.engio.common.domain;

import net.engio.common.persistence.dao.IDao;
import net.engio.common.persistence.dao.TypedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VServerDao extends TypedDao<String, VServer> {

    @Autowired
    private DbDao dbDao;

    public VServerDao() {
        super(String.class, VServer.class);
    }


    @Override
    protected IDao getDao() {
        return dbDao;
    }
}
