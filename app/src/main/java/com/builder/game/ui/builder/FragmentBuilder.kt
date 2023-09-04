package com.builder.game.ui.builder

import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.builder.game.R
import com.builder.game.ui.other.ViewBindingFragment
import com.builder.game.databinding.FragmentBuilderBinding
import com.builder.game.domain.Difficulty
import com.builder.game.domain.Floor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentBuilder :
    ViewBindingFragment<FragmentBuilderBinding>(FragmentBuilderBinding::inflate) {
    private val viewModel: BuilderViewModel by viewModels()
    private val args: FragmentBuilderArgs by navArgs()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.floorsLayout.setOnClickListener {
            if (!viewModel.isFallingDown && !viewModel.isTimeUp) {
                viewModel.launchFloor(binding.crane.x, binding.craneFloor.y)
            }
        }

        viewModel.endCallback = {
            end()
        }

        if (!args.isContinue) {
            viewModel.craneSpeed = when (args.difficulty) {
                Difficulty.EASY -> 10
                Difficulty.INTERMEDIATE -> 20
                Difficulty.HARD -> 30
            }
        }

        binding.protect.setOnClickListener {
            viewModel.isProtected = true
        }

        viewModel.craneXY.observe(viewLifecycleOwner) {
            binding.crane.x = it.x
            binding.crane.y = it.y

            val floor2 = viewModel.flyingFloor.value!!
            val floorImg = when (floor2.img) {
                1 -> R.drawable.floor01
                2 -> R.drawable.floor02
                else -> R.drawable.floor03
            }
            binding.craneFloor.setImageResource(floorImg)
        }

        viewModel.piggies.observe(viewLifecycleOwner) {
            binding.piggies.x = it.x
            binding.piggies.y = it.y
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            binding.progress.layoutParams = binding.progress.layoutParams.apply {
                width = dpToPx(it)
            }
            binding.protect.isVisible = it <= 20 && !viewModel.isTimeUp && it != 0
        }

        viewModel.isWolf.observe(viewLifecycleOwner) {
            binding.wolf.isVisible = it
            binding.piggies.isVisible = it && viewModel.isProtected
        }

        viewModel.flyingFloor.observe(viewLifecycleOwner) { floor ->
            binding.craneFloor.setImageDrawable(null)
            binding.flyingFloorLayout.removeAllViews()
            if (viewModel.isFallingDown) {
                val floorView = ImageView(requireContext())
                floorView.layoutParams =
                    ViewGroup.LayoutParams(binding.craneFloor.width, binding.craneFloor.height)
                floorView.setImageResource(
                    when (floor!!.img) {
                        1 -> R.drawable.floor01
                        2 -> R.drawable.floor02
                        else -> R.drawable.floor03
                    }
                )
                floorView.rotation = when {
                    floor.rotatingLeft -> -30f
                    floor.rotatingRight -> 30f
                    else -> 0f
                }
                floorView.x = floor.x
                floorView.y = floor.y
                binding.flyingFloorLayout.addView(floorView)
            }
        }

        viewModel.currentFloors.observe(viewLifecycleOwner) {
            binding.floorsAmount.text = it.size.toString()
            binding.floorsLayout.removeAllViews()

            if (it.isEmpty() && viewModel.gameState && viewModel.isInit) {
                end()
            }

            lifecycleScope.launch {
                delay(5)
                if (it.isEmpty() && !viewModel.isInit) {
                    lifecycleScope.launch {
                        delay(20)

                        if (!args.isContinue) {
                            viewModel.initFloor((xy.first / 2 - (binding.craneFloor.width / 2)).toFloat())
                        }
                        viewModel.isInit = true

                        if (args.isContinue) {
                            viewModel.initFromDB()
                        }
                    }
                }
                if (it.size > 4) {
                    val currentList = it.toMutableList()
                    val newList = mutableListOf<Floor>()
                    repeat(4) {
                        newList.add(currentList.last())
                        currentList.removeAt(currentList.size - 1)
                    }
                    newList.forEach { floor ->
                        val floorView = ImageView(requireContext())
                        floorView.layoutParams = LinearLayout.LayoutParams(
                            binding.craneFloor.width,
                            binding.craneFloor.height
                        ).apply {
                            marginStart = floor.x.toInt()
                        }
                        floorView.setImageResource(
                            when (floor.img) {
                                1 -> R.drawable.floor01
                                2 -> R.drawable.floor02
                                else -> R.drawable.floor03
                            }
                        )
                        binding.floorsLayout.addView(floorView)
                    }
                } else {
                    val list = it.reversed()
                    list.forEach { floor ->
                        val floorView = ImageView(requireContext())
                        floorView.layoutParams = LinearLayout.LayoutParams(
                            binding.craneFloor.width,
                            binding.craneFloor.height
                        ).apply {
                            marginStart = floor.x.toInt()
                        }
                        floorView.setImageResource(
                            when (floor.img) {
                                1 -> R.drawable.floor01
                                2 -> R.drawable.floor02
                                else -> R.drawable.floor03
                            }
                        )
                        binding.floorsLayout.addView(floorView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.gameState) {
                viewModel.start(
                    binding.craneFloor.width,
                    xy.first.toFloat(),
                    0f,
                    xy.second.toFloat(),
                    binding.craneFloor.width,
                    binding.craneFloor.height
                )
            }
        }
    }

    private fun end() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.deleteGame()
            viewModel.stop()
            viewModel.gameState = false
            findNavController().navigate(
                FragmentBuilderDirections.actionFragmentBuilderToDialogLose(
                    viewModel.currentFloors.value!!.size,
                    when (viewModel.craneSpeed) {
                        10 -> Difficulty.EASY
                        20 -> Difficulty.INTERMEDIATE
                        else -> Difficulty.HARD
                    }
                )
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    override fun onStop() {
        super.onStop()
        if (viewModel.gameState) {
            viewModel.save()
        }
    }
}