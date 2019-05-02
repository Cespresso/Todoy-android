package cespresso.gmail.com.todoy.data.entity

data class Todo(
    val id: Int?,
    var title: String?,
    var body: String?,
    var completed: Boolean?
) {
    constructor(title: String?, body: String?) : this(null, title, body, false)

    fun getShortBody(): String {
        return when {
            body == null -> {
                ""
            }
            body!!.length < 10 -> {
                body!!
            }
            else -> {
                body!!.substring(0, 10) + "....."
            }
        }
    }
}