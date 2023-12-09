package com.technogenis.neighborlly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.constant.Collections
import com.technogenis.neighborlly.databinding.ActivityMainBinding
import com.technogenis.neighborlly.fragment.EventsFragment
import com.technogenis.neighborlly.fragment.FeedsFragment
import com.technogenis.neighborlly.fragment.HomeFragment
import com.technogenis.neighborlly.fragment.PollsFragment
import com.technogenis.neighborlly.fragment.ProfileFragment
import com.technogenis.neighborlly.fragment.SettingFragment
import com.technogenis.neighborlly.start.LoginActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setSupportActionBar(binding.toolbar)
        binding.navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }

    }

    override
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
                binding.toolbar.title = "Neighborhood"
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
                binding.toolbar.title = "Profile"
            }
            R.id.nav_feed -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FeedsFragment()).commit()
                binding.toolbar.title = "Feeds"
            }
            R.id.nav_events -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EventsFragment()).commit()
                binding.toolbar.title = "Events"
            }

            R.id.nav_polls -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PollsFragment()).commit()
                binding.toolbar.title = "Polls"
            }

            R.id.nav_faq -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()

            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingFragment()).commit()
                binding.toolbar.title = "Setting"
            }
            R.id.nav_logout -> signOut()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut() {
        val user = auth.currentUser?.uid
        if (user!=null)
        {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}