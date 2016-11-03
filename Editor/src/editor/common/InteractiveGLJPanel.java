/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Collection;

/**
 * Trida poskytujici nektere vhodne funkce ktere neobsahuje samotny GLPanel
 * @author ggrrin_
 */
public abstract class InteractiveGLJPanel extends GLJPanel implements MouseMotionListener, MouseListener, MouseWheelListener {

    private FPSAnimator animator;

    protected InteractiveGLJPanel() {
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    /**
     * zapne vykreslovaci smycku
     */
    public void Start() {
        animator = new FPSAnimator(this, 25, true);
        addGLEventListener(getRenderer());
        animator.start();
    }
    
    /**
     * zastavi vykreslovaci smycku
     */
    public void Stop()
    {
        animator.stop();
        removeGLEventListener(getRenderer());
    }

    /**
     * Vrati dany renderer
     * @return  renderer
     */
    public abstract Renderer getRenderer();

    /**
     * Vrati index do vertexBufferu modelVertices na pozici interagujci s kurzorem
     * mysi na pozicich xCanvas, yCanvas, s citlivosti sensitivity
     * @param xCanvas pozice mysi
     * @param yCanvas pozice mysi
     * @param sensitivity citlivost vyberu
     * @param modelVertices body ktere projdou testem
     * @return index kolidujciho bodu z modelVertices nebo -1 pokud nenalezena kolize
     */
    public int getMatchedVertex(int xCanvas, int yCanvas, float sensitivity, Collection<Vector3> modelVertices) {
//        int result = -1;
//        float zDepth = 0;
//        int i = 0;
//        for (Vector3 v : modelVertices) {
//            
//            Vector3 vWorld =  mulMatrix(v,getRenderer().getCurentModel().getModelMatrix());
//            
//            Vector3 screen = worldToScreen(vWorld);
//
//            if (Math.abs(screen.X - xCanvas) <= sensitivity && Math.abs(screen.Y - yCanvas) <= sensitivity) {
//                if (result == -1 || zDepth < screen.Z) {
//                    result = i;
//                    zDepth = screen.Z;
//                }
//            }
//
//            i++;
//        }
//
//        return result;
        
        Vector3 camPos = getRenderer().getCameraPoint();
        
        
        int result = -1;
        float lastDistance = Float.MIN_VALUE;
        int i = 0;
        for (Vector3 v : modelVertices) {
            
            Vector3 vWorld =  mulMatrix(v,getRenderer().getCurentModel().getModelMatrix());
            
            Vector3 screen = worldToScreen(vWorld);

            if (Math.abs(screen.X - xCanvas) <= sensitivity && Math.abs(screen.Y - yCanvas) <= sensitivity) {
                
                float  curCamDistance = camPos.sub(vWorld).length();
                
                if (result == -1 || lastDistance > curCamDistance) {
                    result = i;
                    lastDistance = curCamDistance;
                }
            }

            i++;
        }

        return result;
        
        
    }

    /**
     * Vrati novou matici rovna inversu matice in
     * @param in matice k invertovani
     * @return nova invertovana matice
     */
    public static Matrix4 getInverse(Matrix4 in) {
        float[] source = in.getMatrix();

        Matrix4 inverse = new Matrix4();
        float[] resf = inverse.getMatrix();

        for (int j = 0; j < source.length; j++) {
            resf[j] = source[j];
        }

        if (!inverse.invert()) {
            throw new Error();
        }

        return inverse;
    }

    /**
     * Vynasobi matici dannym vektorem v a provede transforamci z homogenich souradnic
     * @param v vektor k vynasobeni
     * @param m matice k vynasobeni
     * @return vysledny vektor
     */
    public static Vector3 mulMatrix(Vector3 v, Matrix4 m) {
        float[] res = {0, 0, 0, 0};
        float[] in = {v.X, v.Y, v.Z, 1};

        m.multVec(in, res);

        float w = res[3];

        return new Vector3(res[0] / w, res[1] / w, res[2] / w);
    }

    /**
     * Vrati paprsek ustici z pozice mysi n
     * @param x x pozice mysi
     * @param y y pozice mysi
     * @return dany paprsek
     */
    public  Ray getScreenRay(float x, float y)
    {
         Matrix4 inv = getInverse(getRenderer().getMvp());

        float rx = 2 * x / (float) getWidth() - 1;
        float ry = -2 * y / (float) getHeight() + 1;

        Vector3 point = mulMatrix(new Vector3(rx, ry, getRenderer().nearPlane), inv);
        Vector3 point1 = mulMatrix(new Vector3(rx, ry, getRenderer().farPlane), inv);
        
        return new Ray(point, point1);
    }
    
    /**
     * Prevede souradnice na panelu do svetovych souradich na rovine plane
     * @param x x poizce mysi
     * @param y y pozice mysi
     * @param plane rovina s kterou kolidovat praprsek
     * @return vysledna souradnice
     */
    public Vector3 screenToWorld(int x, int y, OrientatedPlane plane) {

        return getScreenRay(x,y).intersects(plane);
    }

    /**
     * Prevede svetove souradnice do  souradnice na panelu
     * @param v vektor ve svetovych souradnicyh
     * @return vector v panlovych souradnicich
     */
    public Vector3 worldToScreen(Vector3 v) {
        float[] res = {0, 0, 0, 0};
        float[] in = {v.X, v.Y, v.Z, 1};

        //render.mvp.transpose();
        getRenderer().getMvp().multVec(in, res);
        //render.mvp.transpose();

        return new Vector3(
                ((res[0] / res[3] + 1) / 2.0f) * getWidth(),
                ((1 - res[1] / res[3]) / 2.0f) * getHeight(),
                res[2] / res[3]);
    }

}
