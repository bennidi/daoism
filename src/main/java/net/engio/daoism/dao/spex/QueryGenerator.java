package net.engio.daoism.dao.spex;

import net.engio.common.xpress.ast.nodes.Node;
import net.engio.common.xpress.ast.nodes.ValueNode;
import net.engio.common.xpress.eval.EvalFunction;
import net.engio.common.xpress.eval.EvaluationContext;
import net.engio.common.xpress.eval.Interpreter;
import net.engio.common.xpress.operators.GreaterThan;
import net.engio.common.xpress.operators.LessThan;
import net.engio.common.xpress.operators.logical.And;
import net.engio.common.xpress.operators.logical.Or;

import javax.persistence.criteria.*;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGenerator extends Interpreter.Impl<QueryGenerator>{

    public QueryGenerator() {
        super();
        bindEvalFunction(new GreaterThanFunction(), GreaterThan.GreaterThanNode.class);
        bindEvalFunction(new OrFunction(), Or.OrNode.class);
    }


    public CriteriaQuery buildQuery(ISpecification spec, Class rootEntity, CriteriaBuilder builder ){
        CriteriaQuery query = builder.createQuery(rootEntity);
        Root queryRoot = query.from(rootEntity);
        if(spec != null)query.where(toJpaPredicate(spec, queryRoot, builder, query));
        return query;
    }

    public Predicate toJpaPredicate(ISpecification spec, Root rootEntity, CriteriaBuilder builder, CriteriaQuery query){
        Predicate predicate = evaluate(spec, new EvaluationContext(this)
                .bind(builder).to("builder")
                .bind(query).to("query")
                .bind(rootEntity).to("root"));
       return predicate;
    }

    public static class GreaterThanFunction implements EvalFunction<GreaterThan.GreaterThanNode, Predicate> {


        @Override
        public Predicate evaluate(GreaterThan.GreaterThanNode node, EvaluationContext context) {
            CriteriaBuilder cb = context.get("builder");
            Root root = context.get("root");

            // left should only point to a DateAttribute
            IAttribute left = ((ValueNode)node.getLeft()).getValue();
            Object right =  ((ValueNode)node.getRight()).getValue();

            if(IAttribute.class.isAssignableFrom(right.getClass())){
                return cb.greaterThan(QueryGenerator.<Comparable>resolveAttribute(root, left.getPath()),
                        QueryGenerator.<Comparable>resolveAttribute(root, ((IAttribute)right).getPath()));
            }
            else{
                return cb.greaterThan(QueryGenerator.<Comparable>resolveAttribute(root, left.getPath()), (Comparable)right);
            }
        }
    }

    public static class LessThanFunction implements EvalFunction<LessThan.LessThanNode, Predicate> {

        @Override
        public Predicate evaluate(LessThan.LessThanNode node, EvaluationContext context) {
            CriteriaBuilder cb = context.get("builder");
            Root root = context.get("root");

            // left should only point to a DateAttribute
            IAttribute left = ((ValueNode)node.getLeft()).getValue();
            Object right =  ((ValueNode)node.getRight()).getValue();

            if(IAttribute.class.isAssignableFrom(right.getClass())){
                return cb.lessThan(QueryGenerator.<Comparable>resolveAttribute(root, left.getPath()),
                        QueryGenerator.<Comparable>resolveAttribute(root, ((IAttribute)right).getPath()));
            }
            else{
                return cb.lessThan(QueryGenerator.<Comparable>resolveAttribute(root, left.getPath()), (Comparable)right);
            }
        }
    }


    public static class OrFunction implements EvalFunction<Or.OrNode, Predicate> {

        @Override
        public Predicate evaluate(Or.OrNode node, EvaluationContext context) {
            CriteriaBuilder cb = context.get("builder");

            Predicate[] conditions = new Predicate[node.getChildren().size()];
            int i = 0;
            for(Node child : node.getChildren()){
                conditions[i++] = context.evaluate(child);
            }
            return cb.or(conditions);
        }
    }

    public static class AndFunction implements EvalFunction<And.AndNode, Predicate> {

        @Override
        public Predicate evaluate(And.AndNode node, EvaluationContext context) {
            CriteriaBuilder cb = context.get("builder");

            Predicate[] conditions = new Predicate[node.getChildren().size()];
            int i = 0;
            for(Node child : node.getChildren()){
                conditions[i++] = context.evaluate(child);
            }
            return cb.and(conditions);
        }
    }



    public static <P> Path<P> resolveAttribute(Root root, String attribute) {
        String[] path = attribute.split("\\.");

        Path result = root.get(path[0]);

        for (int i = 1; i < path.length; i++) {
            result = result.get(path[i]);
        }
        return result;

    }


}
