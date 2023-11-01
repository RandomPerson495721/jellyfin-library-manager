package dev.partin.james.jellyfinlibrarymanager.api.model.resources;

import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
public abstract class IResource {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Getter
    @Transient //For now, I don't want to store this variable in the database
    protected final double segmentLengthSeconds = 60.0;

    @Getter
    @Transient
    protected String fileExtension;

    @Getter
    @Setter
    protected double lengthSeconds = -1.0;

    @PostLoad
    protected void reload() {
        fileExtension = file.get().getName().substring(file.get().getName().lastIndexOf('.') + 1);
    }


    //Assume an average of 30 minutes per file
    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    protected List<JPAFile> segments = new ArrayList<>((int) Math.ceil((30.0 * 60.0) / segmentLengthSeconds));

    public void addSegment(JPAFile segment) {
        segments.add(segment);
    }

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    protected JPAFile file;

    public IResource(JPAFile file) {
        super();
        this.file = file;
    }

    public IResource() {}
}
