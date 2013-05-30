package net.engio.common.persistence.spec.spex;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/28/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateAttribute extends OrderedAttribute<DateAttribute> {

    private Date value;

    public DateAttribute(String path) {
        super(path);
    }

    public ISpecification GreaterThan(Date value){
        return new ISpecification.SpecificationNode(new GreaterThanNode(this, value));
    }
}
