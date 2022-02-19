package ng.meili.client.json

import kotlinx.serialization.*

@Serializable
data class MeilingJsonError(
    val error: String,
    @SerialName("error_description")
    val description: String?,
    @SerialName("url")
    val url: String?,
)
