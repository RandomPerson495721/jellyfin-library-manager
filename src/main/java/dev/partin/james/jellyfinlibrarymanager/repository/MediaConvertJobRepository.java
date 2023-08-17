package dev.partin.james.jellyfinlibrarymanager.repository;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaConvertJobRepository extends CrudRepository<Job, Long> {
    @Query(value = "SELECT j FROM Job j WHERE j.fileName = :fileName")
    List<Job> getJobByFileName(@Param("fileName") String fileName);
}
