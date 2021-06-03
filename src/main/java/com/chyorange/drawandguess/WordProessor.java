package com.chyorange.drawandguess;

import java.io.*;

public class WordProessor {

    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\liqi_\\IdeaProjects\\drawandguess\\src\\main\\resources\\word.txt";
            String out = "C:\\Users\\liqi_\\IdeaProjects\\drawandguess\\src\\main\\resources\\out.txt";
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            String s;
            String temp = "";
            while ((s = br.readLine())!=null){
                if(s.isEmpty()){
                    continue;
                }
                if(s.contains("题目")){
                    temp = temp.concat(s.substring(s.indexOf("：")+1)).concat(" ");
                }else if(s.contains("提示语")){
                    temp = temp.concat(s.substring(s.indexOf("：")+1,s.indexOf("、"))).concat(" ");
                    temp = temp.concat(s.substring(s.indexOf("、")+1));
                    bw.write(temp);
                    bw.newLine();
                    bw.flush();
                    temp="";
                }
            }
            br.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
