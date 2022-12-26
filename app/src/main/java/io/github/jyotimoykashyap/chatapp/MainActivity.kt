package io.github.jyotimoykashyap.chatapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import io.github.jyotimoykashyap.chatapp.util.SharedPref
import io.github.jyotimoykashyap.chatapp.databinding.ActivityMainBinding
import io.github.jyotimoykashyap.chatapp.viewmodels.SharedViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        Log.i("chatapp" , "its here")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        SharedPref.createSharedPreferences(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration
            .Builder(R.id.homeFragment)
            .build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        observeChanges()
    }

    private fun observeChanges() {
        // show or hide loader state
        sharedViewModel.loaderState.observe(this) {
            binding.run {
                if (it) loader.show()
                else loader.hide()
            }
        }

        // changes in navigation component
        findNavController(R.id.nav_host_fragment_content_main)
            .addOnDestinationChangedListener { _, destination, _ ->
                Log.i(
                    "navigation",
                    "Destination -> ID: ${destination.id}     Label: ${destination.label}"
                )
                when(destination.label) {
                    "Welcome" -> supportActionBar?.hide()
                    "Home" -> supportActionBar?.show()
                }
            }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                // logout the user
                SharedPref.clearSharedPrefs()
                finish()
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}