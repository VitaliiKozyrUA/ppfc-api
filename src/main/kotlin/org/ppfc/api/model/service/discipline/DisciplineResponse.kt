package org.ppfc.api.model.service.discipline

import kotlinx.serialization.Serializable

@Serializable
data class DisciplineResponse(
    val id: Long,
    val name: String
)