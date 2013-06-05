package net.engio.common.domain;


import net.engio.daoism.dao.spex.DateAttribute;
import net.engio.daoism.dao.spex.NumberAttribute;

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
    @Column
    private Integer numberOfNics = 0;
    @Column
    private Long numberOfPorts = 0L;

    public static final DateAttribute Generated = new DateAttribute("generated");
    public static final NumberAttribute Nics = new NumberAttribute("numberOfNics");
    public static final NumberAttribute Ports = new NumberAttribute("numberOfPorts");

    @Column(name = "v_host")
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public VServer setNumberOfNics(Integer numberOfNics) {
        this.numberOfNics = numberOfNics;
        return this;
    }

    public VServer setNumberOfPorts(Long numberOfPorts) {
        this.numberOfPorts = numberOfPorts;
        return this;
    }
}
