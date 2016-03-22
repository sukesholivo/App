package in.olivo.patientcare.main.om;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/8/2014.
 */
public class GraphData {
    private final String title;
    private final ArrayList<Double> X;
    private final ArrayList<Double> Y;
    private final int lineColor;
    private final int lineWidth;

    public GraphData(String title, ArrayList<Double> X, ArrayList<Double> Y, int lineColor, int lineWidth) {
        this.title = title;
        this.X = X;
        this.Y = Y;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Double> getX() {
        return X;
    }

    public ArrayList<Double> getY() {
        return Y;
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }
}
