package ng.meili.client

import java.security.MessageDigest

data class MeilingPKCEChallenge(val text: String, val hashedText: String, val method: String = "S256") {
    constructor(text: String) : this(text, sha256(text))
}

private fun sha256(text: String): String {
    // Creating SHA-256 hash of the text
    val bytes = text.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}