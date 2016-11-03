/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.heighMapEditor;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;
import editor.common.ITool;
import editor.convexEditor.SubDrawer;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Nastroj pro transformovani terenu vyskove mapy
 * @author ggrrin_
 */
public class BrushTool implements ITool {

    private MapGLJPanel myPanel;
    private SubDrawer sd;   
    private final float sensitivity = 3f;

    private boolean drawMode = false; 
    private int selectedIndex = -1;
    private List<Integer> selectedIndices = null;
    private Vector3 prevSelectedVertex = null;
    //////////////////////////////////
    //parameters
    
    /**
     * Polomer stetce
     */
    public int radius = 5;
    
    /**
     * Relativny uprava terenu vs absolutni
     */
    public boolean relative = true;
    
    /**
     * Typ vybraneho stece
     */
    public BrushType brush = BrushType.flat;

    
    /**
     * Incilizuje nastroj s danym vykreslovacim panelem
     * @param com vykreslovaci panel
     */
    public BrushTool(MapGLJPanel com) {
        this.myPanel = com;
        sd = new SubDrawer(com.getRenderer());
        com.getRenderer().registerComponent(sd);
    }

    /**
     * Vrati rozmery mapy
     * @return rozmery mapy sirka, hloubka
     */
    public  Point getSize()
    {
        return new Point(myPanel.map.width, myPanel.map.depth);
    }
    
    @Override
    public void reset() {
        drawMode = false;
        selectedIndex = -1;
        selectedIndices = null;
        prevSelectedVertex = null;
        sd.resetIndexBuffer(new ArrayList<>());
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

  /**
   * Aktualizace pozicie ridiciho bodu
   * transformace terenu
   * @param me mouse event
   */
    @Override
    public void mouseMoved(MouseEvent me) {

        if (!drawMode) {
            int vertex = myPanel.getMatchedVertex(me.getX(), me.getY(), sensitivity, myPanel.getRenderer().getCurentModel().getVertexBuffer());
            if (vertex != -1) {
                ArrayList<Integer> i = new ArrayList<>();
                i.add(selectedIndex = vertex);
                sd.resetIndexBuffer(i);
            }
        } else {
            updateMap(me.getPoint());
        }
    }

    /**
     * transformace terenu dle  pohybu mysi
     * @param mousePoint  soradnice mysi na platne
     */
    private void updateMap(Point mousePoint) {
        myPanel.save = true;
        List<Vector3> vb = myPanel.map.getVertexBuffer();
        Vector3 center = vb.get(selectedIndex);

        Vector3 pointer = myPanel.screenToWorld(mousePoint.x, mousePoint.y, new OrientatedPlane(myPanel.mulMatrix(center, myPanel.map.getModelMatrix()), new Vector3(0, 0, 1f)));
        pointer = myPanel.mulMatrix(pointer, myPanel.getInverse(myPanel.map.getModelMatrix()));
        //center.Y = pointer.Y;

        for (int i : selectedIndices) {
            Vector3 current = vb.get(i);
            Vector3 line = center.sub(current);
            line.Y = 0;
            float distance = line.length();

            switch (brush) {
                case cosFull:
                case cosHalf:
                    cosBrush(current, pointer, distance);
                    break;

                case linear:
                    linearBrush(current, pointer, distance);
                    break;
                case flat:
                    flatBrush(current, pointer);
            }
        }
        prevSelectedVertex = vb.get(selectedIndex).Clone();

        myPanel.getRenderer().invokeLater(g -> myPanel.map.restartBuffers(g));
    }

    /**
     * Stetec transformujici mapu pouze rovinou
     * @param modify
     * @param pointer 
     */
    private void flatBrush(Vector3 modify, Vector3 pointer) {  
        if (!relative) {
            modify.Y = pointer.Y;
        } else {
            float diff = (pointer.Y - prevSelectedVertex.Y);
            modify.Y += diff;
        }
    }

    /**
     * Transformu je mapu linearne mezi pozici ridiciho bodu a ostatnim terenem
     * @param modify
     * @param pointer
     * @param distance 
     */
    private void linearBrush(Vector3 modify, Vector3 pointer, float distance) {
        float lin = -(1f / radius) * distance + 1;        

        if (!relative) {
            modify.Y = pointer.Y * lin;
        } else {
            float diff = (pointer.Y - prevSelectedVertex.Y);
            modify.Y += diff * lin;
        }
    }

    /**
     * Transformu je mapu dle fce cos mezi ridicim bodem a ostatnim terenem
     * @param modify
     * @param pointer
     * @param distance 
     */
    private void cosBrush(Vector3 modify, Vector3 pointer, float distance) {
        float i, j, k;
        if (brush == BrushType.cosFull) {
            i = 1;
            j = 2;
            k = 1;

        } else {
            i = 0;
            j = 1;
            k = 2;
        }

        double param = distance * Math.PI / k / (radius);

        float cos = ((float) Math.cos(param) + i) / j;

        if (!relative) {
            modify.Y = pointer.Y * cos;
        } else {
            float diff = (pointer.Y - prevSelectedVertex.Y);
            modify.Y += diff * cos;
        }

    }

    /**
     * Zapnuti/vypnuti transformovani dle danneho ridiciho bodu
     * @param me mouse event
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == me.BUTTON1) {
            if (selectedIndex != -1) {
                drawMode ^= true;
                if (drawMode) {
                    prevSelectedVertex = myPanel.map.getVertexBuffer().get(selectedIndex).Clone();
                    sd.resetIndexBuffer(selectedIndices = myPanel.map.getNeigbours(selectedIndex, radius));
                } else {
                    mouseMoved(me);
                    prevSelectedVertex = null;
                }
            }
        }
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
    }

}
