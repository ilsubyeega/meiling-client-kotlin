package ng.meili.client

import io.ktor.client.features.*
import io.ktor.client.request.*
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
        clientCred: MeilingClientCredentials, code: String, challenge: MeilingPKCEChallenge? = null
    ): MeilingCredentials {
        val client = defaultHttpClient()

        try {
            val res: String = client.post("$endpointUrl/v1/oauth2/token") {
                body = mapOf(
                    "client_id" to clientCred.clientId,
                    "client_secret" to clientCred.clientSecret,
                    "grant_type" to "authorization_code",
                    "code" to code
                    // TODO: Add challenge params
                )
            }

            val json = Json.decodeFromString<MeilingJsonTokenResponse>(res)

            // TODO: Add more params to MeilingCredentials
            return MeilingCredentials(
                json.accessToken, json.refreshToken, json.expiresIn, endpointUrl, clientCred
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