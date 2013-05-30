package net.engio.common.persistence.spec.spex;

import net.engio.common.spex.attr.IAttribute;
import net.engio.common.xpress.ast.nodes.ValueNode;
import net.engio.common.xpress.eval.*;
import net.engio.common.xpress.operators.Equals;
import net.engio.common.xpress.operators.GreaterThan;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Date;

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
    }


    public CriteriaQuery buildQuery(ISpecification spec, EntityManager em, Class rootEntity){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(rootEntity);
        Root queryRoot = query.from(rootEntity);
        Predicate predicate = evaluate(spec, new EvaluationContext(this).push(new Bindings.Impl()
                .bind(builder).to("builder")
                .bind(query).to("query")
                .bind(queryRoot).to("root")));
        query.where(predicate);
        return query;
    }

    public static class GreaterThanFunction implements EvalFunction<GreaterThan.GreaterThanNode, Predicate> {


        @Override
        public Predicate evaluate(GreaterThan.GreaterThanNode node, EvaluationContext context) {
            CriteriaBuilder cb = context.lookup("builder");
            CriteriaQuery q = context.lookup("query");
            Root root = context.lookup("root");

            // left should only point to a DateAttribute
            DateAttribute left = ((ValueNode)node.getLeft()).getValue();
            Date value =  ((ValueNode)node.getRight()).getValue();

            return cb.greaterThan(QueryGenerator.<Comparable>resolveAttribute(root, left.getPath()), value);
            /*
            return cb.greaterThan(QueryGenerator.<Comparable>resolveAttribute(root, context.evaluate(node.getLeft())),
                    context.evaluate(node.getRight()))


            if(value != null)return cb.greaterThan(this.<Comparable>resolveAttribute(root, attribute), value);
            else return cb.greaterThan(this.<Comparable>resolveAttribute(root, attribute), this.<Comparable>resolveAttribute(root, otherAttribute));

            return context.evaluate(node.getLeft()).equals(context.evaluate(node.getRight()));
            return null;*/
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
