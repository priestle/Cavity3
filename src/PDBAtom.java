public class PDBAtom {

    // Variables
    private int     serial;
    private String  name;
    private String  resName;
    private char    chainID;
    private int     resSeq;
    private double  x, y, z;
    private double  occupancy;
    private double  bFactor;
    private double  charge;
    private double  VdWradius;
    private String  element;

    // Constructor
    public PDBAtom(String pdbLine) {
        this.serial = Integer.parseInt(pdbLine.substring(6,11).trim());
        this.name = pdbLine.substring(12,16).trim();
        this.resName = pdbLine.substring(17,20).trim();
        this.chainID = pdbLine.charAt(21);
        this.resSeq = Integer.parseInt(pdbLine.substring(22,26).trim());
        this.x = Double.parseDouble(pdbLine.substring(30,38).trim());
        this.y = Double.parseDouble(pdbLine.substring(38,46).trim());
        this.z = Double.parseDouble(pdbLine.substring(46,54).trim());
        this.occupancy = Double.parseDouble(pdbLine.substring(54,60).trim());
        this.bFactor = Double.parseDouble(pdbLine.substring(60,66).trim());
        this.element = pdbLine.substring(76,78).trim();
        this.charge = Double.parseDouble(pdbLine.substring(78).trim());
        this.VdWradius = VdW.getRadius(this.getName());
    }

    // Gets
    public int    getSerial()     { return this.serial; }
    public String getName()       { return this.name; }
    public String getResName()    { return this.resName; }
    public char   getChainID()    { return this.chainID; }
    public int    getResSeq()     { return this.resSeq; }
    public double getX()          { return this.x; }
    public double getY()          { return this.y; }
    public double getZ()          { return this.z; }
    public double getOccupancy()  { return this.occupancy; }
    public double getbFactor()    { return this.bFactor; }
    public double getCharge()     { return this.charge; }
    public double getVdWradius()  { return this.VdWradius; }
    public String getElement()    { return this.element; }

    // Sets
    public void setHydropathyScore(double score) {
        this.bFactor = score;
    }

    public void setRadius(double radius) {
        this.VdWradius = radius;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }


    // Methods
    public String toString() {
        return  this.serial + " " + this.name + " " +  this.resName + " " +  this.chainID + " " +  this.resSeq + " " +
                this.x + " " +  this.y + " " +  this.z + " " +  this.occupancy + " " +  this.bFactor + " " +
                this.charge + " " + this.VdWradius + " " +  this.element;
    }

    public String toPDBLine() {
        String recordName = "ATOM";
        return String.format("%-6s%5d %-4s%1s%3s %1s%4d    %8.3f%8.3f%8.3f%6.2f%6.2f          %2s",
                recordName,               // columns 1–6
                serial,                   // columns 7–11
                formatAtomName(name),     // columns 13–16
                "",                       // altLoc (column 17)
                resName,                  // columns 18–20
                chainID,                  // column 22
                resSeq,                   // columns 23–26
                x, y, z,                  // 31–54
                occupancy, bFactor,       // 55–66
                "X");
    }

    private static String formatAtomName(String name) {
        name = name.trim();

        if (name.length() == 0)
            return "    ";

        // Starts with a digit → left-align (e.g., "1HG", "2HA")
        if (Character.isDigit(name.charAt(0))) {
            return String.format("%-4s", name);
        }

        // 1-letter atom name (e.g., "O", "N") → right-align with spaces
        if (name.length() == 1) {
            return " " + name + "  ";
        }

        // 2-letter atom name (e.g., "CA") → right-align
        if (name.length() == 2) {
            return " " + name + " ";
        }

        // 3-letter atom name starting with a letter → right-align
        if (name.length() == 3 && Character.isLetter(name.charAt(0))) {
            return " " + name;
        }

        // Fallback: truncate or pad to 4 chars
        return String.format("%-4s", name.substring(0, Math.min(name.length(), 4)));
    }


}

