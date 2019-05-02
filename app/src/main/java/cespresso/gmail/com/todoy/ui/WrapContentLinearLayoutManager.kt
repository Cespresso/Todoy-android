package cespresso.gmail.com.todoy.ui

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


internal class WrapContentLinearLayoutManager(val context: Context) : LinearLayoutManager(context) {
    // TODO IndexOutOfBoundsExceptionを握りつぶしている
    // https://stackoverflow.com/questions/41300626/indexoutofboundexception-with-recyclerview
    // 以上を基に追加
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }

    }
}