package cespresso.gmail.com.todoy

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.await(): T {
    return suspendCoroutine { continuation ->
        addOnSuccessListener {
            continuation.resume(it)
        }
        addOnFailureListener {
            continuation.resumeWithException(it)
        }
    }
}