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
import android.widget.Toast
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailabilityLight

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    private val authViewModel: AuthViewModel by viewModels()
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
                R.id.navigation_posts, R.id.navigation_events, R.id.navigation_users
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val itemIcon = navView.menu.findItem(R.id.navigation_profile)

        authViewModel.data.observe(this) { auth ->
            invalidateOptionsMenu()

            if (auth.id == 0L) {
                itemIcon.setIcon(R.drawable.ic_profile_avatar_selector)
            } else {
                userViewModel.getUserById(auth.id)
            }

            navView.menu.findItem(R.id.navigation_profile).setOnMenuItemClickListener {
                if (!authViewModel.authenticated) {
                    findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.signInFragment)
                    true
                } else {
                    userViewModel.getUserById(auth.id)
                    val bundle = Bundle().apply {
                        userViewModel.user.value?.id?.let { it -> putLong("id", it) }
                        putString("avatar", userViewModel.user.value?.avatar)
                        putString("name", userViewModel.user.value?.name)
                    }
                    findNavController(R.id.nav_host_fragment_activity_main).popBackStack()
                    findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.navigation_profile, bundle)
                    true
                }
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

        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
            }
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
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_posts)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorString(code)
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}