public class ColorMethods {

    private ColorMethods() {}; // To prevent instantiation of static class

    public static RGB valueToRGB(double value, double max, double min) {

        if (value > 0) {
            double v = value / max;
            double r = 0.00;
            double g = 1.00 - v;
            double b = v;
            return new RGB(r, g, b);
        }

        if (value < 0) {
            double v = value / min;
            double r = v;
            double g = 1 - v;
            double b = 0.00;
            return new RGB( r, g, b);
        }

        if (value == 0) {
            return new RGB(0.0, 1.0, 0.0);
        }

        RGB rgb = new RGB(1.0, 2.0, -10.0);
        return rgb;
    }

    public static String rgbToString(RGB rgb) {
        return "(" + rgb.getR() + "," + rgb.getG() + "," + rgb.getB() + ")";
    }
}
