public class Vertex {

    // Variables
    private int    serial;
    private double x;
    private double y;
    private double z;
    private double v; // value
    private boolean b; // flag

    // Constructor
    public Vertex(int serial, double x, double y, double z, double v, boolean b) {
        this.serial = serial;
        this.x = x;
        this.y = y;
        this.z = z;
        this.v = v;
        this.b = b;
    }

    // Gets
    public int     getSerial() { return this.serial; }
    public double  getX()      { return this.x; }
    public double  getY()      { return this.y; }
    public double  getZ()      { return this.z; }
    public double  getV()      { return this.v; }
    public boolean getB()      { return this.b; }

    // Sets
    public void setSerial(int serial) { this.serial = serial; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setV(double v) { this.v = v; }
    public void setB(boolean b) { this.b = b; }

    //Methods
    public boolean equals(Vertex v1) {
        boolean isEqual = true;

        if (Math.abs(this.x - v1.getX()) > 0.000001) { isEqual = false; }
        if (Math.abs(this.y - v1.getY()) > 0.000001) { isEqual = false; }
        if (Math.abs(this.z - v1.getZ()) > 0.000001) { isEqual = false; }

        return isEqual;
    }
}
