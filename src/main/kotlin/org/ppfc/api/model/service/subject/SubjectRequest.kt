package org.ppfc.api.model.service.subject

import kotlinx.serialization.Serializable

@Serializable
data class SubjectRequest(
    val name: String
)