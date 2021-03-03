package io.taesu.reactive.springdatar2dbc.app.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator


/**
 * Created by itaesu on 2021/03/02.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Configuration(proxyBeanMethods = false)
class DatabaseInitializationConfiguration {
    /**
     * https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-initialize-a-database-using-r2dbc
     * R2dbc를 사용하는 경우 auto-configuration이 off 됨
     */
    @Autowired
    fun initializeDatabase(connectionFactory: ConnectionFactory) {
        val resourceLoader: ResourceLoader = DefaultResourceLoader()
        val scripts: Array<Resource> = arrayOf(resourceLoader.getResource("classpath:schema.sql"))
        ResourceDatabasePopulator(*scripts).populate(connectionFactory).block()
    }
}