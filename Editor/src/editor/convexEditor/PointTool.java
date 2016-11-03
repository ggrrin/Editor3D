/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.Vector3;
import editor.common.ITool;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Nastroj pro kresleni bodu na platno
 * @author ggrrin_
 */
public class PointTool implements ITool {

    ConvexGLJPanel component;

    /**
     * inicializuje nastroj a propoji s danym vykreslovacim platnem
     * @param t  vykreslovaci platno
     */
    public PointTool(ConvexGLJPanel t) {
        component = t;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {

        Vector3 v = component.screenToWorld(me.getX(), me.getY(), component.plane.getPlane());

        component.plane.update(v);
    }

    /**
     * Pridani bodu na pozici urcene platnem 
     */
    @Override
    public void mouseClicked(MouseEvent me) {

        if (me.getButton() == me.BUTTON1) {

            Vector3 worldPosition = component.screenToWorld(me.getX(), me.getY(), component.plane.getPlane());
            Vector3 modlePosition = component.mulMatrix(worldPosition, component.getInverse(component.getModelmatrix()));

            component.workingPoints.add(modlePosition);
            component.reBuild();
            component.save = true;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Posun platna v z smeru dle otoceni kolecka mysi
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float scrool = e.getWheelRotation();
        float newValue = component.plane.getMovePosition() + scrool / 4f;

        component.plane.setMovePosition(newValue);
        component.plane.update(component.screenToWorld(e.getX(), e.getY(), component.plane.getPlane()));
    }

    @Override
    public void reset() {

    }

}
