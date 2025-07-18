import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        System.out.println("Starting surface generation...");

        // Globals : will need to be moved into a config file eventually
        String pdbFileName             = "C:/tmp/rg108charged.pdb";
        String ligandName              = "RGX";
        String spherePointsFileName    = "C:/tmp/subdiv_3_triangles.txt";
        String tclOutputSolidFileName  = "C:/tmp/test1.tcl";
        String tclOutputMeshFilename   = "C:/tmp/testMesh.pdb";
        String ligandAtomFileName      = "C:/tmp/ligand.csv";
        String triangleFilename        = "C:/tmp/triangle.csv";
        String trianglePdbName         = "C:/tmp/trianglesAsPdb.pdb";
        Double cutoff                  = 4.0;
        double potentialCutoff         = 5.0;
        Boolean parallel               = true;

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
        ArrayList<Triangle> cavityTriangles = TriangleMethods.makeTriangles(cavityPoints, spherePointsFileName);
        System.out.println("There are " + cavityTriangles.size() + " total triangles representing the cavity.");

        // So we're going to need to parallelize this... output some intermediate files here...
        DumpMethods.dumpAtoms(ligandAtoms, ligandAtomFileName);
        DumpMethods.dumpTriangles(cavityTriangles, triangleFilename);
        DumpMethods.dumpTrianglesAsPdb(cavityTriangles, trianglePdbName);

        // Run some tests... can remove later
        Tests.colorTest();

        // Trim any triangles that are not completely visible to the ligand
        ArrayList<Triangle> visibleTriangles = new ArrayList<>();
        if (parallel) {
            visibleTriangles = TriangleMethods.selectVisibleToLigandParallel(ligandAtoms, cavityTriangles);

        } else {
            visibleTriangles = TriangleMethods.selectVisibleToLigand(ligandAtoms, cavityTriangles);
        }


        System.out.println("There are " + visibleTriangles.size() + " triangles visible to the ligand.");

        // Recolor triangles based on a potential calculation
        ArrayList<PDBAtom> potentialAtoms = PDBFileMethods.getCavityAtoms(cavityAtoms,receptorAtoms,potentialCutoff);
        System.out.println("There are " + potentialAtoms.size() + " atoms that interact with the cavity.");

        ArrayList<Triangle> rechargedTriangles = TriangleMethods.recalculateCharge(visibleTriangles, potentialAtoms);

        // Output the triangles in a form that VMD can process as a tcl script
        TriangleMethods.makeTCLFileOfTriangles(rechargedTriangles, tclOutputSolidFileName);
        TriangleMethods.makeTCLFileOfMesh(rechargedTriangles,tclOutputMeshFilename, potentialAtoms);

        // Done
        System.out.println("DONE.");
    }
}