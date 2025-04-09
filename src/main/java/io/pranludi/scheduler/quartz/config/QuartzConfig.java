package io.pranludi.scheduler.quartz.config;

import io.pranludi.scheduler.quartz.job.SampleJob;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;

    public QuartzConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // 스케줄링 설정
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        // @Autowired 사용을 위한 설정
        AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory = new AutowiringSpringBeanJobFactory();
        autowiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(autowiringSpringBeanJobFactory);

        // quartz scheduling 관련 설정
        schedulerFactoryBean.setQuartzProperties(properties());

        // 스케줄링을 위한 Trigger 등록
        schedulerFactoryBean.setTriggers(simpleTriggerFactoryBean().getObject());

        return schedulerFactoryBean;
    }

    // JobDetail 설정
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        // 실행될 Job
        jobDetailFactoryBean.setJobClass(SampleJob.class);
        // 식별을 위한 Identity 등록 (group)
        jobDetailFactoryBean.setGroup("SampleJobDetailGroup");
        // 식별을 위한 Identity 등록 (name)
        jobDetailFactoryBean.setName("SampleJobDetailName");
        return jobDetailFactoryBean;
    }

    // Trigger 설정
    @Bean
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean() {
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean().getObject()));
        // 5초 간격으로 스케줄링 처리
        simpleTriggerFactoryBean.setRepeatInterval(5000);
        // 식별을 위한 Identity 등록 (group)
        simpleTriggerFactoryBean.setGroup("SampleTriggerGroup");
        // 식별을 위한 Identity 등록 (name)
        simpleTriggerFactoryBean.setName("SampleTriggerName");
        return simpleTriggerFactoryBean;
    }

//    @Bean
//    public JobDetail sampleJobDetail() {
//        return JobBuilder.newJob(SampleJob.class)
//            .withIdentity("sampleJob")
//            .storeDurably()
//            .build();
//    }

//    @Bean
//    public Trigger sampleJobTrigger(JobDetail jobDetail) {
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//            .withIntervalInSeconds(20)
//            .repeatForever();
//
//        return TriggerBuilder.newTrigger()
//            .forJob(jobDetail)
//            .withIdentity("sampleTrigger")
//            .withSchedule(scheduleBuilder)
//            .build();
//    }

    // 스케줄링 Properties 설정
    @Bean
    public Properties properties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        // quartz.properties 연결하기
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties properties = null;

        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            System.out.println("quartzProperties set error");
        }

        return properties;
    }

}