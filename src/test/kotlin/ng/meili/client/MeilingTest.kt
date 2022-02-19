package ng.meili.client

import org.junit.jupiter.api.Test

internal class MeilingTest {

    @Test
    fun codeCredentialsUrl() {
        val clientId = "ab5e527f-2d11-4b93-b9ba-33081bc8eec5"
        val client = MeilingClient()

        val challenge = MeilingPKCEChallenge("random")

        val builder = MeilingCodeCredentialBuilder(clientId, listOf("openid"), challenge)
        val url = client.codeCredentialsUrl(builder)

        assert(url == "https://meiling.stella-api.dev/v1/oauth2/auth?client_id=ab5e527f-2d11-4b93-b9ba-33081bc8eec5&response_type=code&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=openid&code_challenge=d9146fab2f05355b11d20bb943b1d6f83dda2d11079acf839beb1bc693fe2d0a&code_challenge_method=S256")
    }

    @Test
    fun codeCredentialsUrlWithoutChallenge() {
        val clientId = "ab5e527f-2d11-4b93-b9ba-33081bc8eec5"
        val client = MeilingClient()

        val builder = MeilingCodeCredentialBuilder(clientId, listOf("openid"))
        val url = client.codeCredentialsUrl(builder)

        assert(url == "https://meiling.stella-api.dev/v1/oauth2/auth?client_id=ab5e527f-2d11-4b93-b9ba-33081bc8eec5&response_type=code&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=openid")

    }
    @Test
    fun exchangeCodeCredentials() {
        TODO("not implemented")
    }

    @Test
    fun refreshCredentials() {
        TODO("not implemented")
    }

}