package com.builder.game.ui.other
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.builder.game.R
import com.builder.game.core.library.main
import com.builder.game.ui.navigation.FragmentNavigation


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, FragmentNavigation(), "main")
                .addToBackStack("main")
                .commit()
        }

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (supportFragmentManager.findFragmentById(R.id.fragment)?.tag != "main") {
                    supportFragmentManager.popBackStack()
                }
            }
        })
    }
    fun navigate(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToDialog(dialog: DialogFragment) {
        dialog.show(supportFragmentManager, "lose")
    }

    fun navigateBack(name: String = "") {
        if (name != "") {
            supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, FragmentNavigation(), "main")
                .addToBackStack(name)
                .commit()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
