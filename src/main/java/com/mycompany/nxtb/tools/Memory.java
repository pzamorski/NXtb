/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author warsztat
 */
public class Memory {

    String separator = ",";

    public void save(String src, String data) throws IOException {
        FileWriter myWritter = new FileWriter(src);
        myWritter.write(data);
        System.out.println("Save[" + src + "]");
        myWritter.close();

    }

    public void save(String src, Double[] data) throws IOException {
        FileWriter myWritter = new FileWriter(src);
        for (int i = 0; i < data.length; i++) {
            myWritter.write(String.valueOf(data[i]) + separator);
        }
        System.out.println("Save[" + src + "]");
        myWritter.close();

    }

    public void save(String src, int[] data) throws IOException {
        FileWriter myWritter = new FileWriter(src);
        for (int i = 0; i < data.length; i++) {
            myWritter.write(String.valueOf(data[i]) + separator);
        }
        System.out.println("Save[" + src + "]");
        myWritter.close();

    }

    public double[] loadDouble(String src) {
        double[] arrayLoad = null;
        try {
            File file = new File(src);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            String[] arrayTemp = sb.toString().split(separator);
            arrayLoad = new double[arrayTemp.length];
            for (int i = 0; i < arrayTemp.length - 1; i++) {

                arrayLoad[i] = Double.valueOf(arrayTemp[i]);
            }
           // System.out.println("Load[" + src + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayLoad;
    }

    public int[] loadInt(String src) {
        int[] arrayLoad = null;
        try {
            File file = new File(src);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            String[] arrayTemp = sb.toString().split(separator);
            arrayLoad = new int[arrayTemp.length];
            for (int i = 0; i < arrayTemp.length - 1; i++) {

                arrayLoad[i] = Integer.valueOf(arrayTemp[i]);
            }
           // System.out.println("Load[" + src + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayLoad;
    }

    public String[] loadString(String src) {
        String[] arrayLoad = null;
        try {
            File file = new File(src);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            String[] arrayTemp = sb.toString().split(separator);
            arrayLoad = new String[arrayTemp.length];
            for (int i = 0; i < arrayTemp.length - 1; i++) {

                arrayLoad[i] = arrayTemp[i];
            }
            //System.out.println("Load[" + src + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayLoad;
    }

}
