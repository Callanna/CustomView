package com.cvlib.address;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Callanna on 2017/6/15.
 */

public class Province {
    @SerializedName("p")
    public String p;

    public void setP(String p) {
        this.p = p;
    }

    public String getP() {
        return p;
    }
    @SerializedName("c")
    ArrayList<City> c = new ArrayList<City>();

    public ArrayList<City> getCities() {
        return c;
    }

    public void setCities(ArrayList<City> cities) {
        this.c = cities;
    }


    public   class City  {
        @SerializedName("n")
        public String n;

        public void setN(String n) {
            this.n = n;
        }

        public String getN() {
            return n;
        }
        @SerializedName("a")
        private ArrayList<County> a = new ArrayList<County>();

        /**
         * Gets counties.
         *
         * @return the counties
         */
        public ArrayList<County> getCounties() {
            return a;
        }

        /**
         * Sets counties.
         *
         * @param counties the counties
         */
        public void setCounties(ArrayList<County> counties) {
            this.a = counties;
        }

    }

    /**
     * The type County.
     */
    public   class County   {
        @SerializedName("s")
        public String s;

        public void setS(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }
    }
}
