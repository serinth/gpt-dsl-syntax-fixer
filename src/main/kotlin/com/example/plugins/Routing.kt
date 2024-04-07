package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticate("basic-auth") {
            post("/dsl/check") {
                val bodyBytes = call.receive<ByteArray>()
                val bodyString = bodyBytes.decodeToString()
                call.application.log.info("Received: $bodyString")

                val command = arrayOf("sh", "-c", "echo $bodyString | grep -c 'syntax error'")
                val process = Runtime.getRuntime()
                    .exec(command)

                // no syntax errors
                if (process.exitValue() == 0) {
                    return@post call.respond(HttpStatusCode.OK)
                }

                // dart CLI tool needs to return non 0 with the syntax error
                val syntaxErrors = process
                    .inputStream
                    .bufferedReader()
                    .readText()
                    .trim()

                val reprompt = """
               Syntax errors were detected. Fix the syntax above with the syntax error hints below:
               $syntaxErrors
            """.trimIndent()
                call.respondText(reprompt)
            }
        }
    }
}

