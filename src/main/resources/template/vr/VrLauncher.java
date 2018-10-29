package ${package};

import com.jme3.app.VRAppState;
import com.jme3.app.VREnvironment;
import com.jme3.system.AppSettings;

class VrLauncher extends VRAppState  {
    public VrLauncher(AppSettings settings, VREnvironment environment) {
        super(settings, environment);
        Main app=new Main();
        initialize(app.getStateManager(),app);
        getApplication().start();
    }
}
