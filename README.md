daoism
======

An example implementation of the DAO pattern for Java. Built around a technology agnostic interface
that allows to plug-in different persistence technologies (IPersistenceProvider) and is shipped with
a JPA 2 implementation of this provider. Offers generic CRUD tests out-of-the-box, just extend the CrudTest
base class.

This code has been developed for small scale systems with some ~100 domain classes and up to some million
entities managed by any ORM like persistence technology. The used abstractions should allow to plug-in different
persistence technologies (document storage) as well. The JPA implementation of the persistence provider has
been tested quite thoroughly. Everybody who wants to start building a system based on JPA can use this
code directly or might get inspirations from it. Read this [article](http://codeblock.engio.net/?p=180) for
more details on this particular DAO pattern implementation.

You can also take a look at the [javadoc](http://bennidi.github.io/daosim)

The test code shows some basic use cases of the API and comes with integration of hibernate and eclipse link
on top of an H2 database. Spring is used to wire up the objects but any managed or unmanaged environment
will work.

```
        // wired by Spring but maybe obtained in any other way, of course
        @Autowired
        VServerDao dao;

        VServer vServer1 = new VServer()
        VServer vServer2 = new VServer();


        dao.persist(vServer1);

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


        List<VServer> byUUid = dao.findAll(Queries.VServer.ByUuid(vServer1.getUuid()));
        byUUid = dao.findAll(Query.Named(Queries.VServer.ByUuid).set("UUID").to(vServer1.getUuid()));
        long howManyVServers = dao.countAll();


        modifyEntity(vServer1);
        dao.persist(vServer1);
        dao.isSameVersion(vServer1, dao.findById(vServer1.getId()));
