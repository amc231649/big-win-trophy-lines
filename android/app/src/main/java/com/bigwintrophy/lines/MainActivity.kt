package com.bigwintrophy.lines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.core.navigation.Navigator
import com.bigwintrophy.lines.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ServiceLocator.init(applicationContext)
        super.onCreate(savedInstanceState)
        val inflated = ActivityMainBinding.inflate(layoutInflater)
        binding = inflated
        setContentView(inflated.root)
        if (savedInstanceState == null) {
            Navigator.showSplash(supportFragmentManager)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
