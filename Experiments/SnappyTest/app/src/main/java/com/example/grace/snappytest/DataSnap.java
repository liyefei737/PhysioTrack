package com.example.grace.snappytest;

import java.io.Serializable;
import java.util.*;
import java.io.*;
/**
 * Created by Grace on 10/29/2016.
 */

public class DataSnap implements Serializable{
    /**
     * QRS wave.
     * @serial
     */
    private float QRS;

    /**
     * Heart rate.
     * @serial
     */
    private float heartRate;

    /**
     * Breathing rate
     * @serial
     */
    private float breathingRate;

    /**
     * Skin Temperature
     * @serial
     */
    private float skinTemp;

    /**
     * Acceleration in X-axis
     * @serial
     */
    private float accX;

    /**
     * Acceleration in Y-axis
     * @serial
     */
    private float accY;

    /**
     * Acceleration in Z-axis
     * @serial
     */
    private float accZ;

    /**
     * Body Position
     * @serial
     */
    private String bodyPos;

    /**
     * Ambulation.
     * @serial
     */
    private String amb;



    private static final long serialVersionUID = -8851726712666484197L;
    DataSnap(){}

    DataSnap(float ECGLead1, float ECGLead2, String BodyPosition, float HR, float BR, float skinTemperature, float AccX, float AccY, float AccZ, String Ambulation){
        QRS = ECGLead1 - ECGLead2;
        bodyPos = BodyPosition;
        heartRate = HR;
        breathingRate = BR;
        skinTemp = skinTemperature;
        accX = AccX;
        accY = AccY;
        accZ = AccZ;
        amb = Ambulation;
    }

    String print(){
        return  "QRS: " + QRS +
                " Position: " + bodyPos +
                " HR: " + heartRate +
                " BR: " + breathingRate +
                " SkinTemp: " + skinTemp +
                " AccX: " + accX +
                " AccY: " + accY +
                " AccZ: " + accZ +
                " Ambulation: " + amb;
    }

    private void readObject(
            ObjectInputStream aInputStream
    ) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        aInputStream.defaultReadObject();
    }

    /**
     * This is the default implementation of writeObject.
     * Customise if necessary.
     */
    private void writeObject(
            ObjectOutputStream aOutputStream
    ) throws IOException {
        //perform the default serialization for all non-transient, non-static fields
        aOutputStream.defaultWriteObject();
    }
}
