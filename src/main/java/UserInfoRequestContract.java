

import java.io.Serializable;

public class UserInfoRequestContract implements Serializable {

    private double longitude;
    private double latitude;
    private int n_pois;
    private int userNum ;

    public UserInfoRequestContract(double longitude,double latitude,int n_pois,int userNum){
        this.longitude=longitude;
        this.latitude=latitude;
        this.n_pois=n_pois;
        this.userNum = userNum;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    public void setUserNum(int userNum) {
    	this.userNum = userNum;
    }
    
    public int getUserNum() {
        return userNum ;
    }

    public void setN_pois(int n_pois) {
        this.n_pois = n_pois;
    }

    public int getN_pois() {
        return n_pois;
    }
}

