package com.hafiztaruligani.cryptoday

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

internal sealed class HttpExceptionBuilder {
    class NotFoundException(
        val exception: HttpException =
            HttpException(
                Response.error<ResponseBody>(
                    404,
                    "exception".toResponseBody("plain/text".toMediaType())

                )
            )
    ) : HttpExceptionBuilder()
}
