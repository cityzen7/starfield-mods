package utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

val mapper = jacksonObjectMapper()

fun readConfig() : Map<String, Any> {
    return readConfigOrNull()!!
}

fun readConfigOrNull() : Map<String, Any>? {
    val source = File("./config.json")
    return source.takeIf { it.exists() }?.let { mapper.readValue(it) }
}
