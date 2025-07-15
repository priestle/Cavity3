import java.util.ArrayList;

public class PointMethods {

    private PointMethods() {} // Prevents instantiation of static class

    // ***************************************************************************** //
    public static ArrayList<Point3DPlus> atomListToPointList(ArrayList<PDBAtom> atomList) {
        ArrayList<Point3DPlus> pointsList = new ArrayList<>();

        for (int i = 0; i < atomList.size(); i++) {
            Point3DPlus aPoint = new Point3DPlus(atomList.get(i));
            pointsList.add(aPoint);
        }

        return pointsList;
    }

    // **************************************************************************** //
    // Basic version using unscaled VdW radius
    public static boolean sphereOccludesRay(Point3DPlus rayOrigin, Point3DPlus rayEnd, PDBAtom atom) {
        return sphereOccludesRay(rayOrigin, rayEnd, atom, 1.0);  // default scale = 1.0
    }

    // **************************************************************************** //
    // Version with VdW radius scaling (e.g. 0.95)
    public static boolean sphereOccludesRay(Point3DPlus rayOrigin, Point3DPlus rayEnd, PDBAtom atom, double vdwScale) {

        double px = rayOrigin.getX();
        double py = rayOrigin.getY();
        double pz = rayOrigin.getZ();

        double qx = rayEnd.getX();
        double qy = rayEnd.getY();
        double qz = rayEnd.getZ();

        double rx = atom.getX();
        double ry = atom.getY();
        double rz = atom.getZ();

        double R = atom.getVdWradius() * vdwScale;
        if (R < 0.01) {
            System.out.println("ASSERT: VdW radius too small in occlusion calculation"); // DEBUG
            return false;
        }

        // Direction vector v = Q - P
        double vx = qx - px;
        double vy = qy - py;
        double vz = qz - pz;

        // Vector w = R - P
        double wx = rx - px;
        double wy = ry - py;
        double wz = rz - pz;

        // Compute t = (w·v)/(v·v)
        double vDotV = vx * vx + vy * vy + vz * vz;
        double wDotV = wx * vx + wy * vy + wz * vz;
        double t = wDotV / vDotV;

        // Closest point on segment = P + t * v
        double cx = px + t * vx;
        double cy = py + t * vy;
        double cz = pz + t * vz;

        // Distance from sphere center to closest point
        double dx = cx - rx;
        double dy = cy - ry;
        double dz = cz - rz;
        double distSq = dx * dx + dy * dy + dz * dz;

        // Return true if inside scaled radius
        return distSq <= R * R;
    }
}
