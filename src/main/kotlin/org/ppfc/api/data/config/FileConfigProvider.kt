package org.ppfc.api.data.config

import java.util.*

class FileConfigProvider(resourceName: String) : ConfigProvider {
    private var config: Config

    init {
        val configInputStream = javaClass.getResourceAsStream("/$resourceName")
        if (configInputStream != null) {
            val properties = Properties()
            properties.load(configInputStream)

            config = Config(
                awsAccessKeyId = properties.getProperty("awsAccessKeyId"),
                awsSecretAccessKey = properties.getProperty("awsSecretAccessKey"),
                awsRegion = properties.getProperty("awsRegion"),
                awsUserPoolId = properties.getProperty("awsUserPoolId"),
                awsClientId = properties.getProperty("awsClientId"),
                jwtIssuer = properties.getProperty("jwtIssuer"),
                jwtAudience = properties.getProperty("jwtAudience"),
                jwtRealm = properties.getProperty("jwtRealm")
            )
        } else {
            throw ConfigFileNotFoundException()
        }
    }

    override fun getConfig(): Config = config
}