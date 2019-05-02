package cespresso.gmail.com.todoy.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class YesOrNoDialog:DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = YesOrNoDialogArgs.fromBundle(arguments)
        return AlertDialog.Builder(requireActivity())
            .setTitle(args.title)
            .setMessage(args.message)
            .setPositiveButton("Yes"){ _,which->
                sendResult(which)
            }
            .setNegativeButton("No"){ _ ,which->
                    sendResult(which)
            }.create()
    }
    private fun sendResult(which:Int) {
        targetFragment?.onActivityResult(targetRequestCode, which, null)
    }
}