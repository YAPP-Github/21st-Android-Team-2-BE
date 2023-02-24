package com.yapp.itemfinder.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.findify.s3mock.S3Mock
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.web.util.UriComponentsBuilder
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Profile("local")
@Configuration
class EmbeddedS3Config(
    @Value("\${cloud.aws.region.static}")
    private val region: String,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.s3.mock.port}")
    private val port: Int
) {

    private lateinit var s3Mock: S3Mock

    @Bean
    @Primary
    fun amazonS3Client(): AmazonS3Client {
        val endpoint = AwsClientBuilder.EndpointConfiguration(getUri(), region)
        val client = AmazonS3ClientBuilder.standard()
            .withPathStyleAccessEnabled(true)
            .withEndpointConfiguration(endpoint)
            .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
            .build() as AmazonS3Client
        client.createBucket(bucket)
        return client
    }

    private fun getUri(): String {
        return UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port).build().toUriString()
    }

    @PostConstruct
    fun startS3Mock() {
        s3Mock = S3Mock.Builder()
            .withPort(port)
            .withInMemoryBackend()
            .build()
        s3Mock.start()
    }

    @PreDestroy
    fun destroyS3Mock() {
        s3Mock.shutdown()
    }
}
