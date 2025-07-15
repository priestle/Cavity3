public class Point3DPlus {

    // Variables
    private double x;
    private double y;
    private double z;
    private double radius; // VdW radius
    private double charge; // charge
    private String element; // element

    // Constructor
    public Point3DPlus(PDBAtom anAtom) {
        this.x = anAtom.getX();
        this.y = anAtom.getY();
        this.z = anAtom.getZ();
        this.radius = anAtom.getVdWradius();
        this.charge = anAtom.getCharge();
        this.element = anAtom.getElement();
    }

    // Constructor
    public Point3DPlus(double x, double y, double z, double radius, double charge, String element) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.charge = charge;
        this.element = element;
    }

    // Gets
    public double getX()       { return this.x; }
    public double getY()       { return this.y; }
    public double getZ()       { return this.z; }
    public double getRadius()  { return this.radius; }
    public double getCharge()  { return this.charge; }
    public String getElement() { return this.element; }

    // Sets


    // Methods
}
