package com.builder.game.ui.navigation

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.builder.game.ui.other.ViewBindingFragment
import com.builder.game.R
import com.builder.game.core.library.shortToast
import com.builder.game.data.data_base.Database
import com.builder.game.databinding.FragmentNavigationBinding
import com.builder.game.domain.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentNavigation :
    ViewBindingFragment<FragmentNavigationBinding>(FragmentNavigationBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            play.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentDifficulty)
            }
            coontinue.setOnClickListener {
                lifecycleScope.launch (Dispatchers.Default) {
                    val list = Database.instance.dao().getAllGames()
                    if (list.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            shortToast(requireContext(), "You have no savings")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(
                                FragmentNavigationDirections.actionFragmentMainToFragmentBuilder(
                                    Difficulty.EASY,
                                    true
                                )
                            )
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