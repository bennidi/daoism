package net.engio.common.domain;

import net.engio.daoism.dao.query.Query;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Queries {

    public static class VServer{

        public static final String ByUuid = "vserver-by-uuid";
        public static final String ByHost = "vserver-by-host";

        public static final Query.NamedQuery ByUuid(String uuid){
            return Query.Named(ByUuid).set("UUID").to(uuid);
        }

        public static final Query.NamedQuery ByHost(String pServerUuid){
            return Query.Named(ByHost).set("HOST").to(pServerUuid);
        }

    }
}
