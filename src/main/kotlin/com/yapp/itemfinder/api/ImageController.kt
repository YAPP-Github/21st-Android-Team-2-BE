package com.yapp.itemfinder.api

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.exception.ErrorResponse
import com.yapp.itemfinder.common.Const.ImageFormat
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.util.AmazonS3Uploader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

const val INVALID_FILE_FORMAT = "잘못된 파일 형식입니다."

@RestController
@RequestMapping("/images")
class ImageController(
    private val amazonS3Uploader: AmazonS3Uploader
) {
    val fileMaxCnt = 10

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "이미지 파일 등록")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "400",
                description = "개수 초과 | 잘못된 파일 형식",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun addImage(
        @LoginMember member: MemberEntity,
        @Parameter(description = "multipart/form-data 형식의 이미지 리스트(개수 제한 10개).<br> key 값은 image.")
        @RequestPart
        image: List<MultipartFile>
    ): List<ImageResponse> {
        if (image.size > fileMaxCnt) {
            throw BadRequestException(message = "파일은 최대 ${fileMaxCnt}개까지 업로드할 수 있습니다.")
        }
        image.map { validateImageFile(it) }
        return image.map { ImageResponse(url = amazonS3Uploader.uploadFile(it)) }
    }

    private fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw BadRequestException(message = INVALID_FILE_FORMAT)
        }
        val fileName = file.originalFilename
            ?: throw BadRequestException(message = INVALID_FILE_FORMAT)
        // 이미지 포맷 검사
        val extension = fileName.split(".").last()
        try {
            enumValueOf<ImageFormat>(extension.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(message = INVALID_FILE_FORMAT)
        }
    }
}
