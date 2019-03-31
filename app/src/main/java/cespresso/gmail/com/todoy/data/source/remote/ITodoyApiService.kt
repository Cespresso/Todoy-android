package cespresso.gmail.com.todoy.data.source.remote

import cespresso.gmail.com.todoy.data.entity.Todo
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ITodoyApiService {

    @GET("/server")
    fun getServerStatus(): Deferred<Response<ResponseBody>>

    @GET("/todo")
    fun getAllTodo(@Header("Authorization") authToken:String): Deferred<Response<MutableList<Todo>>>

    @POST("/todo")
    fun postTodo(@Header("Authorization")authToken:String,@Body todo:Todo): Deferred<Response<ResponseBody>>
}