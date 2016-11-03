/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.heighMapEditor;

import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.bodyEditor.BodyModel;
import editor.common.ModelRender;
import editor.common.Renderer;
import editor.common.ToolsGLJPanel;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

/**
 * Panel pro vykreslovani a tvorbu mapy
 * @author ggrrin_
 */
public class MapGLJPanel extends ToolsGLJPanel {

    private final Vector3 selecteColor = new Vector3(1, 0.9f, 0);
    private final Vector3 wireColor = new Vector3(0, 0, 0);
    private final int size = 15;

    private ModelRender render;

    HeightMapModel map;

    private BrushTool brush;
    private ObjectTool pos;
    
    BodyModel selectedModel;
    
    /**
     * rozmery soucasne mapy
     */
    public Point dimensions = new Point(size, size);

    /**
     * Inicializuje panel
     */
    public MapGLJPanel() {
        render = new ModelRender();
        
        brush = new BrushTool(this);
        pos = new ObjectTool(this);
        init(15, 15);
        Start();
    }

    /**
     * Inicializuje vyskovou mapu a trasformuje ji tak aby se vesla do pohledu
     * @param width sirak mapy
     * @param depth hloubka mapy
     */
    private void init(int width, int depth) {
        int max = Math.max(width, depth);
        float scale = size / (float)max;

        modelMat = new Matrix4();
        modelMat.scale(scale, scale, scale);
        map = new HeightMapModel(width, depth, modelMat);
        render.setCurrentModel(map);
        pos.reset();
    }

    /**
     * Vybere nastroj pro transformaci terenu
     * @return danny nastroj
     */
    BrushTool selectBrush() {
        selectTool(brush);
        return brush;
    }

    
    /**
     * Vybere nastroj pro pozicovani objektu
     * @return danny nastroj
     */
    ObjectTool selectObjTool() {
        selectTool(pos);
        return pos;
    }

    @Override
    public void open(File path) throws IOException {
        map = HeightMapModel.load(path);
        map.setModelMatrix(modelMat);
        //modelMat.loadIdentity();
        
        
        int max = Math.max(map.width, map.depth);
        float scale = size / (float)max;

       
        modelMat.scale(scale, scale, scale);
        render.setCurrentModel(map);
        pos.reset();
        super.save = false;
    }

    @Override
    public void reset() {
        init(dimensions.x, dimensions.y);
        super.save = true;
    }

    @Override
    public void save(File path) throws IOException {
        map.export(path);
        super.save = false;
    }

    @Override
    public Renderer getRenderer() {
        return render;
    }

    /**
     * Nacte objekt ze souboru a prida objekt do mapy 
     * @param file soubor s objektem
     * @return reference na nacteny objekt
     * @throws IOException chyba pri cteni souboru
     */
    Object addBody(File file) throws IOException {
        BodyModel m = BodyModel.load(file);

        Vector3 c = m.getCenteroid();

        Vector3 d = pos.pointer.sub(c);

        m.getModelMatrix().translate(d.X, d.Y, d.Z);//WTF
        map.addElement(m);
        
        super.save = true;
        return m;
    }

    /**
     * Odstrani objekt z mapy o
     * @param o objekt k odstraneni
     */
    void removeBody(Object o) {
        map.removeElement(o);

        if (selectedModel == o) {
            selectedModel = null;            
        }
        
        super.save = true;
    }

    /**
     * vybere dany objekt o 
     * @param o objekt k vybrani
     */
    void selectBody(Object o) {
        if (selectedModel != null) {
            selectedModel.setWireColor(wireColor);
        }

        if (o != null) {
            BodyModel m = (BodyModel) o;
            selectedModel = m;
            selectedModel.setWireColor(selecteColor);
            //Vector3 centr = InteractiveGLJPanel.mulMatrix(m.getCenteriod(), modelMat);
        }
        else
        {
            selectedModel = null;
        }
    }

}
