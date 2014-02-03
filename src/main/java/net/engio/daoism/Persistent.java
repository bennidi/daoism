package net.engio.daoism;

import java.io.Serializable;

/**
 * A domain object (or domain class) represents a relevant concept of the problem domain and as such constitutes a well defined
 * part of the domain model. The domain model consists of an arbitrary number of domain objects, some of which might be coupled
 * and have strong relationships.
 * 
 * Since domain objects also represent the state of the application, they are persistent objects. The underlying storage layer
 * is abstracted using an implementation of {@link net.engio.daoism.dao.IPersistenceProvider}. The {@link net.engio.daoism.dao.IPersistenceProvider} interface is designed to be generic, meaning that any
 * domain object can be accessed using the same {@link net.engio.daoism.dao.IPersistenceProvider} instance.
 * For type-safety there also exists a typed interface {@link net.engio.daoism.dao.ITypedDao} which uses generics to offer the same methods as {@link net.engio.daoism.dao.IPersistenceProvider}
 * but limited by type to one domain object class.
 * Every domain object that implements this interface is automatically persistable using implementation of {@link net.engio.daoism.dao.IPersistenceProvider} or {@link ITypedDao}.
 *  
 * Since every persistent object needs a unique identifier (primary key) for storage
 * and retrieval the interface defines a getter and setter for this key.
 * 
 * @author Benjamin Diedrichsen
 * 
 * @param <KEY>
 *            The type of primary key used for identifying the domain object
 */

public interface Persistent<KEY extends Serializable> extends Serializable, Identifiable<KEY>, Versioned {


}
