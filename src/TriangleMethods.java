import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        // Step 1–2: Extract and sort charges
        List<Double> charges = triangles.stream()
                .map(Triangle::getCharge)
                .sorted()
                .collect(Collectors.toList());

        int n = charges.size();
        if (n < 10) {
            System.err.println("Too few triangles for percentile clamping — using raw min/max");
        }

        // Step 3: Get clamped percentile range
        double min = charges.get((int) (0.01 * n));
        double max = charges.get((int) (0.99 * n));

        System.out.println("Clamped 1% min = " + min);
        System.out.println("Clamped 99% max = " + max);

        ArrayList<String> tclLines = new ArrayList<>();
        tclLines.add("mol new");
        tclLines.add("draw material Opaque");

        for (int i = -256; i < 257; i++) {
            RGB rgb = ColorMethods.valueToRGB(i, 256, -256);
            tclLines.add("color change rgb " + (i + 296) + " " + rgb.getR() + " " + rgb.getG() + " " + rgb.getB());
        }

        for (Triangle T : triangles) {
            double charge = T.getCharge();

            // Step 4: Clamp
            charge = Math.max(min, Math.min(max, charge));

            // Step 5: Map to color scale
            int color = 296;
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

    /* This works but the contrast is shite
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
        tclLines.add("draw material Transparent");

        for (int i = -256; i < 257; i++) {
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

     */

    // ============================================================================
    public static ArrayList<Triangle> selectVisibleToLigand(
            ArrayList<PDBAtom> ligandAtoms,
            ArrayList<Triangle> cavityTriangles) {

        ArrayList<Triangle> goodTriangles = new ArrayList<>();

        // What makes a good triangle? There are no cavity atoms between it and at least one ligand atom
        for (int i = 0 ; i < cavityTriangles.size(); i++) {
            cavityTriangles.get(i).setElement("N");
        }

        for (int i = 0; i < ligandAtoms.size(); i++) {
            for (int j = 0; j < cavityTriangles.size(); j++) {
                System.out.print("\r Checking ligand " + i + " against triangle " + j);
                boolean isBlocked = false;
                for (int k = 0; k < cavityTriangles.size(); k++) {
                    if (k != j) {
                        Point3DPlus rayEnd = getCentroid(cavityTriangles.get(j));
                        Point3DPlus rayStart = new Point3DPlus(ligandAtoms.get(i).getX(),
                                                               ligandAtoms.get(i).getY(),
                                                               ligandAtoms.get(i).getZ(),
                                                               0.00, 0.00, "X");
                        if (rayIntersectsTriangle(rayStart, rayEnd, cavityTriangles.get(k))) {
                            isBlocked = true;
                        }
                    }
                }
                if (!isBlocked) {
                    cavityTriangles.get(j).setElement("Y");
                }
            }
        }
        System.out.println("");

        for (int i = 0; i < cavityTriangles.size(); i++) {
            if (cavityTriangles.get(i).getElement().equals("Y")) {
                goodTriangles.add(cavityTriangles.get(i));
            }
        }


        System.out.println("We started with " + cavityTriangles.size() + " triangles.");
        System.out.println("We ended up with " + goodTriangles.size() + " triangles.");
        return goodTriangles;
    }

    // ============================================================================
    public static ArrayList<Triangle> selectVisibleToLigandParallel(
            ArrayList<PDBAtom> ligandAtoms,
            ArrayList<Triangle> cavityTriangles) {

        // Initialize all triangles as not visible
        cavityTriangles.forEach(triangle -> triangle.setElement("N"));
        AtomicInteger tCount = new AtomicInteger();

        // Parallel processing over triangles
        IntStream.range(0, cavityTriangles.size()).parallel().forEach(j -> {
            Triangle triangle = cavityTriangles.get(j);
            tCount.getAndIncrement();
            System.out.printf("\r%.1f%%", tCount.get() * 100.0 / cavityTriangles.size());
            Point3DPlus rayEnd = getCentroid(triangle);

            // Check each ligand atom
            for (PDBAtom ligandAtom : ligandAtoms) {
                Point3DPlus rayStart = new Point3DPlus(ligandAtom.getX(), ligandAtom.getY(), ligandAtom.getZ(), 0.0, 0.0, "X");
                boolean isBlocked = false;

                // Check intersection with other triangles
                for (int k = 0; k < cavityTriangles.size(); k++) {
                    if (k != j) {
                        Triangle otherTriangle = cavityTriangles.get(k);
                        if (rayIntersectsTriangle(rayStart, rayEnd, otherTriangle)) {
                            isBlocked = true;
                            break;
                        }
                    }
                }

                // If visible from at least one ligand atom, mark as visible and break
                if (!isBlocked) {
                    synchronized (triangle) {
                        triangle.setElement("Y");
                    }
                    break;
                }
            }
        });

        // Collect all visible triangles
        List<Triangle> goodTriangles = cavityTriangles.stream()
                .filter(tri -> tri.getElement().equals("Y"))
                .collect(Collectors.toList());

        System.out.println("We started with " + cavityTriangles.size() + " triangles.");
        System.out.println("We ended up with " + goodTriangles.size() + " triangles.");

        return new ArrayList<>(goodTriangles);
    }

    // ============================================================================
    public static ArrayList<Triangle> selectVisibleToLigandParallelT(
            ArrayList<PDBAtom> ligandAtoms,
            ArrayList<Triangle> cavityTriangles) throws InterruptedException {

        // Initialize all triangles as not visible
        cavityTriangles.forEach(triangle -> triangle.setElement("N"));
        AtomicInteger tCount = new AtomicInteger();

        // Parallel processing over triangles
        IntStream.range(0, cavityTriangles.size()).parallel().forEach(j -> {
            Triangle triangle = cavityTriangles.get(j);
            tCount.getAndIncrement();
            System.out.printf("\r%.1f%%", tCount.get() * 100.0 / cavityTriangles.size());

            Point3DPlus[] vertices = {
                    new Point3DPlus(triangle.getAx(), triangle.getAy(), triangle.getAz(), 0.0, 0.0, "X"),
                    new Point3DPlus(triangle.getBx(), triangle.getBy(), triangle.getBz(), 0.0, 0.0, "X"),
                    new Point3DPlus(triangle.getCx(), triangle.getCy(), triangle.getCz(), 0.0, 0.0, "X")
            };

            boolean isVisible = false;

            for (Point3DPlus rayEnd : vertices) {
                for (PDBAtom ligandAtom : ligandAtoms) {
                    Point3DPlus rayStart = new Point3DPlus(
                            ligandAtom.getX(), ligandAtom.getY(), ligandAtom.getZ(), 0.0, 0.0, "X");

                    boolean isBlocked = false;

                    for (int k = 0; k < cavityTriangles.size(); k++) {
                        if (k != j) {
                            Triangle otherTriangle = cavityTriangles.get(k);
                            if (rayIntersectsTriangle(rayStart, rayEnd, otherTriangle)) {
                                isBlocked = true;
                                break;
                            }
                        }
                    }

                    if (!isBlocked) {
                        synchronized (triangle) {
                            triangle.setElement("Y");
                        }
                        isVisible = true;
                        break; // stop checking more ligand atoms for this vertex
                    }
                }

                if (isVisible) break; // stop checking more vertices
            }
        });

        // Collect all visible triangles
        List<Triangle> goodTriangles = cavityTriangles.stream()
                .filter(tri -> tri.getElement().equals("Y"))
                .collect(Collectors.toList());


        System.out.flush();
        System.out.println("We started with " + cavityTriangles.size() + " triangles.");
        System.out.println("We ended up with " + goodTriangles.size() + " triangles.");

        return new ArrayList<>(goodTriangles);
    }

    // ============================================================================
    private static Point3DPlus getCentroid (Triangle T) {

        double cx = (T.getAx() + T.getBx() + T.getCx()) / 3;
        double cy = (T.getAy() + T.getBy() + T.getCy()) / 3;
        double cz = (T.getAz() + T.getBz() + T.getCz()) / 3;

        Point3DPlus centroid = new Point3DPlus(cx, cy, cz, 0.00, 0.00, "X");

        return centroid;
    }

    // ============================================================================
    public static boolean rayIntersectsTriangle(Point3DPlus S, Point3DPlus E, Triangle T) {
        final double EPSILON = 1e-10;

        double Sx = S.getX();
        double Sy = S.getY();
        double Sz = S.getZ();
        double Ex = E.getX();
        double Ey = E.getY();
        double Ez = E.getZ();
        double Ax = T.getAx();
        double Ay = T.getAy();
        double Az = T.getAz();
        double Bx = T.getBx();
        double By = T.getBy();
        double Bz = T.getBz();
        double Cx = T.getCx();
        double Cy = T.getCy();
        double Cz = T.getCz();

        // Direction vector of the ray
        double dx = Ex - Sx;
        double dy = Ey - Sy;
        double dz = Ez - Sz;

        // Triangle edges
        double[] edge1 = {Bx - Ax, By - Ay, Bz - Az};
        double[] edge2 = {Cx - Ax, Cy - Ay, Cz - Az};

        // Vector h = D x edge2
        double[] h = {
                dy * edge2[2] - dz * edge2[1],
                dz * edge2[0] - dx * edge2[2],
                dx * edge2[1] - dy * edge2[0]
        };

        // a = edge1 · h
        double a = edge1[0]*h[0] + edge1[1]*h[1] + edge1[2]*h[2];
        if (Math.abs(a) < EPSILON) return false; // Ray is parallel to triangle

        double f = 1.0 / a;

        // Vector s = S - A
        double[] s = {Sx - Ax, Sy - Ay, Sz - Az};

        // u = f * (s · h)
        double u = f * (s[0]*h[0] + s[1]*h[1] + s[2]*h[2]);
        if (u < 0.0 || u > 1.0) return false;

        // q = s x edge1
        double[] q = {
                s[1]*edge1[2] - s[2]*edge1[1],
                s[2]*edge1[0] - s[0]*edge1[2],
                s[0]*edge1[1] - s[1]*edge1[0]
        };

        // v = f * (D · q)
        double v = f * (dx*q[0] + dy*q[1] + dz*q[2]);
        if (v < 0.0 || u + v > 1.0) return false;

        // t = f * (edge2 · q)
        double t = f * (edge2[0]*q[0] + edge2[1]*q[1] + edge2[2]*q[2]);

        // Check if the intersection is along the ray segment
        return t >= 0.0 && t <= 1.0;
    }

    // ============================================================================
    public static ArrayList<Triangle> recalculateCharge(ArrayList<Triangle> triangles, ArrayList<PDBAtom> atoms) {

        // Calculate the potential at the centroid of each triangle
        System.out.println("Recalculating potentials...");
        for (int i = 0; i < triangles.size(); i++) {
            double potential = 0.00;
            Point3DPlus centroid = getCentroid(triangles.get(i));
            for (int j = 0; j < atoms.size(); j++) {
                double dx = centroid.getX() - atoms.get(j).getX();
                double dy = centroid.getY() - atoms.get(j).getY();
                double dz = centroid.getZ() - atoms.get(j).getZ();
                double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
                double p = 1 / (4 * Math.PI) * atoms.get(j).getCharge() / distance;
                potential += p;
            }
            triangles.get(i).setCharge(potential);
        }

        return triangles;
    }

    // ============================================================================
    public static void makeTCLFileOfMesh(ArrayList<Triangle> triangles,
                                         String filename,
                                         ArrayList<PDBAtom> atoms) {

        // We're going to make a pdb file, where each triangle vertex is an atom with a charge, and a triangle
        // is drawn with connect records; TER delimiters between all triangles...

        ArrayList<Edge> edges = new ArrayList<>();
        int serial = 0;

        // Generate full connection map
        for (int i = 0; i < triangles.size(); i++) {
            Triangle T = triangles.get(i);
            Vertex v1 = new Vertex(serial, T.getAx(), T.getAy(), T.getAz(), T.getCharge(), false);
            serial++;
            Vertex v2 = new Vertex(serial, T.getBx(), T.getBy(), T.getBz(), T.getCharge(), false);
            serial++;
            Vertex v3 = new Vertex(serial, T.getCx(), T.getCy(), T.getCz(), T.getCharge(), false);
            serial++;
            Edge A = new Edge(v1, v2);
            edges.add(A);
            Edge B = new Edge(v2, v3);
            edges.add(B);
            Edge C = new Edge(v3, v1);
            edges.add(C);
        }

        // Weed out the identical connections
        System.out.println("We have " + edges.size() + " edges found.");
        ArrayList<Edge> shortEdgeList = new ArrayList<>();

        for (int i = 0; i < edges.size(); i ++) {
            // if this edge is not equal to any in the short list then add it
            boolean isInList = false;
            for (int j = 0; j < shortEdgeList.size(); j++) {
                if (edges.get(i).equals(shortEdgeList.get(j))) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                shortEdgeList.add(edges.get(i));
            }
        }
        System.out.println("We have " + shortEdgeList.size() + " edges in the unique list.");

        // Recalculating vertex colors would go here? Maybe?
        // Step 1–2: Extract and sort charges
        List<Double> charges = triangles.stream()
                .map(Triangle::getCharge)
                .sorted()
                .collect(Collectors.toList());

        int n = charges.size();
        if (n < 10) {
            System.err.println("Too few triangles for percentile clamping — using raw min/max");
        }

        // Step 3: Get clamped percentile range
        double min = charges.get((int) (0.01 * n));
        double max = charges.get((int) (0.99 * n));

        for (int i = 0; i < shortEdgeList.size(); i++) {
            if (shortEdgeList.get(i).getV1().getV() > max) {
                shortEdgeList.get(i).setV1charge(max);
            }
            if (shortEdgeList.get(i).getV1().getV() < min){
                shortEdgeList.get(i).setV1charge(min);
            }
            if (shortEdgeList.get(i).getV2().getV() > max) {
                shortEdgeList.get(i).setV2charge(max);
            }
            if (shortEdgeList.get(i).getV2().getV() < min){
                shortEdgeList.get(i).setV2charge(min);
            }
        }



        // Convert it all into a pdb file...
        ArrayList<String> pdbLines = new ArrayList<>();
        ArrayList<String> conectLines = new ArrayList<>();

        PDBAtom fakeAtom = new PDBAtom("ATOM      1 N1   XXX    1      57.279  59.263  90.609  1.00  0.00          N   -0.347");

        for (int i = 0; i < shortEdgeList.size(); i++) {
            Edge E = shortEdgeList.get(i);
            Vertex v1 = E.getV1();
            Vertex v2 = E.getV2();

            fakeAtom.setX(v1.getX());
            fakeAtom.setY(v1.getY());
            fakeAtom.setZ(v1.getZ());
            int s1 = v1.getSerial();
            fakeAtom.setSerial(s1);
            fakeAtom.setCharge(v1.getV());
            fakeAtom.setBFactor(v1.getV());
            pdbLines.add(fakeAtom.toPDBLine());

            fakeAtom.setX(v2.getX());
            fakeAtom.setY(v2.getY());
            fakeAtom.setZ(v2.getZ());
            int s2 = v2.getSerial();
            fakeAtom.setSerial(s2);
            fakeAtom.setCharge(v2.getV());
            fakeAtom.setBFactor(v2.getV());
            pdbLines.add(fakeAtom.toPDBLine());
            pdbLines.add("TER");

            conectLines.add(String.format("CONECT%5d%5d", s1, s2));
        }


        try (FileWriter outputF = new FileWriter(filename)) {
            for (String line : pdbLines) {
                outputF.write(line + "\n");
            }

            for (String conect : conectLines) {
                outputF.write(conect + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

























