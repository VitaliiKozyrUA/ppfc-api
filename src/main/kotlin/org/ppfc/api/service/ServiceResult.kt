package org.ppfc.api.service

sealed class ServiceResult<T> {
    class Success<T>(val data: T) : ServiceResult<T>()
    class Failure<T>(val message: String) : ServiceResult<T>()
}