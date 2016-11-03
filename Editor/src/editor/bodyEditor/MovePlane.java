/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bodyEditor;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;
import editor.common.BoundingBox;
import editor.common.InteractiveGLJPanel;
import editor.common.SimpleDrawer;
import java.awt.Point;

/**
 * Reprezentuje "kreslici platno" (kreslici krychly), pomoci ktere uzivatel
 * interaguje s pozici objektu
 * @author ggrrin_
 */
public class MovePlane {

    private SimpleDrawer sd;
    private BoundingBox box = new BoundingBox(new Vector3(-5, -5, -5), new Vector3(5, 5, 5));

    private OrientatedPlane plane;
    private InteractiveGLJPanel panel;

    private Vector3 position = new Vector3(0, 0, 0);

    private boolean zDirection = true;

    /**
     * inicializuje kreslici platno, a propojis odpovidajicim vykreslovacim panleme
     * @param panel platno
     */
    public MovePlane(InteractiveGLJPanel panel) {
        this.panel = panel;
        sd = new SimpleDrawer(new Vector3(0, 1, 0), 3, 100);
        panel.getRenderer().registerComponent(sd);
        plane = new OrientatedPlane(position, new Vector3(0, 0, 1));
        update();
    }

    /**
     * Prehodi platno mezi mezi platnem pousouvajici se v Z smeru ci X smeru
     */
    public void exchangeDirection() {
        zDirection ^= true;
        resetPlane();
        update();
    }

    /**
     * Restartuje objekt do pocatecniho stavu
     */
    private void resetPlane() {
        if (zDirection) {
            plane = new OrientatedPlane(position, new Vector3(0, 0, 1));
        } else {
            plane = new OrientatedPlane(position, new Vector3(1, 0, 0));
        }
    }

    /**
     * Posune zvyraznovac do aktualniho bodu tak aby odpovidal kurzoru a
     * a aktualizuje pozici 
     * @param x delta posunu v posouvacim smeru
     * @param screenPoint bod na komponente na kterem je cursor mysi
     * @return nova pozice
     */
    public Vector3 move(float x, Point screenPoint) {
        if (zDirection) {
            position = position.plus(new Vector3(0, 0, x));
            position.Z = (float) Math.max(box.getMin().Z, Math.min(box.getMax().Z, position.Z));
        } else {
            position = position.plus(new Vector3(x, 0, 0));
            position.X = (float) Math.max(box.getMin().X, Math.min(box.getMax().X, position.X));
        }
        resetPlane();
        Vector3 pos = getIntersection(screenPoint.x, screenPoint.y);
        update();
        
        return pos;
    }

    /**
     * Nastavi pozici bodu urcujici pozici platna
     * @param pos  pozice v svetochvych souradnicich
     */
    public void setPosition(Vector3 pos) {
        if (box.intersects(pos)) {
            position = pos;
            resetPlane();
            update();
        }
    }

    /**
     * Vrati pozici bodu urcujici pozici platna
     * @return pozice v svetovych souradnicich
     */
    public Vector3 getCurrentPosition() {
        return position.Clone();
    }

    /**
     *  Vrati pozici na kterej momentalen curzor v svetovych souradnicich
     * @param x x pozice kursoru
     * @param y y pozide kursoru
     * @return pozice na platne v 3D
     */
    public Vector3 getIntersection(int x, int y) {
        Vector3 wrldPoint = panel.screenToWorld(x, y, plane);
        if (box.intersects(wrldPoint)) {
            position = wrldPoint;
        }
        update();
        return position;
    }

    protected Vector3 yellow = new Vector3(1, 0.75f, 0);
    protected Vector3 green = new Vector3(0, 1, 0);

    /**
     * Aktualizuje vykreslovani platna dle stavu oplatna
     */
    private void update() {

        Vector3 a, b, c, d;
        if (zDirection) {
            a = new Vector3(box.getMin().X, box.getMin().Y, position.Z);
            b = new Vector3(box.getMax().X, box.getMin().Y, position.Z);
            c = new Vector3(box.getMax().X, box.getMax().Y, position.Z);
            d = new Vector3(box.getMin().X, box.getMax().Y, position.Z);
        } else {
            a = new Vector3(position.X, box.getMin().Y, box.getMin().Z);
            b = new Vector3(position.X, box.getMin().Y, box.getMax().Z);
            c = new Vector3(position.X, box.getMax().Y, box.getMax().Z);
            d = new Vector3(position.X, box.getMax().Y, box.getMin().Z);
        }

        //bBox
        sd.clear();
        sd.addBbox(box, new Vector3(0, 0, 0));
        sd.addSegment(a, b, yellow);
        sd.addSegment(b, c, yellow);
        sd.addSegment(c, d, yellow);
        sd.addSegment(d, a, yellow);

        if (position != null && box.intersects(position)) {
            sd.addSegment(new Vector3(box.getMin().X, position.Y, position.Z), new Vector3(box.getMax().X, position.Y, position.Z), green);
            sd.addSegment(new Vector3(position.X, box.getMin().Y, position.Z), new Vector3(position.X, box.getMax().Y, position.Z), green);
            sd.addSegment(new Vector3(position.X, position.Y, box.getMin().Z), new Vector3(position.X, position.Y, box.getMax().Z), green);
        }

        panel.getRenderer().invokeLater((g) -> sd.updateBuffers(g));
    }

    /**
     * nastavi platno do pocatecniho stavu
     */
    void reset() {
        position = new Vector3(0,0,0);
        update();
    }
}
