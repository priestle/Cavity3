import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class DumpMethods {

    private DumpMethods() {} // Can't instantiate static class

    public static void dumpAtoms(ArrayList<PDBAtom> atoms, String filename) {

        try (FileWriter outputF = new FileWriter(filename)) {
            for (int i = 0; i < atoms.size(); i++) {
                PDBAtom A = atoms.get(i);
                String aS = A.getSerial() + ",";
                aS += A.getName() + ",";
                aS += A.getResName() + ",";
                aS += A.getChainID() + ",";
                aS += A.getResSeq() + ",";
                aS += A.getX() + ",";
                aS += A.getY() + ",";
                aS += A.getZ() + ",";
                aS += A.getOccupancy() + ",";
                aS += A.getbFactor() + ",";
                aS += A.getCharge() + ",";
                aS += A.getVdWradius() + ",";
                aS += A.getElement();
                outputF.write(aS);
                if (i < atoms.size()-1) {
                    outputF.write("\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void dumpTriangles(ArrayList<Triangle> triangles, String filename) {
       // private double Ax, Ay, Az;
       // private double Bx, By, Bz;
       // private double Cx, Cy, Cz;
       // private double charge;
       // private String element;

        try (FileWriter outputF = new FileWriter(filename)) {
            for (int i = 0; i < triangles.size(); i++) {
                Triangle T = triangles.get(i);
                String aS = T.getAx() + ",";
                aS += T.getAx() + ",";
                aS += T.getAy() + ",";
                aS += T.getAz() + ",";
                aS += T.getBx() + ",";
                aS += T.getBy() + ",";
                aS += T.getBz() + ",";
                aS += T.getCx() + ",";
                aS += T.getCy() + ",";
                aS += T.getCz() + ",";
                aS += T.getCharge() + ",";
                aS += T.getElement();
                if ( i < triangles.size()-1) {
                    aS += "\n";
                }
                outputF.write(aS );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
