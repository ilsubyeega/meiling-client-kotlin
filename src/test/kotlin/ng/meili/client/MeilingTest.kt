package ng.meili.client

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class MeilingTest {

    @Test
    fun codeCredentialsUrl() {
        val client = MeilingClient()
        val challenge = MeilingPKCEChallenge("random")

        val builder =
            MeilingCodeCredentialBuilder(importClientCredentialsFromEnv().clientId, listOf("openid"), challenge)
        val url = client.codeCredentialsUrl(builder)

        assert(url == "https://meiling.stella-api.dev/v1/oauth2/auth?client_id=ab5e527f-2d11-4b93-b9ba-33081bc8eec5&response_type=code&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=openid&code_challenge=d9146fab2f05355b11d20bb943b1d6f83dda2d11079acf839beb1bc693fe2d0a&code_challenge_method=S256")
    }

    @Test
    fun codeCredentialsUrlWithoutChallenge() {
        val client = MeilingClient()

        val builder = MeilingCodeCredentialBuilder(importClientCredentialsFromEnv().clientId, listOf("openid"))
        val url = client.codeCredentialsUrl(builder)

        assert(url == "https://meiling.stella-api.dev/v1/oauth2/auth?client_id=ab5e527f-2d11-4b93-b9ba-33081bc8eec5&response_type=code&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=openid")
    }

    @Test
    fun exchangeCodeCredentials() {
        val code = ""

        val client = MeilingClient()

        val result = runBlocking {
            client.exchangeCodeCredentials(importClientCredentialsFromEnv(), code)
        }
        println(result)
    }

    @Test
    fun refreshCredentials() {
        val clientCredentials = MeilingCredentials(
            "",
            "",
            7200,
            "https://meiling.stella-api.dev",
            importClientCredentialsFromEnv()
        )
        runBlocking {
            clientCredentials.refresh()
        }
    }
}

private fun importClientCredentialsFromEnv(): MeilingClientCredentials {
    val env: String = System.getenv("MEILING_CREDENTIALS")
    val envSplit = env.split("|")
    return MeilingClientCredentials(envSplit[0], envSplit[1])
}