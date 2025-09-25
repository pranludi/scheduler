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
public class QuartzSecondTestJobConfiguration {

    private final Logger log = LoggerFactory.getLogger(QuartzSecondTestJobConfiguration.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;

    public QuartzSecondTestJobConfiguration(JobRepository jobRepository, PlatformTransactionManager batchTransactionManager) {
        this.jobRepository = jobRepository;
        this.batchTransactionManager = batchTransactionManager;
    }

    private int itemReaderCnt = 0;

    @Bean
    public Job quartzSecondTestJob() {
        return new JobBuilder("quartzSecondTestJob", jobRepository)
            .start(quartzSecondTestStep())
            .build();
    }

    @Bean
    public Step quartzSecondTestStep() {
        return new StepBuilder("quartzSecondTestStep", jobRepository)
            .<Integer, Integer>chunk(10, batchTransactionManager)
            .reader(quartzSecondTestItemReader())
            .writer(quartzSecondTestItemWriter())
            .build();
    }

    @Bean
    public ItemReader<Integer> quartzSecondTestItemReader() {
        return () -> {
            itemReaderCnt++;
            if (itemReaderCnt == 11) {
                itemReaderCnt = 0;
                return null;
            }
            ;

            int rtnCnt = itemReaderCnt;

            log.info("[quartzSecondTestItemReader] rtnCnt : {}", rtnCnt);
            return rtnCnt;
        };
    }

    @Bean
    public ItemWriter<Integer> quartzSecondTestItemWriter() {
        return chunk -> {
            log.info("[quartzSecondTestItemWriter] items : {}", chunk);
            log.info("Hello Quartz22222!!");
        };
    }

    // 스케줄러 설정
    @Bean
    public JobDetailFactoryBean QuartzSecondTestJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(BatchScheduler.class);
        factoryBean.setDescription("Second Job");
        factoryBean.setDurability(true);
        factoryBean.setName("quartzSecondTestJob");
        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean QuartzSecondTestTrigger(JobDetail QuartzSecondTestJobDetail) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(QuartzSecondTestJobDetail);
        trigger.setCronExpression("*/5 * * * * ?"); // 5초마다 실행하는 Cron 표현식
        return trigger;
    }

    @Bean
    public Scheduler quartzSecondTestScheduler(SchedulerFactoryBean factoryBean, JobDetail QuartzSecondTestJobDetail, Trigger QuartzSecondTestTrigger) throws Exception {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.scheduleJob(QuartzSecondTestJobDetail, QuartzSecondTestTrigger);  // 잡과 트리거를 스케줄러에 등록 QuartzSecondTestTrigger : Triggers의 Name이 들어간다.
        scheduler.start();  // 스케줄러 시작
        return scheduler;
    }
}
