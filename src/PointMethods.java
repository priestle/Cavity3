import java.util.ArrayList;

public class PointMethods {

    private PointMethods() {}; // Prevents instantiation of static class

    // ***************************************************************************** //
    //
    public static ArrayList<Point3DPlus> atomListToPointList(ArrayList<PDBAtom> atomList) {
        ArrayList<Point3DPlus> pointsList = new ArrayList<>();

        for (int i = 0; i < atomList.size(); i++) {
            Point3DPlus aPoint = new Point3DPlus(atomList.get(i));
            pointsList.add(aPoint);
        }

        return pointsList;
    }

}
