package net.engio.daoism;

/**
 * A versioned object specifies an incremental version number that allows to compare two instances
 * with the same primary key to find out which one is newer. This is used, for example, in optimistic locking
 * strategies
 */
public interface Versioned {

    long getVersion();

}
