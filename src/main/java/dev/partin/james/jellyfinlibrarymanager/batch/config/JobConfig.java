package dev.partin.james.jellyfinlibrarymanager.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;

public class JobConfig {
    @Bean
    public Job randomJob(JobRepository jobRepository) {
        return new JobBuilder("randomJob", jobRepository)
                  .start(step1())
                .build();
    }

    private Step step1() {
        return null;
    }
}
