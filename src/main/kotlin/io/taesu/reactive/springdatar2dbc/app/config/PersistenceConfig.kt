package io.taesu.reactive.springdatar2dbc.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import reactor.core.publisher.Mono

/**
 * Created by itaesu on 2021/03/02.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Configuration
@EnableR2dbcAuditing(auditorAwareRef = "auditorAware")
class PersistenceConfig {
    @Bean
    fun auditorAware(): ReactiveAuditorAware<Long> = ReactiveAuditorAware { Mono.just(-1L) }
}