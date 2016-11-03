/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.ConvexPolyhedronBuilder;
import Polyhedron.Model;
import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.ModelRender;
import editor.common.Renderer;
import editor.common.SimpleDrawer;
import editor.common.ToolsGLJPanel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Vykreslovani platno pro editor konvexnich objektu
 * @author ggrrin_
 */
public class ConvexGLJPanel extends ToolsGLJPanel {

    ModelRender render;
    DrawPlane plane;

    ArrayList<Vector3> workingPoints = new ArrayList<>();
    ConvexModel model;
    ConvexPolyhedronBuilder builder;

    SelectorTool selector;
    PointTool pointer;

    SimpleDrawer sd;

    /**
     * inicializuje platno
     */
    public ConvexGLJPanel() {
        modelMat = new Matrix4();
        render = new ModelRender();
        reBuild();

        plane = new DrawPlane(this);

        selector = new SelectorTool(this);
        pointer = new PointTool(this);

        sd = new SimpleDrawer(new Vector3(1, 0, 0), 2, 0.5f);
        render.registerComponent(sd);

        Start();
    }

    /**
     * Vybere natroj pro vyber bodu
     * @return dany nastroj
     */
    public SelectorTool getSelector() {
        return selector;
    }

    /**
     * odstrani vybrane body z objektu
     */
    public void RemoveSelected() {
        workingPoints.clear();

        List<Vector3> vb = render.getCurentModel().getVertexBuffer();
        for (int i = 0; i < vb.size(); i++) {
            if (!selector.getPoints().contains(i)) {
                workingPoints.add(vb.get(i));
            }
        }
        selector.reset();
        reBuild();
        super.save = true;
    }

    @Override
    public void open(File path) throws IOException {
        reset();

        ArrayList<Vector3> v = Model.readInputFile(path.getPath());

        if (v != null) {
            workingPoints = v;
        }

        reBuild();
        super.save = false;
    }

    @Override
    public void save(File path) throws IOException {
        builder.model.export(path.getPath());
        super.save = false;
    }

    /**
     * Nastavi stav komponenty do pocatecniho stavy
     */
    public void reset() {
        workingPoints.clear();
        reBuild();
    }

    /**
     * Vytvori z danych bodu konvexni objekt
     */
    public void reBuild() {
        builder = new ConvexPolyhedronBuilder(workingPoints, true);

        boolean isBuilt = false;

        isBuilt = builder.Calculate();

        ConvexModel m;
        if (isBuilt) {
            m = new ConvexModel(builder.model, modelMat);
        } else {
            m = new ConvexModel(new PointsModel(workingPoints), modelMat);
        }

        render.setCurrentModel(m);

    }

    @Override
    public Renderer getRenderer() {
        return render;
    }

    /**
     * vybere vyberovy nastorj
     */
    public void selectSelectorTool() {
        selectTool(selector);
    }

    /**
     * vybere kreslici natroj
     */
    public void selectPointTool() {
        selectTool(pointer);
    }

}
