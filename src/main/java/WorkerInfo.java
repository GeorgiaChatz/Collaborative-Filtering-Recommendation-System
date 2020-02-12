import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class WorkerInfo {


    private Socket s;
    private long ratio;
    private double percent;
    private int numberR;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    public WorkerInfo(Socket s){
        this.s=s;
        try {
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }


    public void setSocket(Socket s) {
        this.s = s;
    }

    public Socket getSocket() {
        return s;
    }

    public void setRatio(long ratio) {
        this.ratio = ratio;
    }

    public long getRatio() {
        return ratio;
    }

    public int getNumberR() {
        return numberR;
    }

    public void setNumberR(int numberR) {
        this.numberR = numberR;
    }


}
