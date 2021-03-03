package io.taesu.reactive.springdatar2dbc.app.config

import io.taesu.reactive.springdatar2dbc.user.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux

/**
 * Created by itaesu on 2021/03/02.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class AppInitializer(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val userRoleRepository: UserRoleRepository,
        private val client: DatabaseClient
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        demoForUsersInsert()
        saveUserRolesAndRetrieveUserRoles()

        val user = userRepository.save(User(userId = "another", email = "another@crscube.co.kr"))
        val roles: Flux<Role> = roleRepository.saveAll(listOf(
                Role(id = "ADMIN", name = "Admin"),
                Role(id = "SPM", name = "Sponsor Admin"),
                Role(id = "SPONSOR", name = "Sponsor")
        ))
        user.flatMap { roles.map { role -> UserRole(userKey = it.key!!, roleKey = role.key!!) }.collectList() }
                .flatMap { userRoleRepository.saveAll(it).collectList() }
                .flatMap { userRepository.findByUserId("another") }
                .flatMap {
                    client.sql("""
                                select
                                  mr.role_id
                                from usr_user uu
                                  left join user_role ur on ur.user_key = uu.user_key
                                  left join mst_role mr on mr.role_key = ur.role_key
                                where uu.user_key = :userKey
                            """.trimIndent())
                            .bind("userKey", it.key!!)
                            .fetch()
                            .all()
                            .map { roleId -> roleId["role_id"] as String }
                            .collectList()
                            .map { roles -> UserRoles(userId = it.userId, roles) }
                }
                .subscribe {
                    println("${it.userId} has roles ${it.roleIds.joinToString(",")}")
                }
    }

    private fun saveUserRolesAndRetrieveUserRoles() {
        Mono.zip(userRepository.save(User(userId = "leetaesu", email = "taesu@crscube.co.kr")),
                roleRepository.saveAll(listOf(
                        Role(id = "ADMIN", name = "Admin"),
                        Role(id = "SPM", name = "Sponsor Admin"),
                        Role(id = "SPONSOR", name = "Sponsor")
                )).collectList()
        ).flatMap {
            it.t2.toFlux()
                    .map { role -> UserRole(userKey = it.t1.key!!, roleKey = role.key!!) }
                    .flatMap { tit -> userRoleRepository.save(tit) }
                    .collectList()
        }.flatMapMany {
            client.sql("""
                                select 
                                  uu.user_id
                                , mr.role_id 
                                from usr_user uu 
                                  left join user_role ur on ur.user_key = uu.user_key
                                  left join mst_role mr on mr.role_key = ur.role_key
                                where uu.user_key = :userKey
                            """.trimIndent())
                    .bind("userKey", it[0].userKey)
                    .fetch()
                    .all()
        }.collectMultimap({ it["user_id"] as String }, { it["role_id"] as String })
                .flatMapMany { it.entries.toFlux() }
                .map { UserRoles(it.key, it.value.toList()) }
                .single()
                .subscribe {
                    println("${it.userId} has roles ${it.roleIds.joinToString(",")}")
                }
    }

    private fun demoForUsersInsert() {
        runBlocking {
            val take: Flux<User> = withContext(Dispatchers.Default) {
                with(userRepository) {
                    saveAll(arrayListOf(
                            User(userId = "taesu", email = "taesu@crscube.co.kr"),
                            User(userId = "kim", email = "kim@crscube.co.kr")))
                            .thenMany(findAll())
                            .take(1)
                }
            }

            take.collect {
                updateFirstUser(it.key ?: throw IllegalArgumentException("user ke is null"))
                updateFirstUser(52)
            }
        }
    }

    // private suspend fun updateFirstUser(userKey: Long) {
    //     client.sql("select user_id, user_email from usr_user where user_key = :userKey")
    //             .bind("userKey", userKey)
    //             .fetch()
    //             .one()
    //             .flatMap {
    //                 client.sql("update usr_user set user_email = :userEmail where user_key = :userKey")
    //                         .bind("userEmail", "taesu@change.co.kr")
    //                         .bind("userKey", userKey)
    //                         .fetch()
    //                         .rowsUpdated()
    //             }
    //             .switchIfEmpty(
    //                     client.sql("""
    //                         INSERT INTO usr_user (user_id, user_email, created_by, created_at, modified_by, modified_at)
    //                         VALUES (:userId, :userEmail, :createdBy, :createdAt, :modifiedBy, :modifiedAt)
    //                     """.trimIndent())
    //                             .bind("userId", "taesu")
    //                             .bind("userEmail", "taesu@crscube.co.kr")
    //                             .bind("createdBy", -1L)
    //                             .bind("createdAt", LocalDateTime.now())
    //                             .bind("modifiedBy", -1L)
    //                             .bind("modifiedAt", LocalDateTime.now())
    //                             .fetch()
    //                             .rowsUpdated()
    //             )
    //             .subscribe()
    // }

    private suspend fun updateFirstUser(userKey: Long) {
        with(userRepository) {
            findById(userKey)
                    .flatMap {
                        client.sql("update usr_user set user_email = :userEmail where user_key = :userKey")
                                .bind("userEmail", "taesu@change.co.kr")
                                .bind("userKey", userKey)
                                .fetch()
                                .rowsUpdated()
                    }
                    .switchIfEmpty {
                        save(User(userId = "taesu", email = "taesu@change.co.kr")).map { 1 }
                    }
        }.subscribe()
    }
}

data class UserRoles(val userId: String, val roleIds: List<String>)