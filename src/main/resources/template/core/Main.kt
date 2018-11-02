package ${package}

class Main : SimpleApplication() {
    override fun simpleInitApp() {
        val b = Box(1, 1, 1)
        val geom = Geometry("Box", b)
        val mat = Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Blue)
        geom.setMaterial(mat)
        rootNode.attachChild(geom)
    }

    override fun simpleUpdate(tpf: Float) {

    }

    override fun simpleRender(rm: RenderManager) {

    }
}