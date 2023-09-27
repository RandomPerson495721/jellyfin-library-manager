package dev.partin.james.jellyfinlibrarymanager.api.model.resources;

import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

@Entity
public abstract class IResource {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private TreeMap<Integer, JPAFile> segments = new TreeMap<>();
}
