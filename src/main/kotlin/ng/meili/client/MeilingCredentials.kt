package ng.meili.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ng.meili.client.json.MeilingJsonTokenResponse
import java.util.*

class MeilingCredentials {
    var accessToken: String
        private set
    var expiresIn: Date
        private set
    var refreshToken: String? = null
        private set

    var endpointUrl: String
        private set

    private var clientCred: MeilingClientCredentials

    internal constructor(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Date,
        endpointUrl: String,
        clientCred: MeilingClientCredentials
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.expiresIn = expiresIn
        this.endpointUrl = endpointUrl
        this.clientCred = clientCred
    }

    internal constructor(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Int,
        endpointUrl: String,
        clientCred: MeilingClientCredentials
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.expiresIn = Date(Date().time + expiresIn * 1000)
        this.endpointUrl = endpointUrl
        this.clientCred = clientCred
    }


    fun isExpired(): Boolean {
        return Date().before(expiresIn)
    }

    suspend fun refresh() {
        require(refreshToken != null) { "Refresh token is null" }

        val client = defaultHttpClient()

        try {
            val res: String = client.post("$endpointUrl/v1/oauth2/token") {
                body = mapOf(
                    "grant_type" to "refresh_token",
                    "refresh_token" to refreshToken!!,
                    "client_id" to clientCred.clientId,
                    "client_secret" to clientCred.clientSecret
                )
            }

            val json = Json.decodeFromString<MeilingJsonTokenResponse>(res)

            accessToken = json.accessToken
            expiresIn = Date(Date().time + json.expiresIn * 1000)
            refreshToken = json.refreshToken

        } catch (e: ClientRequestException) {
            throw e.asMeilingAPIException()
        } catch (e: ServerResponseException) {
            throw e.asMeilingAPIException()
        } catch (e: Exception) {
            throw e
        } finally {
            client.close()
        }
    }


    suspend fun getCurrentUser() {
        TODO("not implemented")
        /*
        val client = defaultHttpClient()
        val res: String = client.get("{$clientCred.apiUrl}/v1/meiling/users") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
        */
    }


    override fun toString(): String {
        return "MeilingCredentials(accessToken='$accessToken', expiresIn=$expiresIn, refreshToken=$refreshToken, endpointUrl='$endpointUrl', clientCred=$clientCred)"
    }
}