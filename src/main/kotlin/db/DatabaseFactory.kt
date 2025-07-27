
package com.example.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
      
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)

       
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }
}

// --- Database Table Schema for Users ---
object Users : IntIdTable() {
    val username = varchar("username", 80).uniqueIndex()
    val email = varchar("email", 120).uniqueIndex()
    val passwordHash = varchar("password_hash", 256)
}

// --- DAO (Data Access Object) Class for a User ---
// This class maps rows in the Users table to objects.
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var username by Users.username
    var email by Users.email
    var passwordHash by Users.passwordHash
}