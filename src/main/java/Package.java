import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;

public class Package implements Serializable {

    private int start;
    private int end;
    private RealMatrix matrix;
    private int dim;
    private String name_matrix;
    private RealMatrix P;
    private RealMatrix C;
    private RealMatrix Y;
    private int numberR;

    public Package(int start,int end,RealMatrix matrix,RealMatrix P,RealMatrix C,RealMatrix Y,int n){
        this.start=start;
        this.end=end;
        this.matrix=matrix;
        this.P=P;
        this.C=C;
        this.Y=Y;
        this.numberR=n;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) { this.start = start; }

    public int getEnd() {
        return end;
    }

    public RealMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(RealMatrix matrix) {
        this.matrix = matrix;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setName_matrix(String name_matrix) {
        this.name_matrix = name_matrix;
    }

    public String getName_matrix() {
        return name_matrix;
    }

    public void setP(RealMatrix p) { P = p;}

    public void setC(RealMatrix c) { C = c;}

    public RealMatrix getC() { return C; }

    public RealMatrix getP() { return P; }

    public RealMatrix getY() { return Y; }

    public void setY(RealMatrix y) { Y = y; }

    public void setNumberR(int numberR) { this.numberR = numberR; }

    public int getNumberR() { return numberR; }
}
