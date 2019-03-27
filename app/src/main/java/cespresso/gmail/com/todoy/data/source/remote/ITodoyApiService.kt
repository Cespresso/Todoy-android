package cespresso.gmail.com.todoy.data.source.remote

import cespresso.gmail.com.todoy.data.entity.Todo
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ITodoyApiService {

    @GET("/server")
    fun getServerStatus(): Deferred<Response<ResponseBody>>

    @GET("/Todo")
    fun getAllTodo(): Deferred<Response<List<Todo>>>
}