package info.schedule.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import info.schedule.R
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    var itemToAccount: MenuItem? = null
    var itemToHome: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        Timber.d("OncreATE")
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.my_nav_host_fragment).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_toolbar,menu)
        itemToAccount = menu?.findItem(R.id.accountFragment)
        itemToHome = menu?.findItem(R.id.scheduleFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.my_nav_host_fragment)
        when(item.itemId) {
            R.id.accountFragment -> {
                itemToAccount?.isVisible = false
                itemToHome?.isVisible = true
                navController.navigate(R.id.accountFragment)
            }
            R.id.scheduleFragment -> {
                navController.navigate(R.id.scheduleFragment)
            }
            /*navController.currentDestination?.id ->{
                Timber.d("fuck YOU")
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

 /*   override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("account", itemToAccount?.isVisible!!)
        outState.putBoolean("home", itemToHome?.isVisible!!)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }*/

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.my_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController)
        toolbar.setupWithNavController(navController,appBarConfiguration)
    }
}
