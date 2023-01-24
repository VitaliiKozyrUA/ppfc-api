package org.ppfc.api.model.service.discipline

import kotlinx.serialization.Serializable

@Serializable
data class DisciplineRequest(
    val name: String
)