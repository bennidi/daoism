package net.engio.daoism.dao.spex;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberAttribute extends OrderedAttribute<NumberAttribute> {

    public NumberAttribute(String path) {
        super(path);
    }

    public ISpecification GreaterThan(Number value){
        return new ISpecification.SpecificationNode(new GreaterThanNode(this, value));
    }
}
