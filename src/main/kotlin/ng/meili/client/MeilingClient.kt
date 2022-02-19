package ng.meili.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ng.meili.client.json.MeilingJsonTokenResponse

class MeilingClient(var endpointUrl: String = "https://meiling.stella-api.dev") {

    fun codeCredentialsUrl(
        codeCredBuilder: MeilingCodeCredentialBuilder, redirectUrl: String = "urn:ietf:wg:oauth:2.0:oob"
    ): String {
        val builder = HttpRequestBuilder()

        builder.url("${endpointUrl}/v1/oauth2/auth")
        builder.parameter("client_id", codeCredBuilder.clientId)
        builder.parameter("response_type", "code")
        builder.parameter("redirect_uri", redirectUrl)
        builder.parameter("scope", codeCredBuilder.scopes.joinToString(" "))

        if (codeCredBuilder.challenge != null) {
            builder.parameter("code_challenge", codeCredBuilder.challenge.hashedText)
            builder.parameter("code_challenge_method", codeCredBuilder.challenge.method)
        }

        return builder.build().url.toString()
    }

    suspend fun exchangeCodeCredentials(
        clientCred: MeilingClientCredentials, code: String, challenge: MeilingPKCEChallenge?
    ): MeilingCredentials {
        val client = HttpClient {
            expectSuccess = true
        }

        try {
            val res : String = client.post("{$clientCred.apiUrl}/oauth2/token") {
                contentType(ContentType.Application.Json)
                userAgent("MeiliNG/Kotlin")

                body = formData {
                    append("client_id", clientCred.clientId)
                    append("client_secret", clientCred.clientSecret)
                    append("grant_type", "authorization_code")
                    append("code", code)
                    if (challenge != null) {
                        TODO("Add challenge headers here")
                    }
                }
            }

            val json = Json.decodeFromString<MeilingJsonTokenResponse>(res)

            return MeilingCredentials(
                json.accessToken, json.refreshToken, json.expiresIn, clientCred
            )

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