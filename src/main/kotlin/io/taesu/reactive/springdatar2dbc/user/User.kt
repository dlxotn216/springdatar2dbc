package io.taesu.reactive.springdatar2dbc.user

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * Created by itaesu on 2021/03/02.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Table("usr_user")
class User(
        @Id
        @Column("user_key")
        var key: Long? = null,

        @Column("user_id")
        val userId: String,

        @Column("user_email")
        var email: String,

        @Column("created_by")
        @CreatedBy
        var createdBy: Long = -1L,

        @Column("created_at")
        @CreatedDate
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @Column("modified_by")
        @LastModifiedBy
        var modifiedBy: Long? = -1L,

        @Column("modified_at")
        @LastModifiedDate
        var modifiedDate: LocalDateTime = LocalDateTime.now(),
)

interface UserRepository : ReactiveCrudRepository<User, Long> {
    fun findByUserId(userId: String): Mono<User>
}

@Table("mst_role")
class Role(
        @Id
        @Column("role_key")
        var key: Long? = null,

        @Column("role_id")
        val id: String,

        @Column("role_name")
        var name: String,

        @Column("created_by")
        @CreatedBy
        var createdBy: Long = -1L,

        @Column("created_at")
        @CreatedDate
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @Column("modified_by")
        @LastModifiedBy
        var modifiedBy: Long? = -1L,

        @Column("modified_at")
        @LastModifiedDate
        var modifiedDate: LocalDateTime = LocalDateTime.now(),
)

interface RoleRepository : ReactiveCrudRepository<Role, Long>

@Table("user_role")
class UserRole(
        @Id
        @Column("user_role_key")
        var key: Long? = null,

        @Column("user_key")
        val userKey: Long,

        @Column("role_key")
        val roleKey: Long,

        @Column("created_by")
        @CreatedBy
        var createdBy: Long = -1L,

        @Column("created_at")
        @CreatedDate
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @Column("modified_by")
        @LastModifiedBy
        var modifiedBy: Long? = -1L,

        @Column("modified_at")
        @LastModifiedDate
        var modifiedDate: LocalDateTime = LocalDateTime.now(),
)

interface UserRoleRepository : ReactiveCrudRepository<UserRole, Long>