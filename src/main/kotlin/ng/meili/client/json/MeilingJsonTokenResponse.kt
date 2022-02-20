package ng.meili.client.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable()
data class MeilingJsonTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("scope")
    val scopes: String,
    @SerialName("id_token")
    val idToken: String? = null
)
