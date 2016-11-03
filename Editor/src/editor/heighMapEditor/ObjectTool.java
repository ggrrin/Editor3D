/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.heighMapEditor;

import Polyhedron.Vector3;
import editor.common.ITool;
import editor.common.SimpleDrawer;
import editor.convexEditor.SubDrawer;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

/**
 * Nastroj pro pozicovani objektu na vyskove mape
 * @author ggrrin_
 */
public class ObjectTool implements ITool {

    private boolean rotMode = false;
    private MapGLJPanel myPanel;
    private final float sensitivity = 7f;
    private int selectedIndex = -1;
    private SubDrawer sd;
    private SimpleDrawer sdp;

    Vector3 pointer = new Vector3(0, 0, 0);

    /**
     * inicializuje nastroj a propoji s dannym panelem
     * @param aThis 
     */
    ObjectTool(MapGLJPanel aThis) {
        this.myPanel = aThis;
        sd = new SubDrawer(aThis.getRenderer());
        aThis.getRenderer().registerComponent(sd);

        sdp = new SimpleDrawer(new Vector3(1, 0, 0), 3, 5f);
        aThis.getRenderer().registerComponent(sdp);
        reset();
    }

    /**
     * Nastavi bod do ktereho se presovaji objekty
     * @param p novy bod
     */
    private void setPointer(Vector3 p) {
        pointer = p;
        sdp.clear();
        if (p != null) {
            sdp.addPoint(pointer);
        }
        myPanel.getRenderer().invokeLater(g -> sdp.updateBuffers(g));
    }

    @Override
    public void init() {
        setPointer(myPanel.map.getVertexBuffer().get(myPanel.map.getOriginIndex()));
    }

    @Override
    public void reset() {
        sdp.setMatrix(myPanel.getModelmatrix());
        rotMode = false;
        selectedIndex = -1;

        sd.resetIndexBuffer(new ArrayList<>());
        setPointer(null);
    }

    private Point lastDrag = null;
    private final float moveFactor = 0.05f;

    /**
     * Posouvani objektu ve svislem smeru
     * @param me mouse event
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        //kolecko 8
        //prave tlacitko 4
        if (me.getModifiers() != 4 && me.getModifiers() != 8) {

            if (lastDrag != null) {
                if (myPanel.selectedModel != null) {
                    Vector3 t = new Vector3(0, moveFactor * (lastDrag.y - me.getPoint().y), 0);
                    myPanel.selectedModel.getModelMatrix().translate(t.X, t.Y, t.Z);
                    myPanel.save = true;
                }
            }
            lastDrag = me.getPoint();
        }
    }

    /**
     * zvyraznovani bodu dle kurzoru
     * @param me mouse event
     */
    @Override
    public void mouseMoved(MouseEvent me) {

        if (!rotMode) {
            int vertex = myPanel.getMatchedVertex(me.getX(), me.getY(), sensitivity, myPanel.getRenderer().getCurentModel().getVertexBuffer());
            if (vertex != -1) {
                ArrayList<Integer> i = new ArrayList<>();
                i.add(selectedIndex = vertex);
                sd.resetIndexBuffer(i);
            }
        }
    }

    /**
     * vyber noveho pointeru dle vybraneho pointeru , presunuti teles na pointer
     * @param me mouse event
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == me.BUTTON1) {
            if (selectedIndex != -1) {
                setPointer(myPanel.map.getVertexBuffer().get(selectedIndex));

                if (myPanel.selectedModel != null) {

                    Vector3 v = pointer.sub(myPanel.selectedModel.getCenteroid());
                    myPanel.selectedModel.getModelMatrix().translate(v.X, v.Y, v.Z);//.multMatrix(trans);
                    myPanel.save = true;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == me.BUTTON1) {
            lastDrag = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {

    }

}
