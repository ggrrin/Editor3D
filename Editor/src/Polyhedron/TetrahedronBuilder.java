package Polyhedron;

import java.util.ArrayList;

/**
 * Trida pro vytvoreni ctyrstenu
 * @author ggrrin_
 */
class TetrahedronBuilder {

    private Model model;

    /**
     * inicializuje tirdu danym modelem
     * @param m 
     */
    public TetrahedronBuilder(Model m) {
        model = m;
    }

    /**
     * sestavy ctyrsten z daneho modelu
     * @return index od ktereho zacinaji nepouzite vrcholy v modelu
     */
    public int Build() {
        if (model.Vertices.size() < 4) {
            throw new UnsupportedOperationException("In source set is not enough vertecis to build even tetrahedron.");
        }

        OrientatedPlane basement;

        model.Planes.add(basement = GetBasement());
        int top = GetTop(basement);

        boolean invertPlane = basement.intersect(model.Vertices.get(top));
        if (invertPlane)//musime otocit normalu			
        {
            basement.InvertPlane();
        }

        //Add remaining edges 
        Edge e1, e2, e3;
        model.Edges.add(e1 = new Edge(model.Vertices, invertPlane ? 0 : 0, top));
        model.Edges.add(e2 = new Edge(model.Vertices, invertPlane ? 2 : 1, top));
        model.Edges.add(e3 = new Edge(model.Vertices, invertPlane ? 1 : 2, top));

        OrientatedPlane o1, o2, o3;
        model.Planes.add(o1 = new OrientatedPlane(basement.getE1(), e1, e2, true));
        model.Planes.add(o2 = new OrientatedPlane(basement.getE2(), e2, e3, true));
        model.Planes.add(o3 = new OrientatedPlane(basement.getE3(), e3, e1, true));

        //o1.Color = Color.Red;
        //o2.Color = Color.Green;
        //o3.Color = Color.Blue;
        return 4;
    }

    /**
     * Nalezne vrchol takovy ze nelezi v zakladne
     * @param basement zakladna
     * @return index vrocholu
     */
    private int GetTop(OrientatedPlane basement) {
        int index = new Extension<Vector3>().FindIndex(3,
                (Vector3 x) -> !basement.liesIn((Vector3) x), model.Vertices);

        if (index == -1) {
            throw new UnsupportedOperationException("In source set is not enough vertecis to build even tetrahedron.");
        }

        Vector3 v = model.Vertices.get(index);
        model.Vertices.remove(index);
        model.Vertices.add(3, v);

        return 3;
    }

    /**
     * Nalezne zakladnu cytrstenu
     * @return zakladna ctyrstenu
     */
    private OrientatedPlane GetBasement() {
        ArrayList<Vector3> tetrahedronVertecies = new ArrayList<>(3);
        ArrayList<Integer> tetrahedronIndices = new ArrayList<>(3);

        int i = 0;
        while (tetrahedronVertecies.size() < 3) {
            if (i >= model.Vertices.size()) {
                throw new UnsupportedOperationException("In source set is not enough different vertecis to build even tetrahedron.");
            }

            Vector3 cur = model.Vertices.get(i);
            if (!tetrahedronVertecies.contains(cur)) {
                tetrahedronVertecies.add(cur);
                tetrahedronIndices.add(i);
            }
            i++;
        }

        tetrahedronIndices.sort(null);
        //Add base vertices at the begining
        int q = 0;
        for (int id : tetrahedronIndices) {
            model.Vertices.remove(id - q++);
        }
        model.Vertices.addAll(0, tetrahedronVertecies);

        Edge e1, e2, e3;
        model.Edges.add(e1 = new Edge(model.Vertices, 0, 1));
        model.Edges.add(e2 = new Edge(model.Vertices, 1, 2));
        model.Edges.add(e3 = new Edge(model.Vertices, 2, 0));

        OrientatedPlane m = new OrientatedPlane(e1, e2, e3, true);
        //m.Color = Color.Black;
        return m;
    }
}
