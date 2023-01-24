package org.ppfc.api

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import org.ppfc.api.data.config.ConfigFileNotFoundException
import org.ppfc.api.data.config.ConfigProvider
import org.ppfc.api.data.config.FileConfigProvider
import org.ppfc.api.database.Database
import org.ppfc.api.security.auth.AuthProvider
import org.ppfc.api.security.auth.CognitoAuthProvider
import org.ppfc.api.service.abstraction.*
import org.ppfc.api.service.implementation.*
import org.sqlite.SQLiteConfig
import kotlin.system.exitProcess

val appModule = module {
    single {
        val path = "jdbc:sqlite:database.db"
        val config = SQLiteConfig()
        config.enforceForeignKeys(true)
        val connectionProperties = config.toProperties()
        val driver = JdbcSqliteDriver(url = path, properties = connectionProperties)
        Database.Schema.create(driver = driver)
        Database(driver = driver)
    }

    single<ConfigProvider> {
        try {
            FileConfigProvider(resourceName = "config.properties")
        } catch (e: ConfigFileNotFoundException) {
            println("Config file not found!")
            exitProcess(1)
        }
    }

    single<AuthProvider> {
        CognitoAuthProvider(config = get())
    }

    single<GroupService> {
        DbGroupService(database = get())
    }

    single<SubjectService> {
        DbSubjectService(database = get())
    }

    single<ClassroomService> {
        DbClassroomService(database = get())
    }

    single<CourseService> {
        DbCourseService(database = get())
    }

    single<DisciplineService> {
        DbDisciplineService(database = get())
    }

    single<TeacherService> {
        DbTeacherService(database = get())
    }

    single<ScheduleService> {
        DbScheduleService(database = get())
    }

    single<ChangeService> {
        DbChangeService(database = get())
    }

    single<UserService> {
        DbUserService(database = get())
    }
}