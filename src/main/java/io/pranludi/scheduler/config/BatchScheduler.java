package io.pranludi.scheduler.config;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler extends QuartzJobBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    public BatchScheduler(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName(); // JobDetail에서 잡 이름을 가져옴
        try {
            Job job = jobRegistry.getJob(jobName);  // JobRegistry에서 Job을 동적으로 조회
            jobLauncher.run(job, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        } catch (Exception e) {
            log.error("fail to execute job : {}, {}", jobName, this.getClass().getSimpleName(), e);
            throw new JobExecutionException(e);
        }
    }
}