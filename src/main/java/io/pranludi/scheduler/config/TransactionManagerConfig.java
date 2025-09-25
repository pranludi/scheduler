package io.pranludi.scheduler.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // 트랜잭션 관리 활성화. @Transactional 을 사용해 선언적 트랜잭션으로 관리가 가능
@EnableJpaRepositories(
    entityManagerFactoryRef = "batchEntityManagerFactory",
    transactionManagerRef = "batchTransactionManager",
    basePackages = "io.pranludi.scheduler.data"
)
public class TransactionManagerConfig {

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Value("${spring.jpa.properties.hibernate.storage-engine}")
    private String storageEngine;

    @Value("${spring.jpa.properties.hibernate.show-sql}")
    private String showSql;

    @Value("${spring.jpa.properties.hibernate.hbm2ddl-auto}")
    private String hbm2ddlAuto;

    /**
     * JPA 구현체에 대한 설정을 반환
     */
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true); // 로그 출력
        return jpaVendorAdapter;
    }

    /**
     * 엔티티 매니저 팩토리 빈 생성
     */
    @Bean(name = "batchEntityManagerFactory")
    @Primary // 기본 EntityManagerFactory 설정
    public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory(
        @Qualifier("batchDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(hibernateJpaProperties());
        em.setPackagesToScan("com.ani.study.domain.entity");  // JPA 엔티티 위치 설정
        return em;
    }

    /**
     * 하이버네이트 관련 설정 반환
     */
    private Map<String, ?> hibernateJpaProperties() {
        HashMap<String, String> properties = new HashMap<>();

        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.dialect.storage_engine", storageEngine);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        return properties;
    }

    /**
     * 트랜잭션 매니저 설정
     */
    @Bean(name = "batchTransactionManager")
    @Primary
    public PlatformTransactionManager batchTransactionManager(@Qualifier("batchEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}