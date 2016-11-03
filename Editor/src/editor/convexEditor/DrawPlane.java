/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;
import editor.common.BoundingBox;
import editor.common.InteractiveGLJPanel;
import editor.common.SimpleDrawer;

/**
 * Trid reprezentujici kreslici platno pro kresleni bodu konvexniho objektu
 *
 * @author ggrrin_
 */
public class DrawPlane {

    protected SimpleDrawer sd;
    protected BoundingBox box = new BoundingBox(new Vector3(-5, -5, -5), new Vector3(5, 5, 5));

    protected OrientatedPlane plane;
    protected InteractiveGLJPanel panel;

    protected float z;

    /**
     * Inicializuje platno a propoji ho s danym vykreslovacim panelem
     *
     * @param panel vykreslovaci panel
     */
    public DrawPlane(InteractiveGLJPanel panel) {
        this.panel = panel;
        z = 0;
        sd = new SimpleDrawer(new Vector3(0, 1, 0), 3, 100);
        panel.getRenderer().registerComponent(sd);

        plane = new OrientatedPlane(new Vector3(0, 0, z), new Vector3(0, 0, 1));

        update(null);
    }

    /**
     *Vrati z pozici platna
     * @return z pozice platna
     */
    public float getMovePosition() {
        return z;
    }

    /**
     * Nastavi z pozici platna
     * @param z zmena posunu
     */
    public void setMovePosition(float z) {
        this.z = Math.max(box.getMin().Z, Math.min(box.getMax().Z, z));
        plane = new OrientatedPlane(new Vector3(0, 0, z), new Vector3(0, 0, 1));
    }

    /**
     * Vrati rovinu odpovidajici dannemu platnu
     * @return dana rovina
     */
    public OrientatedPlane getPlane() {
        return plane;
    }

    protected Vector3 yellow = new Vector3(1, 0.75f, 0);
    protected Vector3 green = new Vector3(0, 1, 0);

    /**
     * Aktualizuje vykreslovaci informace platna dle stavu
     * @param v pozice bodu s kterm interaguje cursor
     */
    public void update(Vector3 v) {

        Vector3 a = new Vector3(box.getMin().X, box.getMin().Y, z);
        Vector3 b = new Vector3(box.getMax().X, box.getMin().Y, z);
        Vector3 c = new Vector3(box.getMax().X, box.getMax().Y, z);
        Vector3 d = new Vector3(box.getMin().X, box.getMax().Y, z);

        //bBox
        sd.clear();
        sd.addBbox(box, new Vector3(0, 0, 0));
        sd.addSegment(a, b, yellow);
        sd.addSegment(b, c, yellow);
        sd.addSegment(c, d, yellow);
        sd.addSegment(d, a, yellow);

        if (v != null && box.intersects(v)) {
            sd.addSegment(new Vector3(a.X, v.Y, a.Z), v, green);
            sd.addSegment(new Vector3(b.X, v.Y, b.Z), v, green);
            sd.addSegment(new Vector3(v.X, b.Y, b.Z), v, green);
            sd.addSegment(new Vector3(v.X, c.Y, c.Z), v, green);
        }

        panel.getRenderer().invokeLater((g) -> sd.updateBuffers(g));
    }

}
