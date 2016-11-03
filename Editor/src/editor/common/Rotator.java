/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import java.awt.Point;

/**
 * Trida pro rotovani a celeho objektu panelu. A posunu kamery ve svislem smeru
 * @author ggrrin_
 */
public class Rotator {


    InteractiveGLJPanel myPanel;


    /**
     * Napojeni na dany panel
     * @param k odpovidajici panel
     */
    public Rotator(InteractiveGLJPanel k) {

        myPanel = k;

    }

    private Point prev = null;
    private float moveFactor = 0.03f;

    /**
     * Posune kameru ve svislem smeru vzavislosti na posunu mysi
     * @param p sourdnice pozice mysi
     */
    public void getCameraMove(Point p) {

        if (prev != null)//zacatek
        {
            //camera
            Vector3 v = myPanel.getRenderer().getCameraPoint();

            v.Y += moveFactor * (p.y - prev.y);
            myPanel.getRenderer().setCameraPoint(v);
        }

        prev = p;
    }

    /**
     * ukonci posun
     */
    public void stopMove() {
        prev = null;
    }

    private float rotFactor = 0.01f;

    private float[] matrixCopy = null;
    private Point origin = null;

    
    /**
     * Provede hlubokou kopii matice
     * @param m matice
     * @return hluboka kopie matice m
     */
    public static Matrix4 clone(Matrix4 m) {
        Matrix4 n = new Matrix4();

        float[] mf = m.getMatrix();
        float[] nf = n.getMatrix();

        for (int i = 0; i < 16; i++) {
            nf[i] = mf[i];
        }

        return n;
    }

    /**
     * Provate  rotaci modelu dle pohybu mysi
     * @param p pozice mysi
     * @param mat matice ktera bude upravena
     */
    public void getCurentRotation(Point p, Matrix4 mat) {
        if (origin != null) {

            Matrix4 clo = clone(mat);

            mat.loadIdentity();

            Vector3 rotationVec = new Vector3(0, 1, 0);
            float radians = rotFactor * (p.x - origin.x);
            mat.rotate(radians, rotationVec.X, rotationVec.Y, rotationVec.Z);

            mat.multMatrix(clo);
        }

        origin = p;

    }

    /**
     * Zruseni rotovani
     */
    public void stopRotate() {
        origin = null;
    }

}
