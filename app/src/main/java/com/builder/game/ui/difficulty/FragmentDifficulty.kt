package com.builder.game.ui.difficulty

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.builder.game.domain.Difficulty
import com.builder.game.ui.other.ViewBindingFragment
import com.builder.game.R
import com.builder.game.databinding.FragmentDifficultyBinding

class FragmentDifficulty: ViewBindingFragment<FragmentDifficultyBinding>(FragmentDifficultyBinding::inflate) {
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
            findNavController().popBackStack()
        }

        binding.play.setOnClickListener {
            findNavController().navigate(FragmentDifficultyDirections.actionFragmentDifficultyToFragmentBuilder(viewModel.difficulty.value!!, false))
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