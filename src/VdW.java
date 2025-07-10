public class VdW {

    private VdW() {} // Prevents instantiation

    // Persistent storage for VdW data

    public static void init() {

    }

    public static double getRadius(String name) {
        String element = name.substring(0, 1);
        Boolean found = false;
        Double radius = 0.00;
        if (element.equals("C"))  { radius = 1.70; found = true; }
        if (element.equals("N"))  { radius = 1.55; found = true; }
        if (element.equals("O"))  { radius = 1.52; found = true; }
        if (element.equals("S"))  { radius = 1.80; found = true; }
        if (element.equals("H"))  { radius = 1.20; found = true; }
        if (element.equals("P"))  { radius = 1.80; found = true; }
        if (element.equals("Z"))  { radius = 1.00; found = true; }
        if (found == false) {
            radius = 1.00;
            System.out.println("VdW element " + name + " not found");
        }
        return radius;
    }

}
