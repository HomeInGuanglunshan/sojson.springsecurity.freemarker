package sojson.springsecurity.freemarker.core.mybatis;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class SqlSessionConfig {

	@Value("${mybatis.mapper-locations}")
	private String mapperLocations;

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean myGetSqlSessionFactory(DataSource dataSource) throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

		// mapperLocations
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		try {
			sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
		} catch (IOException e) {
			// log.info("sqlSessionFactoryBean的setMapperLocations有问题");
			e.printStackTrace();
		}

		// dataSource
		sqlSessionFactoryBean.setDataSource(dataSource);

		// SqlSessionFactory sessionFactory = sqlSessionFactoryBean.getObject();
		return sqlSessionFactoryBean;

	}
}
