package io.pranludi.scheduler.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class QuartzConfig {

    @Bean("TEST_QUARTZ_BEAN")
    public SchedulerFactoryBean schedulerFactoryBean(
        @Qualifier(value = "batchDataSource") DataSource dataSource,
        @Qualifier(value = "batchTransactionManager") PlatformTransactionManager transactionManager
    ) throws Exception {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        // Spring의 DI(의존성 주입)를 사용하는 JobFactory 설정
        schedulerFactoryBean.setJobFactory(new SpringBeanJobFactory());

        // DataSource 및 트랜잭션 설정
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);

        // 기존 잡을 덮어쓸지 여부
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        return schedulerFactoryBean;

    }
}
