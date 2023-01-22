package org.ppfc.api.data.config

data class Config(
    val awsAccessKeyId: String,
    val awsSecretAccessKey: String,
    val awsRegion: String,
    val awsUserPoolId: String,
    val awsClientId: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String
)
