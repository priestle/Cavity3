import java.io.FileWriter;
import java.util.ArrayList;

public class Tests {

    private Tests() {}  // This is a static class

    public static void colorTest() {

        // A test of the coloring algorithm by outputting a tcl color bar

        ArrayList<RGB> colors = new ArrayList<>();

        for (int i = -255; i < 256; i++) {
            RGB color = ColorMethods.valueToRGB(i, 256, -256);
            colors.add(color);
        }

        ArrayList<String> tclLines = new ArrayList<>();

        tclLines.add("mol new");
        tclLines.add("draw material Transparent");
        for (int i = 0; i < colors.size(); i++) {
            tclLines.add("color change rgb " + (i+40) + " " + colors.get(i).getR() + " " +
                          colors.get(i).getG() + " " + colors.get(i).getB());
        }

        for (int i = 0; i < colors.size(); i++) {
            tclLines.add("draw color " + (i+40));
            double x1 = i / 128.0;
            double x2 = (i+1) / 128.0;

            tclLines.add("draw triangle {" + x1 + " 0.0000 0.0000} {" +
                                             x2 + " 0.0000 0.0000} {" +
                                             x1 + " 1.0000 0.0000} ");
            tclLines.add("draw triangle {" + x2 + " 0.0000 0.0000} {" +
                                             x1 + " 1.0000 0.0000} {" +
                                             x2 + " 1.0000 0.0000} ");
        }

        try (FileWriter outputF = new FileWriter("C:/tmp/testColors.tcl")) {
            for (int i = 0; i < tclLines.size(); i++) {
                outputF.write(tclLines.get(i) + "\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
