package ${package};

import com.jme3.app.VRAppState;
import com.jme3.app.VREnvironment;
import com.jme3.system.AppSettings;

/**
 * Created by: ouazrou-oualid on: 15/01/2018 package: com.jme.game project: jme.
 */

class VrLauncher extends VRAppState  {

    public VrLauncher(AppSettings settings, VREnvironment environment) {
        super(settings, environment);
        Main app=new Main();
        initialize(app.getStateManager(),app);
        getApplication().start();
    }
}
