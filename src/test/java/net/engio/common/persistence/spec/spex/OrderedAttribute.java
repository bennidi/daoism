package net.engio.common.persistence.spec.spex;


/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OrderedAttribute<T extends OrderedAttribute<T>> extends NavigableAttribute implements Ordered<T,ISpecification> {

    protected OrderedAttribute(String path) {
        super(path);
    }

    @Override
    public ISpecification Equals(T other) {
        return new ISpecification.SpecificationNode(new EqualsNode(this,other));
    }

    @Override
    public ISpecification GreaterThan(T other) {
        return new ISpecification.SpecificationNode(new GreaterThanNode(this,other));
    }

    @Override
    public ISpecification LessThan(T other) {
        return new ISpecification.SpecificationNode(new LessThanNode(this,other));
    }


    @Override
    public ISpecification IsNull(T other) {
        return new ISpecification.SpecificationNode(new IsNullNode(this,other));
    }

    @Override
    public Object GreaterOrEqual(Object other) {
        return new ISpecification.SpecificationNode(new GreaterOrEqualNode(this,other));
    }
}
