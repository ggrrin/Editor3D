/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polyhedron;

/**
 * Trida reprezentujici trojslozkovy vektor.
 * Poskytuje zakladni operace s vektory.
 * @author ggrrin_
 */
public class Vector3 implements Comparable<Vector3> {

    public float X;
    public float Y;
    public float Z;

    /**
     * Inicilizuje vector po slozkach
     * @param x_ slozka x
     * @param y_ slozka y
     * @param z_ slozka z
     */
    public Vector3(float x_, float y_, float z_) {
        X = x_;
        Y = y_;
        Z = z_;
    }

    /**      
     * Provede hlubokou kopii vektoru.
     * @return kopie vektoru
     */
    public Vector3 Clone() {
        return new Vector3(X, Y, Z);
    }

    /**
     * Provede vektorovy soucin.
     * @param x prvni vektor
     * @param y druhy vektor
     * @return vektor kolmy na x a y
     */
    public static Vector3 Cross(Vector3 x, Vector3 y) {
        return new Vector3(
                x.Y * y.Z - y.Y * x.Z,
                x.Z * y.X - y.Z * x.X,
                x.X * y.Y - y.X * x.Y
        );
    }

    /**
     * Provede rozdil vektoru.
     * @param v odcitany vektor
     * @return novy vektor reprezentujici rozdil
     */
    public Vector3 sub(Vector3 v) {
        return new Vector3(X - v.X, Y - v.Y, Z - v.Z);
    }

    /**
     * Provede soucet vektoru.
     * @param v pricitany vektor
     * @return novy vektor reprezentujici soucet
     */
    public Vector3 plus(Vector3 v) {
        return new Vector3(X + v.X, Y + v.Y, Z + v.Z);
    }

    /**
     * Otoci vektor
     * @return novy otoceny vektor
     */
    public Vector3 invert() {
        X *= -1;
        Y *= -1;
        Z *= -1;
        return new Vector3(X, Y, Z);
    }

    /**
     * Provede sklarni soucin.
     * @param v1 prvni vektor
     * @param v2 druhy vektor
     * @return skalarni soucin
     */
    public static float dot(Vector3 v1, Vector3 v2) {
        return v1.X * v2.X + v1.Y * v2.Y + v1.Z * v2.Z;
    }

    /**
     * Vytvori novy vektor rovny normalizovanemu v
     * @param v vektor del ktereho se provede normalizace
     * @return novy normalizovany vektor
     */
    public static Vector3 normalize(Vector3 v) {
        Vector3 res = new Vector3(v.X, v.Y, v.Z);
        float len = v.length();
        res.X /= len;
        res.Y /= len;
        res.Z /= len;
        return res;
    }

    /**
     * Delka vektoru
     * @return delka vektoru
     */
    public float length() {
        return (float) Math.sqrt((double) (X * X + Y * Y + Z * Z));
    }


    /**
     * Definuje usporadani na vektorech, postupne
     * dle prvni, druhe a treti slozky.
     * @param y porovnavany vektor
     * @return vysledek porovnani dany usporadanim
     */
    @Override
    public int compareTo(Vector3 y) {

        int r0 = ((Float) X).compareTo(y.X);
        if (r0 != 0) {
            return r0;
        }

        int r1 = ((Float) Y).compareTo(y.Y);
        if (r1 != 0) {
            return r1;
        }

        int r2 = ((Float) Z).compareTo(y.Z);

        return r2;

    }

    /**
     * Textova reprezentace vektoru
     * @return text
     */
    @Override
    public String toString() {
        return "{X:" + X + " Y:" + Y + " Z:" + Z + "}";
    }

    /**
     * Provede vynasobeni skalarem
     * @param k skalar pro vynasobeni
     * @return novy vektor odpovidajci k vynasobene sklarem
     */
    public Vector3 ms(float k) {
        return new Vector3(k * X, k * Y, k * Z);
    }

    
    /**
     * Rovnost vektoru poslozkach
     * @param v vektor k porovnani
     * @return zda jsou vektory rovny
     */
    @Override
    public boolean equals(Object v) {
        if (v instanceof Vector3) {

            Vector3 q = (Vector3) v;

            return q.X == X && q.Y == Y && q.Z == Z;
        } else {
            return false;
        }

    }
}
