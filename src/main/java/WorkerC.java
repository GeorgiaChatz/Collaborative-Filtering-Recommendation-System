



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.random.JDKRandomGenerator;


public class WorkerC extends Thread{

	private final double lambda=0.1;
	private static long workerRatio;
	private String serverIP;
	private int port;
	private RealMatrix Xsub;
	private RealMatrix Ysub;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket connection;

	private RealMatrix X;
	private RealMatrix Y;
	private RealMatrix C;
	private RealMatrix P;
	private RealMatrix Cu;
	private RealMatrix Ci;
	private RealMatrix Pu;
	private RealMatrix Pi;


	public static void main(String [] args){
		workerRatio=getRatio();
		new WorkerC();
	}


	public WorkerC(){
		this.start();
	}
	public void run(){
		initialize();
	}




	public void initialize(){

		try{


			connection = new Socket("192.168.1.19",4205);
			System.out.println("Connected\n");


			out = new ObjectOutputStream(connection.getOutputStream());


			out.writeLong(getRatio());
			out.flush();

			boolean runW=true;
			in = new ObjectInputStream(connection.getInputStream());
			int k=0;

			while(runW) {

				try {



					Package p = (Package) in.readObject();


					RealMatrix m = p.getMatrix();


					if(k==0){

						Y=p.getY();
						resizeY(Y,p.getNumberR());
						C=p.getC();
						P=p.getP();
					}



					if(p.getName_matrix().equals("X")) {


						for (int i = 0; i < m.getRowDimension(); i++) {
							m.setRowMatrix(i, calculate_x_u(i, this.Y, calculateCu(i, C), calculatePu(i, P)).transpose());
						}
					}else {



						for (int i = 0; i < m.getRowDimension(); i++) {
							m.setRowMatrix(i, calculate_y_i(i, this.X, calculateCi(i, C), calculatePi(i, P)).transpose());
						}
					}



					if(p.getName_matrix().equals("X")) {
					    this.X=m;

                    }
					if(p.getName_matrix().equals("Y")) {
					    this.Y=m;

                    }


					p.setMatrix(m);

					out.writeObject(p);
					out.flush();





				k++;

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e){
					System.out.println("Server Disconnected");
					break;
				}

			}

			closeConnection();

		}catch(UnknownHostException e){
			System.err.println("Unknown Host Exception!");
		}catch(IOException e){
			e.printStackTrace();
		}


	}




	public RealMatrix calculateCu(int u, RealMatrix C) {
		double[] arr = new double[Y.getRowDimension()];

		for(int i = 0; i < Y.getRowDimension(); i++) {
			arr[i] = C.getEntry(u,i);
		}

		RealMatrix Cu = MatrixUtils.createRealDiagonalMatrix(arr);

		return Cu;
	}

	public RealMatrix calculateCi(int i, RealMatrix C) {
		double[] arr = new double[X.getRowDimension()];

		for(int j = 0; j < X.getRowDimension(); j++) {
			arr[j] = C.getEntry(j,i);
		}

		RealMatrix Ci = MatrixUtils.createRealDiagonalMatrix(arr);

		return Ci;
	}


	public RealMatrix preCalculateXX(RealMatrix X){
		return X.multiply(X.transpose());
	}

	public RealMatrix preCalculateYY(RealMatrix Y){
		return Y.multiply(Y.transpose());
	}

	public RealMatrix calculate_y_i(int u,RealMatrix X,RealMatrix Ci,RealMatrix Pi){

		RealMatrix Yi=(X.transpose().multiply(Ci).multiply(X)).add((MatrixUtils.createRealIdentityMatrix(X.getColumnDimension()).scalarMultiply(lambda)));
		Yi=new LUDecomposition(Yi).getSolver().getInverse();
		Yi=Yi.multiply(X.transpose().multiply(Ci).multiply(Pi));



		return Yi;
	}


	public RealMatrix calculate_x_u(int i, RealMatrix Y, RealMatrix Cu,RealMatrix Pu){


		RealMatrix Xu=(Y.transpose().multiply(Cu).multiply(Y)).add((MatrixUtils.createRealIdentityMatrix(Y.getColumnDimension()).scalarMultiply(lambda)));
		Xu= new LUDecomposition(Xu).getSolver().getInverse();
		Xu=Xu.multiply(Y.transpose().multiply(Cu).multiply(Pu));


		return Xu;
	}

	public RealMatrix calculatePu(int u,RealMatrix P){
		double [] arr =new double[Y.getRowDimension()];
		for(int i=0;i<Y.getRowDimension();i++){
			arr[i]=P.getEntry(u,i);
		}
		RealMatrix Pu=MatrixUtils.createColumnRealMatrix(arr);

		return Pu;
	}

	public RealMatrix calculatePi(int i, RealMatrix P) {
		double[] arr = new double[X.getRowDimension()];

		for(int j = 0; j < X.getRowDimension(); j++) {
			arr[j] = P.getEntry(j,i);
		}

		RealMatrix Pi = MatrixUtils.createColumnRealMatrix(arr);

		return Pi;
	}




	public void sendResultsToMaster(RealMatrix M){
		try {
			out.writeObject(M);

		}catch(IOException e){
			e.printStackTrace();
		}
	}



	public static long getRatio(){
		long ratio;
		ratio=Runtime.getRuntime().freeMemory()/Runtime.getRuntime().availableProcessors();
		String str=Long.toString(ratio);
		long new_ratio=Long.parseLong(str.substring(1, 5));
		return ratio;
	}
	public void closeConnection() { //close the connection between Master-Worker
		try {
			in.close(

			);
			out.close();
			connection.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void resizeY(RealMatrix Y,int dim){
		double [][] arr = new double[dim][Y.getColumnDimension()];
		for(int i=0;i<dim;i++){
			for(int j=0;j<Y.getColumnDimension();j++){
				arr[i][j]=Y.getEntry(i,j);
			}
		}
		Y=MatrixUtils.createRealMatrix(arr);
	}


}




