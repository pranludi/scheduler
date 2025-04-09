package io.pranludi.scheduler.batch.job;

import io.pranludi.scheduler.batch.config.BatchConfigChunk;
import io.pranludi.scheduler.batch.config.BatchConfigTasklet;
import java.util.Date;
import java.util.UUID;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogSchedule {

    private final JobLauncher jobLauncher;
    private final BatchConfigTasklet batchConfigTasklet;
    private final BatchConfigChunk batchConfigChunk;

    public LogSchedule(JobLauncher jobLauncher, BatchConfigTasklet batchConfigTasklet, BatchConfigChunk batchConfigChunk) {
        this.jobLauncher = jobLauncher;
        this.batchConfigTasklet = batchConfigTasklet;
        this.batchConfigChunk = batchConfigChunk;
    }

    @Scheduled(cron = "0/10 * * * * ?") // 10초마다 Job 실행, cron 표현식 활용 (초 분 시 일 월 요일)
    public void logRegister() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(
            batchConfigTasklet.logRegisterJob(),
            new JobParametersBuilder()
                .addString("uuid", UUID.randomUUID().toString())
                .addLong("currentTime", new Date().getTime())
                .toJobParameters()
        );
    }

    @Scheduled(fixedRate = 20000) // 메서드가 실행된 시간부터 20초마다 Job 실행
    public void logUpdate() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(
            batchConfigChunk.logUpdateJob(),
            new JobParametersBuilder()
                .addString("uuid", UUID.randomUUID().toString())
                .addLong("currentTime", new Date().getTime())
                .toJobParameters()
        );
    }
}
