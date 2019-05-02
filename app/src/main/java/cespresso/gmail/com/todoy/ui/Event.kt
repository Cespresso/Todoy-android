package cespresso.gmail.com.todoy.ui

/**
 * https://github.com/googlesamples/android-architecture/blob/todo-mvvm-live-kotlin/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/Event.kt
 * http://www.apache.org/licenses/LICENSE-2.0
 */
open class Event<out T>(private val content: T) {

    // イベントがすでに通知済みかを保持する
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
    /**
     * Returns the content, even if it's already been handled.
     */
//    fun peekContent(): T = content
}