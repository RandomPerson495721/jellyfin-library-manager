package dev.partin.james.jellyfinlibrarymanager.api.model.resources;

import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
    @Setter
    protected double lengthSeconds = -1.0;

    //Assume an average of 30 minutes per file
    @OneToMany(cascade = CascadeType.ALL)
    protected List<JPAFile> segments = new ArrayList<>((int) Math.ceil((30.0 * 60.0) / segmentLengthSeconds));

    public void addSegment(JPAFile segment) {
        segments.add(segment);
    }
}
