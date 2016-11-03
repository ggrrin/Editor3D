/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bodyEditor;

import Polyhedron.ConvexPolyhedronBuilder;
import Polyhedron.Model;
import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.ModelRender;
import editor.common.Renderer;
import editor.common.ToolsGLJPanel;
import editor.convexEditor.ConvexModel;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * Trida reprezentuji vykreslovaci panel a implmentujici funkcionalitu
 * editoru nekonvexnich objektu
 * @author ggrrin_
 */
public class BodyGLJPanel extends ToolsGLJPanel {

    private final Vector3 selecteColor = new Vector3(1, 0.9f, 0);
    private final Vector3 wireColor = new Vector3(0, 0, 0);

    private ModelRender render;

    private PositioningTool pos;
    private BodyModel model;

    MovePlane plane;
    ConvexModel selectedModel = null;

    /**
     * vytvori novy panel
     */
    public BodyGLJPanel() {
        modelMat = new Matrix4();
        render = new ModelRender();
        plane = new MovePlane(this);
        pos = new PositioningTool(this);
        currentTool = pos;
        model = new BodyModel(modelMat, "noname");
        render.setCurrentModel(model);
        Start();
    }

    
    /**
     * Prida do modelu novy objekt nazteny ze souboru f
     * @param f soubor kde je objekt ulozen
     * @return vrati referenci na ulozeny objekt
     * @throws IOException chyba se souborem
     */
    public Object addConvex(File f) throws IOException {
        ConvexPolyhedronBuilder b = new ConvexPolyhedronBuilder(Model.readInputFile(f.getPath()), true);
        b.Calculate();
        ConvexModel m = new ConvexModel(b.model, new Matrix4());
        m.name = f.getName();
        model.addElement(m);
        super.save = true;
        return m;
    }

    
    /**
     * Odstrani objekt z editoru
     * @param o reference objektu
     */
    public void removeConvex(Object o) {
        model.removeElement(o);

        if (selectedModel == o) {
            selectedModel = null;
            pos.reset();
        }
        super.save = true;
    }

    
    /**
     * Vybere(oznaci) dany objekt o
     * @param o reference na objekt
     */
    public void selectConvex(Object o) {
        if (selectedModel != null) {
            selectedModel.setWireColor(wireColor);
        }

        if (o != null) {
            ConvexModel m = (ConvexModel) o;
            selectedModel = m;
            selectedModel.setWireColor(selecteColor);
            //Vector3 centr = InteractiveGLJPanel.mulMatrix(m.getCenteriod(), modelMat);
        } else {
            selectedModel = null;
        }
    }

    /**
     * Presune kurzor na pozici tak aby odpovidala bodu center v 3D prostoru
     * @param centr bod ve svetovych souradnicich
     */
    void moveMouseToPoint(Vector3 centr) {
        Vector3 xy = worldToScreen(centr);

        try {
            Robot r = new Robot();

            Point p = new Point((int) xy.X, (int) xy.Y);
            SwingUtilities.convertPointToScreen(p, this);

            r.mouseMove(p.x, p.y);
        } catch (AWTException e) {
            System.err.println("unable to move cursor");
        }
    }

    @Override
    public void open(File path) throws IOException {
        reset();
        model = BodyModel.load(path);
        render.setCurrentModel(model);
        modelMat = model.getModelMatrix();
        super.save = false;
    }

    /**
     * Vrati referenci na cely model
     * @return model
     */
    public BodyModel getModel() {
        return model;
    }

    @Override
    public void reset() {
        pos.reset();
        plane.reset();
        modelMat.loadIdentity();
        model = new BodyModel(modelMat, "noname");
        render.setCurrentModel(model);
        super.save = false;
    }

    @Override
    public void save(File path) throws IOException {
        model.export(path);
        super.save = false;
    }

    @Override
    public Renderer getRenderer() {
        return render;
    }

}
