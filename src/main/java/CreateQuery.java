

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CreateQuery extends Thread {

    public synchronized void run() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Number of Recomendations");
        int k = input.nextInt();
        Socket s = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {

            s = new Socket("localhost",5500);

            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());

            //UserInfoRequestContract user = new UserInfoRequestContract(37.994100,23.732400,k,10);
            out.writeInt(5);
            out.flush();
            new ShowResults().getResults(in, k);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            in.close();
            out.close();
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




    }


}
