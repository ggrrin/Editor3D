/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bodyEditor;

import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.ITool;
import editor.common.InteractiveGLJPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Nastroj pro pozicovani objektu na platne
 * @author ggrrin_
 */
public class PositioningTool implements ITool {

    private BodyGLJPanel component;

    /**
     * Inicializuje nastroj a propoji s danym platnem
     * @param cmp vykreslovaci panel
     */
    public PositioningTool(BodyGLJPanel cmp) {
        component = cmp;
    }

    private boolean moving = false;

    @Override
    public void reset() {
        moving = false;
    }

    /**
     * Presune vybrany objekt na pozici pos
     * @param pos pozice kam presunout vybrany objekt
     */
    private void moveObj(Vector3 pos) {        
        Vector3 transPoint = InteractiveGLJPanel.mulMatrix(pos, component.getInverse(component.getModelmatrix()));        
        Vector3 translation = transPoint.sub(component.selectedModel.getCenteroid());   
        Matrix4 modMat = component.selectedModel.getModelMatrix();

        synchronized (modMat) {            
            modMat.translate(translation.X, translation.Y, translation.Z);//.multMatrix(move);
        }
        component.save = true;
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        Vector3 pos = component.plane.getIntersection(me.getX(), me.getY());
        if (moving) {
            moveObj(pos);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Vector3 pos = component.plane.move(e.getWheelRotation() / 4f, e.getPoint());
        if (moving) {
            moveObj(pos);
        }
    }

    /**
     * Vypinani/zapinani pozicovani
     * @param me event
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == me.BUTTON2) {
            component.plane.exchangeDirection();
        } else if (me.getButton() == me.BUTTON1) {
            if (component.selectedModel != null) {
                moving ^= true;
                if (moving) {
                    Vector3 cenV = component.selectedModel.getCenteroid(component.getModelmatrix());

                    component.plane.setPosition(cenV);
                    component.moveMouseToPoint(cenV);
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

}
