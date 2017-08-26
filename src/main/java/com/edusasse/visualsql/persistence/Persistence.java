package com.edusasse.visualsql.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Persistence {

    public static void saveFile(String fileName, String data)
            throws IOException {


        String tmp = "";
        String ext[] = fileName.split("\\.");
        int i = ext.length;

        if (i > 1) {
            tmp = ext[i - 1];
        }


        if (tmp != null) {
            if (!tmp.equals("xml")) {
              
                fileName = fileName.trim() + ".xml";
              
            }
        }
        FileWriter fw = new FileWriter(fileName);

        fw.write(data);
        fw.flush();
        fw.close();
    }

    public static String openFile(File file)
            throws FileNotFoundException, IOException {


        if (!file.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
        StringBuffer bufSaida = new StringBuffer();

        String linha;
        while ((linha = br.readLine()) != null) {
            bufSaida.append(linha + "\n");
        }


        br.close();
        return bufSaida.toString();
    }
}