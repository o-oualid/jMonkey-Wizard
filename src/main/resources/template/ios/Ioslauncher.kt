package ${package}

import com.jme3.system.ios.IosHarness

internal class IosLauncher(appDelegate: Long) : IosHarness(appDelegate) {

    override fun appPaused() {

    }

    override fun appReactivated() {
        app = Main()
        app.start()
    }

    override fun appClosed() {
        app.stop()
    }

    override fun appUpdate() {

    }

    override fun appDraw() {}

    override fun appReshape(width: Int, height: Int) {

    }
}
