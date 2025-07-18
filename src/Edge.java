public class Edge {

    // Variables
    private Vertex v1;
    private Vertex v2;

    // Constructor
    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    // Gets

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    // Sets
    public void setV1charge(double charge) {
        this.v1.setV(charge);
    }

    public void setV2charge(double charge) {
        this.v2.setV(charge);
    }

    // Methods
    public  void swap() {
        int    serial = this.v1.getSerial();
        double anX = this.v1.getX();
        double anY = this.v1.getY();
        double anZ = this.v1.getZ();
        double anV = this.v1.getV();
        boolean anB = this.v1.getB();

        this.v1.setSerial(this.v2.getSerial());
        this.v1.setX(this.v2.getX());
        this.v1.setY(this.v2.getY());
        this.v1.setZ(this.v2.getZ());
        this.v1.setV(this.v2.getV());
        this.v1.setB(this.v2.getB());

        this.v2.setSerial(serial);
        this.v2.setX(anX);
        this.v2.setY(anY);
        this.v2.setZ(anZ);
        this.v2.setV(anV);
        this.v2.setB(anB);
    }

    public boolean equals(Edge E) {
        // Case 1: v1-v2 matches directly
        boolean sameDirection = this.v1.equals(E.getV1()) && this.v2.equals(E.getV2());

        // Case 2: v1-v2 matches in reverse (undirected edge)
        boolean reverseDirection = this.v1.equals(E.getV2()) && this.v2.equals(E.getV1());

        // Edge is equal if either case matches
        return sameDirection || reverseDirection;
    }
}
