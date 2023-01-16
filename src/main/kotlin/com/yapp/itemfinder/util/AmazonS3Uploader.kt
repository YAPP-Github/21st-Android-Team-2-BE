package com.yapp.itemfinder.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.exception.BaseException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.UUID

const val FILE_UPLOAD_FAIL = "파일 업로드 실패하였습니다."

@Component
class AmazonS3Uploader(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String,
) {
    fun uploadFile(multipartFile: MultipartFile): String {
        val originalFilename = multipartFile.originalFilename ?: throw BadRequestException()
        val extension = originalFilename.split(".").last()
        val fileName = "${UUID.randomUUID()}.$extension"

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = multipartFile.contentType
        objectMetadata.contentLength = multipartFile.size

        try {
            amazonS3Client.putObject(PutObjectRequest(bucketName, fileName, multipartFile.inputStream, objectMetadata))
        } catch (e: Exception) {
            throw BaseException(httpStatus = INTERNAL_SERVER_ERROR, message = FILE_UPLOAD_FAIL)
        }
        return amazonS3Client.getResourceUrl(bucketName, fileName)
    }
}
