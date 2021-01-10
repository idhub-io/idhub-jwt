package io.idhub.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.idhub.jwt.converters.ApplicationConfigReadConverter;
import io.idhub.jwt.converters.ApplicationConfigWriteConverter;
import io.idhub.jwt.converters.KeyReadConverter;
import io.idhub.jwt.converters.KeyWriteConverter;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfiguration extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.host}")
    private String host;
    @Value("${spring.r2dbc.port}")
    private Integer port;
    @Value("${spring.r2dbc.database}")
    private String database;
    @Value("${spring.r2dbc.username}")
    private String username;
    @Value("${spring.r2dbc.password}")
    private String password;
    @Value("${spring.r2dbc.pool.initial-size}")
    private int initialSize;
    @Value("${spring.r2dbc.pool.max-size}")
    private int maxSize;
    @Value("${spring.r2dbc.pool.max-idle-time}")
    private int maxIdleTime;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                                                 .host(host)
                                                 .port(port)
                                                 .database(database)
                                                 .username(username)
                                                 .password(password)
                                                 .build()
        );
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                                                                               .maxIdleTime(Duration.ofMinutes(maxIdleTime))
                                                                               .initialSize(initialSize)
                                                                               .maxSize(maxSize)
                                                                               .maxCreateConnectionTime(Duration.ofSeconds(1))
                                                                               .build();
        return new ConnectionPool(configuration);
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {

        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new ApplicationConfigReadConverter());
        converterList.add(new ApplicationConfigWriteConverter());
        converterList.add(new KeyReadConverter());
        converterList.add(new KeyWriteConverter());

        return new R2dbcCustomConversions(getStoreConversions(), converterList);
    }
}