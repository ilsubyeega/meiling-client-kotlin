package ng.meili.client

data class MeilingCodeCredentialBuilder(
    val clientId: String,
    val scopes: List<String>,
    val challenge: MeilingPKCEChallenge?
) {
    constructor(clientId: String, scopes: List<String>) : this(clientId, scopes, null)
}