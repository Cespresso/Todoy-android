package cespresso.gmail.com.todoy.data.source.remote

import cespresso.gmail.com.todoy.data.entity.Todo
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ITodoyApiService {

    // サーバーとの疎通確認用
    @GET("/server")
    fun getServerStatus(): Deferred<Response<ResponseBody>>

    // Todoを全件取得
    @GET("/todo")
    fun getAllTodo(@Header("Authorization") authToken: String): Deferred<Response<MutableList<Todo>>>

    // 新規Todo追加
    @POST("/todo")
    fun postTodo(@Header("Authorization") authToken: String, @Body todo: Todo): Deferred<Response<ResponseBody>>

    // Todoの削除
    @DELETE("todo/{id}")
    fun deleteTodo(@Header("Authorization") authToken: String,@Path("id") id:Int) :Deferred<Response<ResponseBody>>
}