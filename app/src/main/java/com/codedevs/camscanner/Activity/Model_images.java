package com.codedevs.camscanner.Activity;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Model_images implements Serializable{
    public static Comparator<Model_images> comparator = new Comparator<Model_images>(){

        public int compare(Model_images mi1,Model_images mi2){
            String S1 = mi1.getStr_folder().toUpperCase();
            String S2 = mi2.getStr_folder().toUpperCase();

            Log.e("s1 :" + S1,"s2" + S2);
            //ascending order
            return S1.compareTo(S2);
        }
    };

    public static Comparator<Model_images> comparatorDate = new Comparator<Model_images>(){

        public int compare(Model_images mi1,Model_images mi2){
            String S1 = mi1.getDate1().toUpperCase();
            String S2 = mi2.getDate1().toUpperCase();

//            Log.e("Date s1 :" +S1,"s2" + S2);
            //ascending order
            return S1.compareTo(S2);
        }
    };


    String str_folder;
    ArrayList<String> al_imagepath;
    String date1;
    String bucketID;

    public String getBucketID(){
        return bucketID;
    }

    public void setBucketID(String bucketID){
        this.bucketID = bucketID;
    }

    public String getStr_folder(){
        return str_folder;
    }

    public void setStr_folder(String str_folder){
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath(){
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath){
        this.al_imagepath = al_imagepath;
    }

    public String getDate1(){
        return date1;
    }

    public void setDate1(String date1){
        this.date1 = date1;
    }
}
