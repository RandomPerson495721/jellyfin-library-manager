package dev.partin.james.jellyfinlibrarymanager.helpers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.util.Date;

@Entity
public class JPAFile {
    private String absPath;

    @OneToMany(cascade = CascadeType.ALL)
    private double[] time = {0.0, 0.0};

    public void setTime(double start, double end) {
        time = new double[]{start, end};
    }

    public double getStartTime() {
        return time[0];
    }

    public double getEndTime() {
        return time[1];
    }

    //I want to have a way to lease a file, so that it can't be deleted while it's being used.
    //I also want to have a way to check if a file is leased, so that I can delete it if it's not.

    @Transient
    private Date leaseExpiration = null;
    @Transient
    private Object leaseHolder = null;

    public Date acquireLease(Object leaseHolder) {
        this.leaseHolder = leaseHolder;
        //TODO: Make expiration configurable
        this.leaseExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        return new Date(this.leaseExpiration.getTime());
    }

    public Date renewLease(Object leaseHolder) {
        if (this.leaseHolder != leaseHolder) {
            throw new IllegalArgumentException("The lease holder does not match the current lease holder.");
        }
        if (this.leaseExpiration == null) {
            return null;
        }
        if (this.leaseExpiration.before(new Date())) {
            this.releaseLease();
            return null;
        }
        this.leaseExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        return new Date(this.leaseExpiration.getTime());
    }

    public void releaseLease() {
        this.leaseHolder = null;
        this.leaseExpiration = null;
    }

    public boolean isLeased() {
        if (this.leaseExpiration == null) {
            return false;
        }
        if (this.leaseExpiration.before(new Date())) {
            this.releaseLease();
            return false;
        }
        return true;
    }

    @Transient
    private File file;

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

    public void setAbsPath(@NotNull String absPath) {
        this.absPath = absPath;
    }

    public JPAFile(@NotNull String absPath) {
        this.absPath = absPath;
        this.file = new File(absPath);
    }

    public JPAFile(@NotNull String parent, @NotNull String child) {
        this.absPath = new File(parent, child).getAbsolutePath();
        this.file = new File(absPath);
    }

    public JPAFile(@NotNull URI uri) {
        this.absPath = new File(uri).getAbsolutePath();
        this.file = new File(absPath);
    }

    public JPAFile(@NotNull File file) {
        this.absPath = file.getAbsolutePath();
        this.file = new File(absPath);
    }

    public File get() {
        return this.file;
    }

    public JPAFile() {
    }

    @Override
    public String toString() {
        return this.absPath;
    }
}
