package net.engio.daoism.dao.spex;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/29/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Attribute implements IAttribute{

    public String path;

    protected Attribute(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
