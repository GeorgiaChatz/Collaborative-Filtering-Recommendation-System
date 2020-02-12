

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ShowResults {

	ArrayList<Integer> poiList = new ArrayList<Integer>();

	public void showResults(int k) {
		System.out.println("Here are the suggested POI");
		for(int i =0; i < k ; i++) {
			System.out.println(poiList.get(i));
		}

	}

	public void getResults(ObjectInputStream in,int k) {
		try {
				//poiList = new ArrayList<POI>();
		    poiList = (ArrayList<Integer>) in.readObject();
		    showResults(k);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
