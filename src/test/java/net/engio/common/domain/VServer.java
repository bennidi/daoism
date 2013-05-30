package net.engio.common.domain;

import net.engio.common.persistence.dao.query.Query;
import net.engio.common.persistence.spec.spex.DateAttribute;
import org.hibernate.ejb.QueryHints;

import javax.persistence.*;
import java.util.Date;


@NamedQueries({
        @NamedQuery(
                name = Queries.VServer.ByUuid,
                query = "SELECT vs FROM VServer vs WHERE vs.uuid = :UUID"),
        @NamedQuery(
                name = Queries.VServer.ByHost,
                query = "SELECT vs FROM VServer vs WHERE vs.host = :HOST")
})
@Entity
public class VServer extends VResource{

    private static  final long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 *24 * 2;

    @Temporal(value = TemporalType.DATE)
    private Date generated = new Date(tomorrow);

    public static final DateAttribute Generated = new DateAttribute("generated");

    @Column(name = "v_host")
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
