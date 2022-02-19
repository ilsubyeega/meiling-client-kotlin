package ng.meili.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
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

    private var clientCred: MeilingClientCredentials

    internal constructor(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Date,
        clientCred: MeilingClientCredentials
    ) {
        this.accessToken = accessToken
        this.expiresIn = expiresIn
        this.refreshToken = refreshToken
        this.clientCred = clientCred
    }

    internal constructor(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Int,
        clientCred: MeilingClientCredentials
    ) {
        this.accessToken = accessToken
        this.expiresIn = Date(Date().time + expiresIn * 1000)
        this.refreshToken = refreshToken
        this.clientCred = clientCred
    }

    fun isExpired(): Boolean {
        return Date().before(expiresIn)
    }

    suspend fun refresh() {
        require(refreshToken != null) { "Refresh token is null" }

        val client = HttpClient {
            expectSuccess = true
        }

        try {
            val res: String = client.post("{$clientCred.apiUrl}/oauth/token") {
                contentType(ContentType.Application.Json)
                userAgent("MeiliNG/Kotlin")

                body =
                    formData {
                        append("grant_type", "refresh_token")
                        append("refresh_token", refreshToken!!)
                        append("client_id", clientCred.clientId)
                        append("client_secret", clientCred.clientSecret)
                    }
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


}