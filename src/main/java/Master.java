import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.List;

import static java.lang.Math.abs;

public class Master {

	public static void main(String[] args){
		new Master().initialize();
	}

	private ServerSocket sC = null ; //ServerSocket for client
	private Socket sClient = null; //Socket ofr Client
	private Socket sWorkers=null;
	private ServerSocket ssWorker = null; //ServerSocket for workers
	private RealMatrix R;
	private RealMatrix X;
	private RealMatrix Y;
	private RealMatrix P;
	private RealMatrix C;
	private RealMatrix Rec;
	private ArrayList<WorkerInfo> workers;
	private ArrayList<Package> packages;
	private final double lambda=0.1;
	private final int k=20; //dimension X,Y
	private double previous_training=0.0;
	private double current_training=0.0;
	private final int iterations=1;
	private int cln=0;


	public void initialize() {

		try {

			load_DataSet();


			workers = new ArrayList<WorkerInfo>();


			ssWorker = new ServerSocket(4205);


			System.out.println("Enter number of workers");
			Scanner input = new Scanner(System.in);
			int workNumber = input.nextInt();



			int wn = 0;
			boolean shouldRun = true;
			long total = 0;

			while (shouldRun) {
				Socket sWorker = null;

				sWorker = ssWorker.accept();

				if (sWorker != null) {
					System.out.println("A Worker has connected");
					wn++;
					WorkerInfo info = new WorkerInfo(sWorker);
					info.setRatio(info.getIn().readLong());
					total += info.getRatio();
					System.out.println("RAM/CPU " + info.getRatio());


					workers.add(info);
					if (workNumber == wn) {
						System.out.println("All workers connected\n");
						break;
					}
				}
			}
			//have all workers connected


			for (int i = 0; i < workers.size(); i++) {
				workers.get(i).setPercent((float) (workers.get(i).getRatio()) / total);
			}
			//update the percentage of lines of initial Matrix that each worker will receive


			createXY(k);


			calculateCMatrix(40, R);
			calculatePMatrix(R);
			//needed for calculations


			int it = 0;// iterations

			while ((abs(current_training - previous_training) > 0.01 || it==0) && it < iterations) {

				distributeX(X, total);
				distributeY(Y, total);
				previous_training = current_training;
				current_training = this.calculateError();
				System.out.println("Epoch " +(it)+ " total error "+current_training);
				it++;
			}

			System.out.println("Final error: " + current_training);


			ServerSocket sClient = new ServerSocket(4202);
			int times = 0;
			//times <= cln
			while (true) {

				Socket sC = sClient.accept();

				ObjectInputStream inClient = new ObjectInputStream(sC.getInputStream());
				ObjectOutputStream outClient = new ObjectOutputStream(sC.getOutputStream());

				int k = inClient.readInt();
				int uid = inClient.readInt();
				ArrayList<Integer> recomendations = new ArrayList<Integer>(k);
				ArrayList<Integer> sets = new ArrayList<Integer>();
				String UserCategory = inClient.readUTF();
				if (UserCategory.equalsIgnoreCase("arts")){
					UserCategory = "Arts & Entertainment";
				}
				double longi = inClient.readDouble();
				double lat = inClient.readDouble();
				double range = inClient.readDouble();
				sets = getSet(uid, range,UserCategory,longi,lat);
				recomendations = getPois(uid, k,sets);

				outClient.writeInt(recomendations.size());
				outClient.flush();
				MyClass my = new MyClass();

				for (int i = 0; i < recomendations.size(); i++) {
					if (recomendations.get(i) <= 1691) {

						JSONObject back = my.sendBack(recomendations.get(i));

						String nameOfpoi = back.get("POI_name").toString();

						String longitude = back.get("longitude").toString();

						String latidude = back.get("latidude").toString();

						String category = back.get("POI_category_id").toString();


						outClient.writeUTF(nameOfpoi);
						outClient.flush();
						outClient.writeUTF(longitude);
						outClient.flush();
						outClient.writeUTF(latidude);
						outClient.flush();
						outClient.writeUTF(category);
						outClient.flush();

					}
				}

				inClient.close();
				outClient.close();


			}


			} catch(IOException e){
				e.printStackTrace();
			}

		}


	public void load_DataSet(){

		BufferedReader br=null;
		FileReader fr=null;
		File f=null;
		String line=null;
		double a=0;
		//R= MatrixUtils.createRealMatrix(765,1964);
		double temp[][] = new double[765][1964];
		int row=0;
		int column=0;

		String fileName= "/home/gchatz/Desktop/katanemimena/p3150223-p3150162-p3150012-p3150107/Maven/georgia/src/main/java/file.txt";
		try{

			br = new BufferedReader(new FileReader(fileName));

			RealMatrix M=MatrixUtils.createRealMatrix(765,1964);
			while((line=br.readLine())!=null){

				String[]arr=line.split(", ");
				row=(int)Integer.parseInt(arr[0]);

				column=(int)Integer.parseInt(arr[1].trim());
				a=(double)Double.parseDouble(arr[2].trim());



				temp[row][column]=a;
				M.setEntry(row,column,a);
			}


			this.R=M;

		}catch(IOException e){
			e.printStackTrace();
		}

	}


	    public void distributeX(RealMatrix X,long total) {

			int endX = 0;//end of previous worker
			int endY=0;
			int numberRX = 0;//number of rows for user
			int numberRY =0;
			int Xsize = X.getRowDimension();
			int Ysize = Y.getRowDimension();
			int startX=0;
			int startY=0;
			ArrayList<Package> packages = new ArrayList<Package>();

			//create and send packages to workers through ActionForWorker

			for (int i = 0; i < workers.size(); i++) {
				numberRX = (int) (X.getRowDimension() * workers.get(i).getPercent());
				numberRY =(int)(Y.getRowDimension()*workers.get(i).getPercent());
				Xsize -= numberRX;
				Ysize-=numberRY;
				endX+=numberRX;
				endY+=numberRY;
				if (i == workers.size()-1 && Xsize > 0) {
					numberRX += Xsize;
					endX+=Xsize;
					Xsize=0;
				}
				if(i==workers.size()-1 && Ysize>0){
					numberRY += Ysize;
					endY+=Ysize;
					Ysize=0;
				}
				workers.get(i).setNumberR(numberRX);


				Package p = new Package(startX, endX , distributeXX(startX, endX ,X),P,C,Y,numberRY);
				p.setName_matrix("X");
				packages.add(p);
				startX=endX+1;
			}
			send_receive_Packages(packages,workers,"X");

		}//end distribute method



	public void distributeY(RealMatrix Y,long total) {
		int end = 0;//end of previous worker
		int numberR = 0;//number of rows for user
		int Ysize = Y.getRowDimension();
		int start=0;
		ArrayList<Package> packages = new ArrayList<Package>();

		//create and send packages to workers through ActionForWorker

		for (int i = 0; i < workers.size(); i++) {
			numberR = (int) (Y.getRowDimension() * workers.get(i).getPercent());
			Ysize -= numberR;
			end+=numberR;
			if (i == workers.size()-1 && Ysize > 0) {
				numberR += Ysize;
				end+=Ysize;
				Ysize=0;
			}


			Package p = new Package(start, end , distributeYY(start, end ,Y),P,C,Y,numberR);
			p.setName_matrix("Y");
			packages.add(p);
			start=end+1;
		}

		send_receive_Packages(packages,workers,"Y");

	}//end distribute method


			public RealMatrix distributeXX(int start,int end,RealMatrix matrix){
				double [][] arr= new double[end-start][matrix.getColumnDimension()];

				for(int i=0;i<end-start;i++){
					for(int j=0;j<matrix.getColumnDimension();j++){
						arr[i][j]=matrix.getEntry(i,j);}}

				RealMatrix Xsub = MatrixUtils.createRealMatrix(arr);
				return Xsub;
			}


	public RealMatrix distributeYY(int start,int end,RealMatrix matrix){
		double [][] arr= new double[end-start][matrix.getColumnDimension()];

		for(int i=0;i<end-start;i++){
			for(int j=0;j<matrix.getColumnDimension();j++){
				arr[i][j]=matrix.getEntry(i,j);}}

		RealMatrix Ysub = MatrixUtils.createRealMatrix(arr);
		return Ysub;
	}


	public void createXY(int k){

		X= MatrixUtils.createRealMatrix(R.getRowDimension(),k);
		Y= MatrixUtils.createRealMatrix(R.getColumnDimension(),k);
		JDKRandomGenerator r = new JDKRandomGenerator(1);
		r.setSeed(1);

		for(int i=0;i<X.getRowDimension();i++){
			for(int j=0;j<X.getColumnDimension();j++){
				this.X.setEntry(i,j,r.nextDouble());
			}
		}

		for(int i=0;i<Y.getRowDimension();i++){
			for(int j=0;j<Y.getColumnDimension();j++){
				this.Y.setEntry(i,j,r.nextDouble());
			}
		}

	}

	public RealMatrix calculateCMatrix(int a,RealMatrix R){
		double arr[][] = new double[R.getRowDimension()][R.getColumnDimension()];
		int num=0;
		for(int i=0;i<R.getRowDimension();i++){
			for(int j=0;j<R.getColumnDimension();j++){
				arr[i][j]=1+a*R.getEntry(i,j);
			}
		}

		C=MatrixUtils.createRealMatrix(arr);
		return C;}


	public RealMatrix calculatePMatrix(RealMatrix R){
		double [][] arr = new double[R.getRowDimension()][R.getColumnDimension()];
		for(int i=0;i<R.getRowDimension();i++){
			for(int j=0;j<R.getColumnDimension();j++){
				if(R.getEntry(i,j)>0) {
					arr[i][j] = 1;
				}else{
					arr[i][j]=0;
				}
			}
		}

		P=MatrixUtils.createRealMatrix(arr);
		return P;
	}





	public double calculateError(){

		double sum=0.0,normal_term =0.0;
		double a,b;

		for(int i=0;i<R.getRowDimension();i++){
			for(int j=0;j<R.getColumnDimension();j++) {

					a= C.getEntry(i, j) * Math.pow((P.getEntry(i, j) - ((X.getRowMatrix(i).multiply(Y.getRowMatrix(j).transpose()))).getEntry(0,0)),2);
					RealMatrix m = X.getRowMatrix(i).multiply(Y.getRowMatrix(j).transpose());

					sum += a;
			}
		}

		for(int i=0;i<X.getRowDimension();i++){
			normal_term+=(Math.pow(X.getRowMatrix(i).getFrobeniusNorm(),2));}

		for(int i=0;i<Y.getRowDimension();i++){
			normal_term+=(Math.pow(Y.getRowMatrix(i).getFrobeniusNorm(),2));}

		normal_term*=lambda;
		double error= sum+normal_term;

		return error;
	}

	public double calculateScore(int u,int p){
		double score=X.getRowMatrix(u).transpose().multiply(Y.getRowMatrix(p)).getEntry(0,0);
		return score;
	}


	public void send_receive_Packages(ArrayList<Package> packages,List<WorkerInfo> workers,String matr){
		try {
			for (int i = 0; i < workers.size(); i++) {

				workers.get(i).getOut().writeObject(packages.get(i));
			}
		}catch(IOException e){
			e.printStackTrace();
		}



		packages.clear();
		try{


			for(int i=0;i<workers.size();i++){
				packages.add((Package)workers.get(i).getIn().readObject());
			}
			this.packages=packages;

		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}


		if(matr.equals("X")) createXafter(this.packages);
		if(matr.equals("Y")) createYafter(this.packages);

	}

	public void createXafter(List<Package> packages){


			for (int i = 0; i < packages.size(); i++) {

				int k = 0;



				for (int j = packages.get(i).getStart(); j < packages.get(i).getEnd(); j++) {

					this.X.setRowMatrix(j, packages.get(i).getMatrix().getRowMatrix(k));
					k++;
				}

			}
	}

	public void createYafter(List<Package> packages){

			for (int i = 0; i < packages.size(); i++) {
				int k = 0;
				for (int j = packages.get(i).getStart(); j < packages.get(i).getEnd(); j++) {

					this.Y.setRowMatrix(j, packages.get(i).getMatrix().getRowMatrix(k));
					k++;
				}
			}
	}




	public void createR(){
		Rec= MatrixUtils.createRealMatrix(R.getRowDimension(),R.getColumnDimension());
		for(int i=0;i<R.getRowDimension();i++){
			for(int j=0;j<R.getColumnDimension();j++){
				double val = calculateScore(i,j);

				Rec.setEntry(i,j,val);
			}
		}
	}
	public ArrayList<Integer> getPois(int u,int k,ArrayList<Integer> sets1){

		ArrayList<Integer> rec = new ArrayList<Integer>(k);
		MyClass myclass = new MyClass();


		double max=0.0;
		int poi=-1;
		while(true) {
			for (int i = 0; i < sets1.size(); i++) {



				if (Rec.getEntry(u, sets1.get(i)) > max && !rec.contains(sets1.get(i))) {
					max = Rec.getEntry(u, sets1.get(i));
					poi = sets1.get(i);


				}



			}
			rec.add(poi);

			max=0.0;
			poi=0;

			if(rec.size()==k) break;
		}
		return rec;
	}
	public ArrayList<Integer> getSet (int user , double range,String category,double longitude , double latidude){
		ArrayList<Integer> sets =new ArrayList<Integer>();
		double dis=0;
		boolean cat =false;
		createR();

		int poi=-1;
		MyClass myclass = new MyClass();
		for ( int i=0; i <Rec.getColumnDimension(); i++){
			if ( i <= 1691) {
				cat = myclass.checkCat(category, i);

				dis = myclass.Distance(latidude,longitude,i);


				if (!sets.contains(i) && (cat == true) && dis <= range) {


					poi = i;
					sets.add(poi);

				}


			}


		}
		return sets;


	}

}
