package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class jobDefinitionStatus {
    private String stepName;
    private boolean finished;
    private boolean failed;
    private float progress;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public jobDefinitionStatus() {
        this.stepName = "";
        this.finished = false;
        this.failed = false;
        this.progress = 0;
    }

    public jobDefinitionStatus(String stepName, boolean finished, boolean failed, float progress) {
        this.stepName = stepName;
        this.finished = finished;
        this.failed = failed;
        this.progress = progress;
    }

    public jobDefinitionStatus(String stepName) {
        this.stepName = stepName;
        this.finished = false;
        this.failed = false;
        this.progress = 0;
    }

}
