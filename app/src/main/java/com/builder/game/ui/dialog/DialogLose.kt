package com.builder.game.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.builder.game.R
import com.builder.game.core.library.ViewBindingDialog
import com.builder.game.core.library.l
import com.builder.game.databinding.DialogLoseBinding
import com.builder.game.domain.Difficulty
import com.builder.game.ui.builder.FragmentBuilder
import com.builder.game.ui.other.MainActivity


class DialogLose : ViewBindingDialog<DialogLoseBinding>(DialogLoseBinding::inflate) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                (requireActivity() as MainActivity).navigateBack("main")
                dialog?.cancel()
                true
            } else {
                false
            }
        }
        binding.menu.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack("main")
            dialog?.cancel()
        }
        l(arguments.toString())
        binding.restart.setOnClickListener {
            val difficulty = arguments?.getSerializable("DIFFICULTY") as Difficulty
            (requireActivity() as MainActivity).navigate(FragmentBuilder().apply {
                arguments = Bundle().apply {
                    putSerializable("DIFFICULTY", difficulty)
                    putBoolean("IS_CONTINUE", false)
                }
            })
            dialog?.cancel()
        }

        binding.floorsAmount.text = arguments?.getInt("FLOORS").toString()
    }
}