//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;

public class MyClass {

    public static void main(String[] args) {

    }

    public JSONObject sendBack(int i) {
        JSONParser parser = new JSONParser();
        JSONObject category = null;
        try {
            Object obj = parser.parse(new FileReader("/home/gchatz/Desktop/katanemimena/p3150223-p3150162-p3150012-p3150107/Maven/georgia/src/main/java/POIs.json"));
          ;


            JSONObject sonObj = (JSONObject) obj;
            String poi = Integer.toString(i);


            category = (JSONObject) sonObj.get(poi);


        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return category;
    }


    public boolean checkCat(String category, int poid) {
        JSONParser parser = new JSONParser();
        boolean cat = false;
        JSONObject job = null;
        try {
            Object obj = parser.parse(new FileReader("/home/gchatz/Desktop/katanemimena/p3150223-p3150162-p3150012-p3150107/Maven/georgia/src/main/java/POIs.json"));



            JSONObject sonObj = (JSONObject) obj;
            String poi = Integer.toString(poid);


            job = (JSONObject) sonObj.get(poi);

            if (job.get("POI_category_id").toString().equalsIgnoreCase(category)) {

                cat = true;


            }else if ( category.equalsIgnoreCase("None")){
                cat = true;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cat;
    }

    public double Distance(double la, double lon, int i) {
        JSONParser parser = new JSONParser();
        boolean cat = false;
        JSONObject job1 = null;
        double dis =0;
        try {
            Object obj = parser.parse(new FileReader("/home/gchatz/Desktop/katanemimena/p3150223-p3150162-p3150012-p3150107/Maven/georgia/src/main/java/POIs.json"));

            JSONObject sonObj = (JSONObject) obj;
            String poi = Integer.toString(i);


            job1 = (JSONObject) sonObj.get(poi);
            dis = distance(la , lon , Double.parseDouble(job1.get("latidude").toString()) , Double.parseDouble(job1.get("longitude").toString()));
        }  catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dis;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;


        return (dist);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
