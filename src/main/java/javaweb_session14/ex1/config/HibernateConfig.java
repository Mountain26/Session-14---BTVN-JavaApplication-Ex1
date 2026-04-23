package javaweb_session14.ex1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean factoryBean(DataSource dataSource){
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("javaweb_session14.ex1.model.entity");
        Properties prop = new Properties();
        prop.put("hibernate.show_sql",env.getProperty("hibernate.show_sql"));
        prop.put("hibernate.hbm2ddl.auto",env.getProperty("hibernate.ddl-auto"));
        prop.put("hibernate.dialect",env.getProperty("hibernate.dialect"));
        prop.put("hibernate.format_sql",env.getProperty("hibernate.format_sql"));
        bean.setHibernateProperties(prop);
        return bean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(LocalSessionFactoryBean localSessionFactoryBean){
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(localSessionFactoryBean.getObject());
        return manager;
    }
}
