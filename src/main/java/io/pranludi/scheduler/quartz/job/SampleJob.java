package io.pranludi.scheduler.quartz.job;

import io.pranludi.scheduler.data.LogEntity;
import io.pranludi.scheduler.data.LogJpaRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleJob implements Job {

    @Autowired
    LogJpaRepository logJpaRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 작업 로직
        System.out.println("Executing Sample Job : " + LocalDateTime.now());
        // 로그 데이터 적재
        logJpaRepository.save(
            new LogEntity(
                "logRegisterJobContents",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
        );
    }
}
