/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.tools;

import com.mycompany.nxtb.api.XtbApi;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void save(String src, double[] data) throws IOException {
        FileWriter myWritter = new FileWriter(src);
        for (int i = 0; i < data.length; i++) {
            myWritter.write(String.valueOf(data[i]) + separator);
        }
        System.out.println("Save[" + src + "]");
        myWritter.close();

    }
    
        public void save(String src, Double[] data) throws IOException {
        FileWriter myWritter = new FileWriter(src);
        for (int i = 0; i < data.length; i++) {
            myWritter.write(String.valueOf(data[i]) + separator);
        }
        //System.out.println("Save[" + src + "]");
        myWritter.close();

    }

    public void saveAppend(String src, double priceNetwork, double priceApi,String coment) throws IOException {
        String temp = new Memory().loadStringString(src);

        FileWriter myWritter = new FileWriter(src);

        String priceNetworkString=String.valueOf(priceNetwork).replace(".", ",");
        String priceApiString=String.valueOf(priceApi).replace(".", ",");
        
        
        if (temp != null) {
            if (!temp.contains("null")) {
                myWritter.append(temp);
            }
        }
        
        
        myWritter.write(priceNetworkString + ";" + priceApiString+";"+coment);
        myWritter.close();

    }
        public void saveAppend(String src, double toChar) throws IOException {
        String temp = new Memory().loadStringString(src);

        FileWriter myWritter = new FileWriter(src);

        String stringToChar=String.valueOf(toChar).replace(".", ",");
        
        
        if (temp != null) {
            if (!temp.contains("null")) {
                myWritter.append(temp);
            }
        }
        
        
        myWritter.write(stringToChar + ";");
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
    
    public void saveCSV(String src,double priceFromNetwork,double actualPrice,String coment) throws IOException{
    
            File dataDir = new File("data/csv");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

            new Memory().saveAppend(src, priceFromNetwork, actualPrice,coment);

    
    }
        public void saveCSV(String src,double toChar) throws IOException{
    
            File dataDir = new File("data/csv");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

            new Memory().saveAppend("data/csv/"+src+".csv", toChar);

    
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
             //System.out.println("Load[" + src + "]");
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

    public String loadStringString(String src) {
        String Load = null;
        BufferedReader br;
        FileReader fr;
        try {
            File file = new File(src);

            if (file.exists()) {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                StringBuffer sb = new StringBuffer();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                fr.close();
                Load = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Load;
    }

}
