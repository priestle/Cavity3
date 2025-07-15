import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class TriangleMethods {

    private TriangleMethods() {} // Prevents instantiation of static class

    // ============================================================================
    public static ArrayList<Triangle> makeTriangles(ArrayList<Point3DPlus> nucleii, String icosahedronMapFileName) {
        ArrayList<Triangle> unitIcosahedron = new ArrayList<>();

        try (Scanner inputF = new Scanner(Paths.get(icosahedronMapFileName))) {
            while (inputF.hasNextLine()) {
                String row = inputF.nextLine().trim();
                String[] parts = row.split("\\s+");
                try {
                    double Ax = Double.parseDouble(parts[0]);
                    double Ay = Double.parseDouble(parts[1]);
                    double Az = Double.parseDouble(parts[2]);
                    double Bx = Double.parseDouble(parts[3]);
                    double By = Double.parseDouble(parts[4]);
                    double Bz = Double.parseDouble(parts[5]);
                    double Cx = Double.parseDouble(parts[6]);
                    double Cy = Double.parseDouble(parts[7]);
                    double Cz = Double.parseDouble(parts[8]);
                    double charge = 0;
                    String element = "C";
                    Triangle aTriangle = new Triangle(Ax, Ay, Az, Bx, By, Bz, Cx, Cy, Cz, charge, element);
                    unitIcosahedron.add(aTriangle);
                } catch (Exception e) {
                    System.out.println("FAIL : **" + row + "**");
                    for (String p : parts) {
                        System.out.print("**" + p + "** ");
                    }
                    System.out.println();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Triangle> triangleList = new ArrayList<>();
        for (Point3DPlus nuc : nucleii) {
            double r = nuc.getRadius();
            String e = nuc.getElement();
            double c = nuc.getCharge();

            double Ox = nuc.getX();
            double Oy = nuc.getY();
            double Oz = nuc.getZ();

            for (Triangle t : unitIcosahedron) {
                double Ax = t.getAx() * r + Ox;
                double Ay = t.getAy() * r + Oy;
                double Az = t.getAz() * r + Oz;
                double Bx = t.getBx() * r + Ox;
                double By = t.getBy() * r + Oy;
                double Bz = t.getBz() * r + Oz;
                double Cx = t.getCx() * r + Ox;
                double Cy = t.getCy() * r + Oy;
                double Cz = t.getCz() * r + Oz;

                Triangle triangleToAdd = new Triangle(Ax, Ay, Az, Bx, By, Bz, Cx, Cy, Cz, c, e);
                triangleList.add(triangleToAdd);
            }
        }

        return triangleList;
    }

    // ============================================================================
    public static void makeTCLFileOfTriangles(ArrayList<Triangle> triangles, String tclFileName) {
        double min = 1e9;
        double max = -1e9;

        for (Triangle t : triangles) {
            double charge = t.getCharge();
            if (charge > max) max = charge;
            if (charge < min) min = charge;
        }
        System.out.println("Minimum charge = " + min);
        System.out.println("Maximum charge = " + max);

        ArrayList<String> tclLines = new ArrayList<>();
        tclLines.add("mol new");
        tclLines.add("draw material Opaque");

        for (int i = -256; i < 256; i++) {
            RGB rgb = ColorMethods.valueToRGB(i, 256, -256);
            tclLines.add("color change rgb " + (i + 296) + " " + rgb.getR() + " " + rgb.getG() + " " + rgb.getB());
        }

        for (Triangle T : triangles) {
            int color = 296;
            double charge = T.getCharge();
            if (charge > 0) {
                color = (int) Math.round(295 + 256 * charge / max);
            } else if (charge < 0) {
                color = (int) Math.round(-256.0 * charge / min + 296);
            }

            tclLines.add("draw color " + color);
            tclLines.add("draw triangle {" + T.getAx() + " " + T.getAy() + " " + T.getAz() + "} {" +
                    T.getBx() + " " + T.getBy() + " " + T.getBz() + "} {" +
                    T.getCx() + " " + T.getCy() + " " + T.getCz() + "}");
        }

        try (FileWriter outputF = new FileWriter(tclFileName)) {
            for (String line : tclLines) {
                outputF.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================================
    public static ArrayList<Triangle> selectVisibleToLigand(
            ArrayList<PDBAtom> ligandAtoms,
            ArrayList<Triangle> cavityTriangles,
            ArrayList<PDBAtom> cavityAtoms) {

        ArrayList<Triangle> goodTriangles = new ArrayList<>();

        for (int i = 0; i < cavityTriangles.size(); i++) {

            // We keep the triangle if any ray from a ligand nucleus to that triangle does not pass through any other
            // triangle. We don't self-test the triangle in question

            if (i % 1000 == 0) { System.out.println(i + " / " + cavityTriangles.size()); }

            Triangle test = cavityTriangles.get(i);

            boolean foundGoodRay = false;

            double centroidX = (test.getAx() + test.getBx() + test.getCx()) / 3;
            double centroidY = (test.getAy() + test.getBy() + test.getCy()) / 3;
            double centroidZ = (test.getAz() + test.getBz() + test.getCz()) / 3;

            Point3DPlus startRay = new Point3DPlus(centroidX, centroidY, centroidZ, 0.00, 0.00, "X");
outer:
            for (int j = 0; j < ligandAtoms.size(); j++) {

                double ligX = ligandAtoms.get(j).getX();
                double ligY = ligandAtoms.get(j).getY();
                double ligZ = ligandAtoms.get(j).getZ();
                Point3DPlus endRay = new Point3DPlus(ligX, ligY, ligZ, 0.00, 0.00, "X");

                for (int k = 0; k < cavityTriangles.size(); k++) {
                    if ( i != k) {  // we're not going to self test the same triangle

                        foundGoodRay = triangleIntersectsRay(startRay, endRay, cavityTriangles.get(k));

                        if (foundGoodRay) {
                            goodTriangles.add(test);
                            break outer;
                        }
                    }
                }
            }
        }
        return goodTriangles;
    }

    /**
     * Checks whether the segment from rayOrigin to rayEnd intersects the given triangle (triangle ABC)
     * using the Möller–Trumbore algorithm. Returns true if intersection occurs strictly between origin and end.
     */
    public static boolean triangleIntersectsRay(Point3DPlus rayOrigin, Point3DPlus rayEnd, Triangle tri) {
        final double EPS = 1e-8;

        // Direction vector D = rayEnd - rayOrigin
        double dx = rayEnd.getX() - rayOrigin.getX();
        double dy = rayEnd.getY() - rayOrigin.getY();
        double dz = rayEnd.getZ() - rayOrigin.getZ();

        // Triangle vertices
        double v0x = tri.getAx(), v0y = tri.getAy(), v0z = tri.getAz();
        double v1x = tri.getBx(), v1y = tri.getBy(), v1z = tri.getBz();
        double v2x = tri.getCx(), v2y = tri.getCy(), v2z = tri.getCz();

        // Edges of the triangle
        double e1x = v1x - v0x, e1y = v1y - v0y, e1z = v1z - v0z;
        double e2x = v2x - v0x, e2y = v2y - v0y, e2z = v2z - v0z;

        // P-vector = D × e2
        double px = dy * e2z - dz * e2y;
        double py = dz * e2x - dx * e2z;
        double pz = dx * e2y - dy * e2x;

        // Determinant
        double det = e1x * px + e1y * py + e1z * pz;
        if (Math.abs(det) < EPS) return false; // Parallel or nearly parallel

        double invDet = 1.0 / det;

        // Vector from v0 to ray origin
        double t0x = rayOrigin.getX() - v0x;
        double t0y = rayOrigin.getY() - v0y;
        double t0z = rayOrigin.getZ() - v0z;

        // Calculate u parameter
        double u = (t0x * px + t0y * py + t0z * pz) * invDet;
        if (u < 0.0 || u > 1.0) return false;

        // Q-vector = t0 × e1
        double qx = t0y * e1z - t0z * e1y;
        double qy = t0z * e1x - t0x * e1z;
        double qz = t0x * e1y - t0y * e1x;

        // Calculate v parameter
        double v = (dx * qx + dy * qy + dz * qz) * invDet;
        if (v < 0.0 || u + v > 1.0) return false;

        // Calculate t (intersection point along the ray)
        double t = (e2x * qx + e2y * qy + e2z * qz) * invDet;

        // Only count intersections strictly within the segment (not at endpoints)
        return (t > EPS && t < 1.0 - EPS);
    }


    // ============================================================================
    public static ArrayList<Triangle> recalculateCharge(ArrayList<Triangle> triangles, ArrayList<PDBAtom> atoms) {
        ArrayList<Triangle> chargedTriangles = new ArrayList<>();

        for (Triangle T : triangles) {
            double potential = 0.0;

            double cx = (T.getAx() + T.getBx() + T.getCx()) / 3;
            double cy = (T.getAy() + T.getBy() + T.getCy()) / 3;
            double cz = (T.getAz() + T.getBz() + T.getCz()) / 3;

            for (PDBAtom atom : atoms) {
                double dx = cx - atom.getX();
                double dy = cy - atom.getY();
                double dz = cz - atom.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                potential += (1.0 / (4 * Math.PI)) * (atom.getCharge() / dist);
            }

            T.setCharge(potential);
            chargedTriangles.add(T);
        }

        return chargedTriangles;
    }
}
