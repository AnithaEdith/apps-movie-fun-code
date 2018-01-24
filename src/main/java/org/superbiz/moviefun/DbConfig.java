package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DbConfig {

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean(name="movieentitymanager")
    //@Qualifier("movieentitymanager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanMovies(@Qualifier("movies") DataSource dataSource, HibernateJpaVendorAdapter jpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean moviefactorybean=new LocalContainerEntityManagerFactoryBean();

        moviefactorybean.setDataSource(hikariwrapper(dataSource));
        moviefactorybean.setJpaVendorAdapter(jpaVendorAdapter);
        moviefactorybean.setPackagesToScan("org.superbiz.moviefun.movies");
        moviefactorybean.setPersistenceUnitName("persitentmoviefun");

        return moviefactorybean;
    }

    public HikariDataSource hikariwrapper(DataSource ds){

        HikariConfig config = new HikariConfig();
        config.setDataSource(ds);
        HikariDataSource hikariDataSource=new HikariDataSource(config);
        return hikariDataSource;

    }

    @Bean(name="albumentitymanager")
    //@Qualifier("albumentitymanager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanAlbum(@Qualifier("albums") DataSource dataSource, HibernateJpaVendorAdapter jpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean albumfactorybean=new LocalContainerEntityManagerFactoryBean();
        albumfactorybean.setDataSource(hikariwrapper(dataSource));
        albumfactorybean.setJpaVendorAdapter(jpaVendorAdapter);
        albumfactorybean.setPackagesToScan("org.superbiz.moviefun.albums");
        albumfactorybean.setPersistenceUnitName("persitentalbumfun");

        return albumfactorybean;
    }

    @Bean(name="albums")
    //@Qualifier("albums")
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="movies")
    //@Qualifier("movies")
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="albumplatform")
   // @Qualifier("albumplatform")
    public PlatformTransactionManager albumtransactionManager(@Qualifier("albumentitymanager") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return  txManager;

    }

    @Bean("movieplatform")
   // @Qualifier("movieplatform")
    public PlatformTransactionManager movietransactionManager(@Qualifier("movieentitymanager") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return  txManager;

    }


}