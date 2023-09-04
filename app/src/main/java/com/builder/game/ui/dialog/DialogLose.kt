package com.builder.game.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.builder.game.R
import com.builder.game.core.library.ViewBindingDialog
import com.builder.game.databinding.DialogLoseBinding
import com.builder.game.ui.navigation.FragmentNavigationDirections

class DialogLose : ViewBindingDialog<DialogLoseBinding>(DialogLoseBinding::inflate) {
    private val args: DialogLoseArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(FragmentNavigationDirections.actionFragmentMainToFragmentBuilder(args.difficulty, false))
        }

        binding.floorsAmount.text = args.floors.toString()
    }
}