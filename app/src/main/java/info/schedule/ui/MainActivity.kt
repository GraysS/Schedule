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


class MainActivity : AppCompatActivity() {

    var itemToAccount: MenuItem? = null
    var itemToHome: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.my_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController)
        toolbar.setupWithNavController(navController,appBarConfiguration)
    }
}
