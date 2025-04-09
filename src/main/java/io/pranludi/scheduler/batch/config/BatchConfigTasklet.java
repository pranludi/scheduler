package io.pranludi.scheduler.batch.config;

import io.pranludi.scheduler.data.LogEntity;
import io.pranludi.scheduler.data.LogJpaRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfigTasklet {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LogJpaRepository logJpaRepository;

    public BatchConfigTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager, LogJpaRepository logJpaRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.logJpaRepository = logJpaRepository;
    }

    // 로그 등록을 위한 Job
    @Bean
    public Job logRegisterJob() {
        return new JobBuilder("logRegisterJob", jobRepository)
            .start(contentsLogRegisterStep()) // job이 처음 시작될 때 실행되는 step
            .next(dateLogRegisterStep()) // 이전 step이 끝나면 실행되는 step
            .preventRestart() // job 실행이 실패할 경우 재시작 막기
            .build();
    }

    @Bean
    public Step contentsLogRegisterStep() {
        return new StepBuilder("contentsLogRegisterStep", jobRepository)
            .tasklet(
                (contribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();

                    logJpaRepository.save(
                        new LogEntity(
                            "Tasklet - " + jobParameters.get("uuid"),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        )
                    );
                    // 배치 종료
                    return RepeatStatus.FINISHED;
                }
                , transactionManager) // step에서 실행될 tasklet
            .build();
    }

    // date가 입력된 로그 등록을 위한 Step
    @Bean
    public Step dateLogRegisterStep() {
        return new StepBuilder("dateLogRegisterStep", jobRepository)
            .tasklet(
                (contribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                    // 로그 데이터 적재
                    logJpaRepository.save(
                        new LogEntity(
                            "uuid - " + jobParameters.get("uuid"),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            )
                        )
                    );

                    // 랜덤값이 조건을 넘는 경우 배치 종료
                    if (Math.random() * 10 >= 8) {
                        return RepeatStatus.FINISHED;
                    }

                    // 랜덤값이 조건을 넘지 않는 경우 tasklet 처리 반복
                    return RepeatStatus.CONTINUABLE;
                }
                , transactionManager) // step에서 실행될 tasklet
            .build();
    }

}
