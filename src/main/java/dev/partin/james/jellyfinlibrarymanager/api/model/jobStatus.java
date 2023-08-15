package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class jobStatus {
    private String stepName;
    private boolean finished;
    private boolean failed;
    private float progress;

    @Id
    private Long id;

    public jobStatus() {
        this.stepName = "";
        this.finished = false;
        this.failed = false;
        this.progress = 0;
    }

    public jobStatus(String stepName, boolean finished, boolean failed, float progress) {
        this.stepName = stepName;
        this.finished = finished;
        this.failed = failed;
        this.progress = progress;
    }

    public jobStatus(String stepName) {
        this.stepName = stepName;
        this.finished = false;
        this.failed = false;
        this.progress = 0;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
