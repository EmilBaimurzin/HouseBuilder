package com.builder.game.ui.navigation

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.builder.game.ui.other.ViewBindingFragment
import com.builder.game.core.library.shortToast
import com.builder.game.data.data_base.Database
import com.builder.game.databinding.FragmentNavigationBinding
import com.builder.game.domain.Difficulty
import com.builder.game.ui.builder.FragmentBuilder
import com.builder.game.ui.difficulty.FragmentDifficulty
import com.builder.game.ui.other.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentNavigation :
    ViewBindingFragment<FragmentNavigationBinding>(FragmentNavigationBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            play.setOnClickListener {
                (requireActivity() as MainActivity).navigate(FragmentDifficulty())
            }
            coontinue.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Default) {
                    val list = Database.instance.dao().getAllGames()
                    if (list.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            shortToast(requireContext(), "You have no savings")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            (requireActivity() as MainActivity).navigate(FragmentBuilder().apply {
                                arguments = Bundle().apply {
                                    putSerializable(
                                        "DIFFICULTY",
                                        Difficulty.EASY
                                    )
                                    putBoolean("IS_CONTINUE", true)
                                }
                            })
                        }
                    }
                }
            }
            exit.setOnClickListener {
                requireActivity().finish()
            }
        }
        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}