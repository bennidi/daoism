package net.engio.daoism.dao.spex;


/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ComparableAttribute<T extends ComparableAttribute, S extends Comparable>
        extends Attribute
        implements ComparableType<T,ISpecification> {

    protected ComparableAttribute(String path) {
        super(path);
    }

    @Override
    public ISpecification Equals(T other) {
        return new ISpecification.SpecificationNode(new EqualsNode(this,other));
    }

    public ISpecification Equals(S other) {
        return new ISpecification.SpecificationNode(new EqualsNode(this,other));
    }

    @Override
    public ISpecification GreaterThan(T other) {
        return new ISpecification.SpecificationNode(new GreaterThanNode(this,other));
    }

    public ISpecification GreaterThan(S other) {
        return new ISpecification.SpecificationNode(new GreaterThanNode(this,other));
    }

    @Override
    public ISpecification LessThan(T other) {
        return new ISpecification.SpecificationNode(new LessThanNode(this,other));
    }

    public ISpecification LessThan(S other) {
        return new ISpecification.SpecificationNode(new LessThanNode(this,other));
    }


    @Override
    public ISpecification IsNull() {
        return new ISpecification.SpecificationNode(new IsNullNode(this));
    }

    @Override
    public ISpecification GreaterOrEqual(T other) {
        return new ISpecification.SpecificationNode(new GreaterOrEqualNode(this,other));
    }

    public ISpecification GreaterOrEqual(S other) {
        return new ISpecification.SpecificationNode(new GreaterOrEqualNode(this,other));
    }
}
