package org.ppfc.api.service

import org.sqlite.SQLiteException

suspend fun <T> sqlServiceExceptionHandler(block: suspend () -> T): ServiceResult<T> {
    try {
        return ServiceResult.Success(
            data = block()
        )
    } catch (e: SQLiteException) {
        val errorMessage = when (val sqlLiteErrorCode = e.resultCode.name) {
            "SQLITE_CONSTRAINT_FOREIGNKEY" -> {
                "Помилка зовнішнього ключа. Ви проводите операцію, яка впливає на дані іншої таблиці."
            }

            else -> {
                "Помилка $sqlLiteErrorCode."
            }
        }

        return ServiceResult.Failure(message = errorMessage)
    } catch (e: Exception) {
        return ServiceResult.Failure(
            message = "Помилка: ${e.message}."
        )
    }
}