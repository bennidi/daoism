package net.engio.daoism.dao.spex;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NumberAttribute<N extends Number & Comparable> extends ComparableAttribute<NumberAttribute, N> {

    public NumberAttribute(String path) {
        super(path);
    }
}
