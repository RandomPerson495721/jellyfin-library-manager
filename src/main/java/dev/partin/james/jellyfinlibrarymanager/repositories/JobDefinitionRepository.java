package dev.partin.james.jellyfinlibrarymanager.repositories;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDefinitionRepository extends CrudRepository<JobDefinition, Long> {
    @Query(value = "SELECT j FROM JobDefinition j WHERE j.fileName = :fileName")
    List<JobDefinition> getJobByFileName(@Param("fileName") String fileName);

}
