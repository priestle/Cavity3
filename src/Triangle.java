public class Triangle {

    // Variables
    private double Ax, Ay, Az;
    private double Bx, By, Bz;
    private double Cx, Cy, Cz;
    private double charge;
    private String element;

    // Constructor
    public Triangle(double Ax, double Ay, double Az,
                     double Bx, double By, double Bz,
                     double Cx, double Cy, double Cz,
                     double charge, String element) {

        // This is just the class that describes one triangle
        this.Ax = Ax;
        this.Ay = Ay;
        this.Az = Az;
        this.Bx = Bx;
        this.By = By;
        this.Bz = Bz;
        this.Cx = Cx;
        this.Cy = Cy;
        this.Cz = Cz;
        this.charge = charge;
        this.element = element;
    }

    // Gets
    public double getAx() { return this.Ax; }
    public double getAy() { return this.Ay; }
    public double getAz() { return this.Az; }
    public double getBx() { return this.Bx; }
    public double getBy() { return this.By; }
    public double getBz() { return this.Bz; }
    public double getCx() { return this.Cx; }
    public double getCy() { return this.Cy; }
    public double getCz() { return this.Cz; }
    public double getCharge() { return this.charge; }
    public String getElement() { return this.element; }

    public double getCentroidCoord(int axis) {
        double x = (getAx() + getBx() + getCx()) / 3.0;
        double y = (getAy() + getBy() + getCy()) / 3.0;
        double z = (getAz() + getBz() + getCz()) / 3.0;

        switch (axis) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            default: throw new IllegalArgumentException("Axis must be 0 (x), 1 (y), or 2 (z)");
        }
    }


    // Sets
    public void setCharge(double charge) {
        this.charge = charge;
    }
    public void setElement(String element) { this.element = element; }


    // Methods
}
