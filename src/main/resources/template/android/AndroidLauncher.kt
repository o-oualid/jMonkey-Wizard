package ${package}

import android.os.Bundle
import com.jme3.app.AndroidHarness

class AndroidLauncher : AndroidHarness() {
    fun onCreate(savedInstanceState: Bundle) {
        app = Main()
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}