package ng.meili.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ng.meili.client.exceptions.MeilingAPIError
import ng.meili.client.json.MeilingJsonError

suspend fun ResponseException.asMeilingAPIException(): Exception {
    try {
        val bodyText = this.response.readText()
        Json.decodeFromString<MeilingJsonError>(bodyText).let {
            return MeilingAPIError("${it.error}: ${it.description}")
        }
    } catch (e: Exception) {
        return this
    }
}

fun defaultHttpClient() = HttpClient {
    expectSuccess = true
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    defaultRequest {
        headers.append("User-Agent", "MeiliNG/Kotlin")
        contentType(ContentType.Application.Json)
    }
}