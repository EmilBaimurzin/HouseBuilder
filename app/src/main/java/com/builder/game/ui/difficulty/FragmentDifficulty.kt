package com.builder.game.ui.difficulty

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels

import com.builder.game.domain.Difficulty
import com.builder.game.ui.other.ViewBindingFragment
import com.builder.game.R
import com.builder.game.databinding.FragmentDifficultyBinding
import com.builder.game.ui.builder.FragmentBuilder
import com.builder.game.ui.other.MainActivity

class FragmentDifficulty :
    ViewBindingFragment<FragmentDifficultyBinding>(FragmentDifficultyBinding::inflate) {
    private val viewModel: DifficultyViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.easy.setOnClickListener {
            viewModel.setDifficulty(Difficulty.EASY)
        }

        binding.intermediate.setOnClickListener {
            viewModel.setDifficulty(Difficulty.INTERMEDIATE)
        }

        binding.hard.setOnClickListener {
            viewModel.setDifficulty(Difficulty.HARD)
        }

        binding.close.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack()
        }

        binding.play.setOnClickListener {
            (requireActivity() as MainActivity).navigate(FragmentBuilder().apply {
                arguments = Bundle().apply {
                    putSerializable("DIFFICULTY", viewModel.difficulty.value!!)
                    putSerializable("IS_CONTINUE", false)
                }
            })
        }

        viewModel.difficulty.observe(viewLifecycleOwner) {
            binding.apply {
                if (it == Difficulty.EASY) {
                    easyImg.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.level03,
                            null
                        )
                    )
                } else {
                    easyImg.setImageDrawable(null)
                }
                if (it == Difficulty.INTERMEDIATE) {
                    intermediateImg.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.level03,
                            null
                        )
                    )
                } else {
                    intermediateImg.setImageDrawable(null)
                }
                if (it == Difficulty.HARD) {
                    hardImg.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.level03,
                            null
                        )
                    )
                } else {
                    hardImg.setImageDrawable(null)
                }
            }
        }
    }
}