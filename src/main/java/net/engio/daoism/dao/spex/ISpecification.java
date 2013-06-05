package net.engio.daoism.dao.spex;

import net.engio.common.xpress.ast.NodeDecorator;
import net.engio.common.xpress.ast.nodes.Node;
import net.engio.common.xpress.operators.logical.And;
import net.engio.common.xpress.operators.logical.Or;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISpecification extends NodeDecorator, And<ISpecification>, Or<ISpecification>{

    public static class SpecificationNode extends NodeDecorator.Impl implements ISpecification{

        public SpecificationNode(Node node) {
            super(node);
        }

        @Override
        public ISpecification And(ISpecification... expressions) {
            return new SpecificationNode(new AndNode(this, expressions));
        }

        @Override
        public ISpecification Or(ISpecification... expressions) {
            return new SpecificationNode(new Or.OrNode(this, expressions));
        }
    }
}
