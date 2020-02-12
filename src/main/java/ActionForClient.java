import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ActionForClient extends Thread {

	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	ArrayList<POI> Pois = new ArrayList<POI>();

	public ActionForClient(Socket connection) {
		try {

			in = new ObjectInputStream(connection.getInputStream());
      		out = new ObjectOutputStream(connection.getOutputStream());

       } catch (IOException e) {
        e.printStackTrace();
       }
   }

   public synchronized void run() {
		 try {

				UserInfoRequestContract u = (UserInfoRequestContract)in.readObject();
				int k = u.getN_pois();

				for ( int i =0; i<k; i++) {
					POI p = new POI("Buenos Ares");
					Pois.add(p);
				}
				out.writeObject(Pois);
				out.flush();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					        in.close();
					        out.close();

	            } catch (IOException ioException) {
	                ioException.printStackTrace();
	            }
			}




   }

}
