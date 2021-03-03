package io.taesu.reactive.springdatar2dbc.app.base

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

/**
 * Created by itaesu on 2021/03/02.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class Audit(
        @Column("created_by")
        @CreatedBy
        var createdBy: Long? = null,
        @Column("created_at")
        @CreatedDate
        var createdAt: LocalDateTime? = null,
        @Column("modified_by")
        @LastModifiedBy
        var modifiedBy: Long? = null,
        @Column("modified_at")
        @LastModifiedDate
        var modifiedDate: LocalDateTime? = null,
)