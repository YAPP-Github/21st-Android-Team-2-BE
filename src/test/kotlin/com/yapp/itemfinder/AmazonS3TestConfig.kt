package com.yapp.itemfinder

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.findify.s3mock.S3Mock
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.util.UriComponentsBuilder
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
class AmazonS3TestConfig {
    private val port = 8001
    private val region = Regions.AP_NORTHEAST_2.name
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket = ""
    private lateinit var s3Mock: S3Mock

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        val endpoint = AwsClientBuilder.EndpointConfiguration(getUri(), region)
        val client = AmazonS3ClientBuilder
            .standard()
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
