package dev.partin.james.jellyfinlibrarymanager.repository;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaConvertJobRepository extends CrudRepository<Job, Long> {}
