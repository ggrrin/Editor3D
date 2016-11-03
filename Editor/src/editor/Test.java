/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.Drawable;
import editor.common.ModelRender;
import editor.common.Renderer;
import editor.common.SimpleDrawer;
import editor.common.ToolsGLJPanel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ggrrin_
 */
public class Test extends ToolsGLJPanel {

    ModelRender render;

    Drawable m;

    Integer[] ib = new Integer[]{0, 1, 2};
    Vector3[] vb = new Vector3[]{new Vector3(-4, -4, -4), new Vector3(-1, -1, -1), new Vector3(-3, -2, 0)};

    SimpleDrawer sd;
    
    public Test() {
        m = new Drawable() {

            @Override
            public List<Integer> getIndexBuffer() {
                return Arrays.asList(ib);
            }

            @Override
            public List<Vector3> getVertexBuffer() {
                return Arrays.asList(vb);
            }

            @Override
            public Vector3 getColor() {
                return new Vector3(1, 0, 0);
            }

            @Override
            public Matrix4 getModelMatrix() {
                return modelMat;
            }
        };

        render = new ModelRender();
        render.setCurrentModel(m);
        modelMat = new Matrix4();

        sd = new SimpleDrawer(new Vector3(0,1,0), 3f, 20f);
        render.registerComponent(sd);
        sd.addPoint(new Vector3(0, 0, 0));
        
        render.invokeLater(g -> sd.updateBuffers(g));
        
        Vector3 c = getCenteroid(m);

        modelMat.translate(-c.X, -c.Y, -c.Z);

        Start();
    }

    private Vector3 getCenteroid(Drawable d) {
        List<Vector3> v = d.getVertexBuffer();
        return v.get(0).sub(v.get(1).sub(v.get(2))).ms(1f / 3f);
    }

    public void open(File path) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(File path) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Renderer getRenderer() {
        return render;
    }

}
