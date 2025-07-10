import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class PDBFileMethods {

    private PDBFileMethods() {}; // Prevents instantiation of static class

    // ****************************************************************** //
    // Input:  pdb file name, ligand residue name
    // Output: arraylist of PDBAtom containing ligand atoms

    public static ArrayList<PDBAtom> getLigandAtoms(String filename, String residueName) {

        ArrayList<PDBAtom> atomList = new ArrayList<>();

        try (Scanner inputF = new Scanner(Paths.get(filename))) {
            while (inputF.hasNextLine()) {
                String row = inputF.nextLine();
                if (row.startsWith("ATOM") || row.startsWith("HETATM")) {
                    PDBAtom anAtom = new PDBAtom(row);
                    if (anAtom.getResName().contains(residueName)) {
                        atomList.add(anAtom);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return atomList;
    }

    // ***************************************************************** //
    // Input:  pdb file name, ligand residue name
    // Output: arrayList of PDBatom containing non-ligand atoms

    public static ArrayList<PDBAtom> getReceptorAtoms(String filename, String residueName) {

        ArrayList<PDBAtom> atomList = new ArrayList<>();

        try (Scanner inputF = new Scanner(Paths.get(filename))) {
            while (inputF.hasNextLine()) {
                String row = inputF.nextLine();
                if (row.startsWith("ATOM") || row.startsWith("HETATM")) {
                    PDBAtom anAtom = new PDBAtom(row);
                    if (!anAtom.getResName().contains(residueName)) {
                        atomList.add(anAtom);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return atomList;
    }

    // ************************************************************** //
    // Input:  ArrayLists of ligand and receptor atoms, cutoff distance
    // Output: ArrayList of receptor atoms that are within cutoff distance to ligand
    // Notes:  Should balk if result is zero length...

    public static ArrayList<PDBAtom> getCavityAtoms(ArrayList<PDBAtom> ligandAtoms,
                                                    ArrayList<PDBAtom> receptorAtoms,
                                                    double cutoff) {

        ArrayList<PDBAtom> cavityAtoms = new ArrayList<>();

        for (int i = 0; i < receptorAtoms.size(); i++) {
            boolean closeAtom = false;
            for (int j = 0; j < ligandAtoms.size(); j++) {

                double dx = receptorAtoms.get(i).getX() - ligandAtoms.get(j).getX();
                double dy = receptorAtoms.get(i).getY() - ligandAtoms.get(j).getY();
                double dz = receptorAtoms.get(i).getZ() - ligandAtoms.get(j).getZ();
                double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

                if (distance <= cutoff) {
                    closeAtom = true;
                    break;
                }
            }
            if (closeAtom) {
                cavityAtoms.add(receptorAtoms.get(i));
            }
        }


        return cavityAtoms;
    }

    // *************************************************************** //
}










































