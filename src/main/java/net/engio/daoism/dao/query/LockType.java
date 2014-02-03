package net.engio.daoism.dao.query;

/**
 * Different lock types as potentially supported by various persistence technologies.
 * Not all lock types need to be provided by a specific persistence technology. Semantics
 * of the same lock type may also vary.
 *
*/
public enum LockType {
    Optimistic,PessimisticWrite, Default, None
}
