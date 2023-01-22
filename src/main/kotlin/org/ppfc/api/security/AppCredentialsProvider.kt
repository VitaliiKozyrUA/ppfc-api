package org.ppfc.api.security

import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ppfc.api.data.config.ConfigProvider

class AppCredentialsProvider : CredentialsProvider, KoinComponent {
    private val config: ConfigProvider by inject()

    override suspend fun getCredentials(): Credentials = Credentials(
        accessKeyId = config.getConfig().awsAccessKeyId,
        secretAccessKey = config.getConfig().awsSecretAccessKey
    )
}