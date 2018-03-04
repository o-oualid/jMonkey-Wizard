package ${package};

import com.jme3.system.ios.IosHarness;

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
