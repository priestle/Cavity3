import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        System.out.println("Starting surface generation...");

        // Globals : will need to be moved into a config file eventually
        String pdbFileName             = "C:/tmp/rg108charged.pdb";
        String ligandName              = "RGX";
        String spherePointsFileName    = "C:/tmp/icosphere2.csv";
        String tclOutputFileName       = "C:/tmp/test.tcl";
        Double cutoff                  = 4.0;

        // DNF below here when working

        // Reading in static class data : 'init' is a 'tickle' to load static class data under control
        VdW.init();

        // Reading in the ligand and cavity atoms
        System.out.println("Reading pdbfiles and processing...");
        ArrayList<PDBAtom> ligandAtoms   = PDBFileMethods.getLigandAtoms(pdbFileName, ligandName);
        ArrayList<PDBAtom> receptorAtoms = PDBFileMethods.getReceptorAtoms(pdbFileName, ligandName);
        ArrayList<PDBAtom> cavityAtoms   = PDBFileMethods.getCavityAtoms(ligandAtoms, receptorAtoms, cutoff);
        System.out.println("\tReceptor atoms   : " + receptorAtoms.size());
        System.out.println("\tLigand atoms     : " + ligandAtoms.size());
        System.out.println("\tCavity atoms     : " + cavityAtoms.size());
        if (receptorAtoms.size()== 0 || ligandAtoms.size() == 0 || cavityAtoms.size() == 0) {
            System.out.println("Issue with processing pdb files");
            System.exit(1);
        }

        // DEBUG
        for (int i = 0; i < 10; i++) {
            System.out.println(receptorAtoms.get(i));
        }

        // Assign VdW radii to receptor atoms
        for (int i = 0; i < receptorAtoms.size(); i++) {
            double radius = VdW.getRadius(receptorAtoms.get(i).getName());
            receptorAtoms.get(i).setRadius(radius);
        }

        // Convert PDBAtoms to Point3DPlus's: x, y, z, radius, hydropathy, element
        ArrayList<Point3DPlus> ligandPoints = PointMethods.atomListToPointList(ligandAtoms);
        ArrayList<Point3DPlus> cavityPoints = PointMethods.atomListToPointList(cavityAtoms);

        System.out.println("There are " + ligandPoints.size() + " ligand points.");
        System.out.println("There are " + cavityPoints.size() + " cavity points.");

        // Generate cavity triangles - expand each origin to an icosahedral shell
        ArrayList<Triangles> cavityTriangles = new ArrayList<Triangles>();

        // Done
        System.out.println("DONE.");
    }
}