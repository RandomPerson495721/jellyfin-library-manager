package dev.partin.james.jellyfinlibrarymanager.helpers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.io.File;
import java.net.URI;

@Entity
public class JPAFile {
    private String absPath;

    private File file = null;

    @PostLoad
    @RequiresNonNull("absPath")
    private void generateFile() {
        this.file = new File(absPath);
    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public JPAFile(String absPath) {
        this.absPath = absPath;
        this.file = new File(absPath);
    }

    public JPAFile(String parent, String child) {
        this.absPath =  new File(parent, child).getAbsolutePath();
        this.file = new File(absPath);
    }

    public JPAFile(URI uri) {
        this.absPath = new File(uri).getAbsolutePath();
        this.file = new File(absPath);
    }

    public JPAFile(File file) {
        this.absPath = file.getAbsolutePath();
        this.file = new File(absPath);
    }

    public File get() {
        return this.file;
    }

    public JPAFile() {}

    @Override
    public String toString() {
        return this.absPath;
    }

    @Override
    public JPAFile clone() {
        return new JPAFile(this.absPath);
    }
}
