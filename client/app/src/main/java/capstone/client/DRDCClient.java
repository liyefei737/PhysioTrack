package capstone.client;

import android.app.Application;

import java.util.Date;

/**
 * Created by Grace on 2017-01-21.
 */

public class DRDCClient extends Application {

    //global vars for app
    //access in any activity by String s = ((MyApplication) this.getApplication()).getSomeVariable(); or
    //    ((MyApplication) this.getApplication()).setSomeVariable("foo");

    private String serviceNumber;
    private String name;
    private Date dob;
    private float weight;
    private float height;


    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String id) {
        this.serviceNumber = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float w) {
        this.weight = w;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float h) {
        this.height = h;
    }
}