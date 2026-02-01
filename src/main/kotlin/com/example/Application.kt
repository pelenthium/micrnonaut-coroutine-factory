package com.example

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.bind.binders.HttpCoroutineContextFactory
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.Micronaut.run
import jakarta.inject.Singleton
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.optionals.getOrNull

fun main(args: Array<String>) {
	run(*args)
}

@Controller
class TestController {
	@Get("/test")
	suspend fun test(): String = currentCoroutineContext()[RequestContext.Key]?.requestId ?: "test"

	@Post("/test1", consumes = [MediaType.MULTIPART_FORM_DATA], produces = [MediaType.APPLICATION_JSON])
	suspend fun test1(): String = currentCoroutineContext()[RequestContext.Key]?.requestId ?: "test"

	@Post("/test2", consumes = [MediaType.MULTIPART_FORM_DATA], produces = [MediaType.APPLICATION_JSON])
	suspend fun test2(file: CompletedFileUpload): String = currentCoroutineContext()[RequestContext.Key]?.requestId ?: "test"
}


class RequestContext(
	val requestId: String
) : AbstractCoroutineContextElement(RequestContext) {
	companion object Key : CoroutineContext.Key<RequestContext>
}


@Singleton
class RequestContextFactory : HttpCoroutineContextFactory<RequestContext> {

	override fun create(): RequestContext {
		val request = ServerRequestContext.currentRequest<Any>()
			.getOrNull()

		val header: String? = request?.headers?.get("X-Request-Id")
		val requestId = header ?: "unknown"
		return RequestContext(
			requestId = requestId
		)
	}
}