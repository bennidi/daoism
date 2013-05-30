package net.engio.common.persistence.spec.spex;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/29/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NavigableAttribute {

    public String path;

    protected NavigableAttribute(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
