package com.example
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class DemoTest {

    @Inject
    lateinit var application: EmbeddedApplication<*>
    
    @Inject
    @Client("/")
    lateinit var client: HttpClient
    
    @Test
    fun testItWorksWithDefaultValue() = runBlocking {
        val r = client.toBlocking().retrieve("/test")
        Assertions.assertEquals("unknown", r)
    }

    @Test
    fun testItWorksWithHeaderAndGet() = runBlocking {
        val request = HttpRequest.GET<String>("/test")
            .header("X-Request-Id", "MyRandomIdFromUI")
        val r = client.toBlocking()
            .retrieve(request)
        
        Assertions.assertEquals("MyRandomIdFromUI", r)
    }

    @Test
    fun testItWorksWithHeaderAndPost() = runBlocking {
        val request = HttpRequest.POST("/test1", null)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("X-Request-Id", "MyRandomIdFromUI2")
        val r = client.toBlocking()
            .retrieve(request)

        Assertions.assertEquals("MyRandomIdFromUI2", r)
    }

    @Test
    fun testItWorksWithHeaderAndFile() = runBlocking {
        val body = MultipartBody.builder()
            .addPart(
                "file",
                "test.txt",
                byteArrayOf()
            )
            .build()
        val request = HttpRequest.POST("/test2", body)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("X-Request-Id", "MyRandomIdFromUI3")
        val r = client.toBlocking()
            .retrieve(request)

        Assertions.assertEquals("MyRandomIdFromUI3", r)
    }
}
