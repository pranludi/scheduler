package io.pranludi.scheduler;

import io.pranludi.scheduler.config.BatchScheduler;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration

public class QuartzTestJobConfiguration {

    private final Logger log = LoggerFactory.getLogger(QuartzTestJobConfiguration.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;

    public QuartzTestJobConfiguration(JobRepository jobRepository, PlatformTransactionManager batchTransactionManager) {
        this.jobRepository = jobRepository;
        this.batchTransactionManager = batchTransactionManager;
    }

    private int itemReaderCnt = 0;

    @Bean
    public Job quartzTestJob() {
        return new JobBuilder("quartzTestJob", jobRepository)
            .start(quartzTestStep())
            .build();
    }

    @Bean
    public Step quartzTestStep() {
        return new StepBuilder("quartzTestStep", jobRepository)
            .<Integer, Integer>chunk(10, batchTransactionManager)
            .reader(quartzTestItemReader())
            .writer(quartzTestItemWriter())
            .build();
    }

    @Bean
    public ItemReader<Integer> quartzTestItemReader() {
        return () -> {
            itemReaderCnt++;
            if (itemReaderCnt == 11) {
                itemReaderCnt = 0;
                return null;
            }
            ;

            int rtnCnt = itemReaderCnt * 2;

            log.info("[quartzTestItemReader] rtnCnt : {}", rtnCnt);
            return rtnCnt;
        };
    }

    @Bean
    public ItemWriter<Integer> quartzTestItemWriter() {
        return chunk -> {
            log.info("[quartzTestItemWriter] items : {}", chunk);
            log.info("Hello Quartz!!");
        };
    }

    // 스케줄러 설정
    @Bean
    public JobDetailFactoryBean QuartzTestJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(BatchScheduler.class);
        factoryBean.setDescription("Invoke A Job");
        factoryBean.setDurability(true);
        factoryBean.setName("quartzTestJob");
        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean QuartzTestTrigger(JobDetail QuartzTestJobDetail) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(QuartzTestJobDetail);
        trigger.setCronExpression("*/10 * * * * ?"); // 10초마다 실행하는 Cron 표현식
        return trigger;
    }

    @Bean
    public Scheduler quartzTestScheduler(SchedulerFactoryBean factoryBean, JobDetail QuartzTestJobDetail, Trigger QuartzTestTrigger) throws Exception {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.scheduleJob(QuartzTestJobDetail, QuartzTestTrigger);  // 잡과 트리거를 스케줄러에 등록
        scheduler.start();  // 스케줄러 시작
        return scheduler;
    }
}
