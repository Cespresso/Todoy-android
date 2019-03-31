package cespresso.gmail.com.todoy.ui

import java.lang.Exception

sealed class TaskState {
    object None:TaskState()
    object  Progress:TaskState()
    data class Complete <T> (val payload: T):TaskState()
    data class Figure (val e:Exception):TaskState()
}