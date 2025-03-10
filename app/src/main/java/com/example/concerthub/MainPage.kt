package com.example.concerthub


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.concerthub.MainPageActivity.AccountFragment
import com.example.concerthub.MainPageActivity.EventFragment
import com.example.concerthub.MainPageActivity.HistoryFragment
import com.example.concerthub.MainPageActivity.HomeFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainPage : AppCompatActivity() {
    private lateinit var BottomNavMenu: ChipNavigationBar
    private lateinit var fragment: Fragment

    companion object {
        const val EXTRA_FRAGMENT_ID = "extra_fragment_id"
        const val FRAGMENT_HOME = "home"
        const val FRAGMENT_EVENT = "event"
        const val FRAGMENT_HISTORY = "history"
        const val FRAGMENT_ACCOUNT = "account"

        fun getIntent(context: Context, fragmentId: String): Intent {
            val intent = Intent(context, MainPage::class.java)
            intent.putExtra(EXTRA_FRAGMENT_ID, fragmentId)
            return intent
        }
    }

    fun redirectToFragment(fragmentId: String) {
        fragment = when (fragmentId) {
            MainPage.FRAGMENT_EVENT -> EventFragment()
            MainPage.FRAGMENT_HISTORY -> HistoryFragment()
            MainPage.FRAGMENT_ACCOUNT -> AccountFragment()
            else -> HomeFragment()
        }
        openMainFragment()
        BottomNavMenu.setItemSelected(getBottomNavId(fragmentId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)

        BottomNavMenu = findViewById(R.id.BottomNavMenu)

        val fragmentId = intent.getStringExtra(EXTRA_FRAGMENT_ID) ?: FRAGMENT_HOME
        fragment = when (fragmentId) {
            FRAGMENT_EVENT -> EventFragment()
            FRAGMENT_HISTORY -> HistoryFragment()
            FRAGMENT_ACCOUNT -> AccountFragment()
            else -> HomeFragment()
        }

        // Menampilkan fragment yang sesuai saat aktivitas dibuat
        openMainFragment()

        BottomNavMenu.setItemSelected(getBottomNavId(fragmentId))

        BottomNavMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    fragment = HomeFragment()
                    openMainFragment()
                }

                R.id.event -> {
                    fragment = EventFragment()
                    openMainFragment()
                }

                R.id.history -> {
                    fragment = HistoryFragment()
                    openMainFragment()
                }

                R.id.account -> {
                    fragment = AccountFragment()
                    openMainFragment()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun openMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    private fun getBottomNavId(fragmentId: String): Int {
        return when (fragmentId) {
            FRAGMENT_EVENT -> R.id.event
            FRAGMENT_HISTORY -> R.id.history
            FRAGMENT_ACCOUNT -> R.id.account
            else -> R.id.home
        }
    }
}
