package ng.meili.client

import java.security.MessageDigest

data class MeilingPKCEChallenge(val text: String, var hashedText: String, val method: String = "S256") {
    constructor(text: String) : this(text, "") {
        // Creating SHA-256 hash of the text
        val bytes = this.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        this.hashedText = digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}