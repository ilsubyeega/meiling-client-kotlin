package ng.meili.client

import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ng.meili.client.exceptions.MeilingAPIError
import ng.meili.client.json.MeilingJsonError

suspend fun ResponseException.asMeilingAPIException() : Exception {
    try {
        val bodyText = this.response.readText()
        Json.decodeFromString<MeilingJsonError>(bodyText).let {
            return MeilingAPIError("${it.error}: ${it.description}")
        }
    } catch (e: Exception) {
        return this
    }
}