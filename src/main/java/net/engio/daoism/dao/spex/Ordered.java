package net.engio.daoism.dao.spex;

import net.engio.common.xpress.operators.GreaterOrEqual;
import net.engio.common.xpress.operators.GreaterThan;
import net.engio.common.xpress.operators.LessThan;
import net.engio.common.xpress.types.CComparable;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Ordered<IN, OUT> extends
        CComparable<IN, OUT>,
        GreaterThan<IN, OUT>,
        LessThan<IN, OUT>,
        GreaterOrEqual {
}
