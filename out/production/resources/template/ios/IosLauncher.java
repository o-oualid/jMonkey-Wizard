package ${package};

import com.jme3.system.ios.IosHarness;

/**
 * Created by: ouazrou-oualid on: 15/01/2018 package: com.jme.game project: jme.
 */

class IosLauncher extends IosHarness{
    public IosLauncher(long appDelegate) {
        super(appDelegate);
    }

    @Override
    public void appPaused() {

    }

    @Override
    public void appReactivated() {
      app=new Main();
      app.start();
    }

    @Override
    public void appClosed() {
        app.stop();
    }

    @Override
    public void appUpdate() {

    }

    @Override
    public void appDraw() {
    }

    @Override
    public void appReshape(int width, int height) {

    }
}
