package ${package}

import com.jme3.app.VRAppState
import com.jme3.app.VREnvironment
import com.jme3.system.AppSettings

internal class VrLauncher(settings: AppSettings, environment: VREnvironment) : VRAppState(settings, environment) {
    init {
        val app = Main()
        initialize(app.getStateManager(), app)
        getApplication().start()
    }
}