public class Triangles {

    // Variables
    private double Ax, Ay, Az;
    private double Bx, By, Bz;
    private double Cx, Cy, Cz;
    private double charge;
    private String element;

    // Constructor
    public Triangles(double Ax, double Ay, double Az,
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
}
