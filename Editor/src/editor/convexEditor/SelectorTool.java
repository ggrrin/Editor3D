/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;
import editor.common.ITool;
import editor.common.InteractiveGLJPanel;
import editor.common.SelectorListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Nastroj pro vyber bodu konvexniho objektu
 * @author ggrrin_
 */
public class SelectorTool implements ITool {

    private static final Logger L = Logger.getLogger("selectore");

    private HashSet<Integer> selectedVertices;
    float selectorSensitivity;
    private SubDrawer sd;

    InteractiveGLJPanel myPanel;


    private SelectorTool(InteractiveGLJPanel iPanel, Vector3 color, float lineWidth, float pointSize, float sensitivity) {
        initalize(iPanel, color, lineWidth, pointSize, sensitivity);
    }

    /**
     * Inicializuje dany nastroj a propoji s vykreslovacim panelem
     * @param iPanel  vykreslovaci panel
     */
    public SelectorTool(InteractiveGLJPanel iPanel) {
        initalize(iPanel, new Vector3(1, 0.8f, 0), 5, 2, 5);
    }

    /**
     * Vrati mnozinu vybranych bodu
     * @return vybrane body
     */
    public Set<Integer> getPoints() {
        return selectedVertices;
    }

    
    HashSet<SelectorListener> listeners = new HashSet<>();
    
    /**
     * registruje listener poslouchajici pridani bodu
     * @param l  novy listener
     */
    public void addSelectorListener(SelectorListener l)
    {
        listeners.add(l);
    }
    
    
    /**
     * odstrani listener poslouchajici pridani bodu
     * @param l listener l
     */
    public void removeSelectorListener(SelectorListener l)
    {
        listeners.remove(l);
    }
    
    /**
     * inicializuje nastorj dle paramatru
     * @param iPanel vykreslovaci panel
     * @param color barva bodu
     * @param lineWidth velikost car
     * @param pointSize velikost bodu
     * @param sensitivyty citlivost vyberu bodu
     */
    private void initalize(InteractiveGLJPanel iPanel, Vector3 color, float lineWidth, float pointSize, float sensitivyty) {
        myPanel = iPanel;

        sd = new SubDrawer(myPanel.getRenderer());

        myPanel.getRenderer().registerComponent(sd);
        selectedVertices = new HashSet<>();
        selectorSensitivity = sensitivyty;
    }

    /**
     * vyvola vsechny listenery
     */
    private void callListeners()
    {
        for(SelectorListener s : listeners)
        {
            s.itemsChanged(selectedVertices);
        }
    }
    
    /**
     * Vybere prave jeden bod z modelu urceny indexem z indexbufferu modelu i 
     * @param i index do index bufferu modelu
     */
    public void selectOne(int i) {

        //L.log(Level.SEVERE, "selected one vertex index -> " + i);

        selectedVertices.clear();
        selectedVertices.add(i);
        sd.resetIndexBuffer(selectedVertices);
        callListeners();
        
    }

    /**
     * Nastavi nastroj na pocatecni stav
     */
    public void reset() {
        selectedVertices.clear();
        sd.resetIndexBuffer(selectedVertices);
        callListeners();
    }

    /**
     * Prida do vybranych bodu dalsi bod urceny i 
     * @param i  index do index bufferu modelu
     */
    public void selectMultiple(int i) {
        if (selectedVertices.contains(i)) {
            //L.log(Level.SEVERE, "removed " + i);
            selectedVertices.remove(i);
        } else {
            //L.log(Level.SEVERE, "added " + i);
            selectedVertices.add(i);
        }
        sd.resetIndexBuffer(selectedVertices);
        callListeners();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevMovePoint = null;
        movePlane = null;
    }

    Vector3 prevMovePoint = null;
    OrientatedPlane movePlane = null;

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Vyber bodu
     * @param me mouse event
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3 || me.getButton() == MouseEvent.BUTTON1) {
            int vertex = myPanel.getMatchedVertex(me.getX(), me.getY(), selectorSensitivity, myPanel.getRenderer().getCurentModel().getVertexBuffer());
            if (vertex != -1) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    selectOne(vertex);
                } else if (me.getButton() == MouseEvent.BUTTON3) {
                    selectMultiple(vertex);
                }
            } else {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    reset();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
}
