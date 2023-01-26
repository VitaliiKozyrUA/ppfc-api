package org.ppfc.api.service

import org.ppfc.api.common.StringResource
import org.sqlite.SQLiteException

suspend fun <T> sqlServiceExceptionHandler(block: suspend () -> T): ServiceResult<T> {
    try {
        return ServiceResult.Success(
            data = block()
        )
    } catch (e: SQLiteException) {
        val errorMessage = when (e.resultCode.name) {
            "SQLITE_CONSTRAINT_FOREIGNKEY" -> {
                StringResource.foreignKeyError
            }

            else -> {
                "${StringResource.error}: ${e.message}"
            }
        }

        return ServiceResult.Failure(message = errorMessage)
    } catch (e: MalformedModelException) {
        return ServiceResult.Failure(
            message = "${StringResource.error}: ${e.message}"
        )
    } catch (e: Exception) {
        return ServiceResult.Failure(
            message = "${StringResource.error}: ${e.message}"
        )
    }
}