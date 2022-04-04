package com.example.mynetwork.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.mynetwork.auth.AppAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mynetwork.R
import com.example.mynetwork.databinding.ActivityAppBinding
import com.example.mynetwork.viewmodel.AuthViewModel
import com.example.mynetwork.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_posts, R.id.navigation_events, R.id.navigation_users, R.id.navigation_profile -> {
                    navView.visibility = View.VISIBLE
                }
                else -> {
                    navView.visibility = View.GONE
                }
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_posts,
                R.id.navigation_events,
                R.id.navigation_users,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val itemIcon = navView.menu.findItem(R.id.navigation_profile)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
            if (it.id == 0L) {
                itemIcon.setIcon(R.drawable.ic_profile_avatar_selector)
            } else {
                userViewModel.getUserById(it.id)
            }
        }

        userViewModel.user.observe(this) {

            Glide.with(this)
                .asBitmap()
                .load("${it.avatar}")
                .transform(CircleCrop())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        itemIcon.icon = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.signInFragment)
                true
            }
            R.id.signup -> {
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.signUpFragment)
                true
            }
            R.id.signout -> {
                appAuth.removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}