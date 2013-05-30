package net.engio.common.domain;


import net.engio.common.persistence.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@MappedSuperclass
public abstract class VResource implements Entity<String> {

    @Version
    private long version = -1;

    private Timestamp tsCreated = null;

    private Timestamp tsLastModified = null;

    @PrePersist
    public void prePersist(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if(tsCreated == null)tsCreated = now;
        tsLastModified = now;
    }


    @Column(name = "v_name")
    private String name;

    @Id
    @Column(name = "v_uuid")
    private String uuid = UUID.randomUUID().toString();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getId() {
        return getUuid();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VResource vResource = (VResource) o;

        if (!uuid.equals(vResource.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
