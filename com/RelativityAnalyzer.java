package com;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import mllib.Item;
import mllib.Kmeans;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linheng on 2016/5/30.
 */
public class RelativityAnalyzer {
    static Map<String, Country> lc = new HashMap<>();
    static Map<String, Map<String, Double>> Average = new HashMap<>();
    static Map<String, Map<String, Double>> MaxAll = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ReadFile();
//        WriteCountriesToExcel("E:\\result.xls");
        Cluster();
    }

    public static void WriteCountriesToExcel(String OutFilePath) throws IOException {
        try {
            File fileWrite = new File(OutFilePath);
            if (!fileWrite.exists()) fileWrite.createNewFile();
            OutputStream os;
            os = new FileOutputStream(fileWrite);
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            WritableSheet ws = wwb.createSheet("Pearson", 0);
            int row = 0;
            for (Map.Entry<String, Country> entry1 : lc.entrySet()) {
                Map<String, Map<String, Double>> pearson = entry1.getValue().GetPearson();
                int col = 1;
                int innerRow = row + 1;
                ws.addCell(new Label(0, row, entry1.getKey()));
                for (Map.Entry<String, Map<String, Double>> entry2 : pearson.entrySet()) {
                    ws.addCell(new Label(col, row, entry2.getKey()));
                    ws.addCell(new Label(0, innerRow, entry2.getKey()));
                    ++col;
                    ++innerRow;
                }
                ++row;
                for (Map.Entry<String, Map<String, Double>> entry2 : pearson.entrySet()) {
                    if (!Average.containsKey(entry2.getKey())) {
                        Average.put(entry2.getKey(), new HashMap<>());
                    }
                    if (!MaxAll.containsKey(entry2.getKey())) {
                        MaxAll.put(entry2.getKey(), new HashMap<>());
                    }
                    col = 1;
                    for (Map.Entry<String, Double> entry3 : entry2.getValue().entrySet()) {
                        double value = entry3.getValue();
                        Map<String, Double> msd1 = Average.get(entry2.getKey());
                        Map<String, Double> msd2 = MaxAll.get(entry2.getKey());
                        String entry3Key = entry3.getKey();
                        if (!msd1.containsKey(entry3Key)) {
                            msd1.put(entry3.getKey(), 0.0);
                        }
                        if (!msd2.containsKey(entry3Key)) {
                            msd2.put(entry3.getKey(), 0.0);
                        }
                        msd1.put(entry3Key, entry3.getValue() + msd1.get(entry3Key));
                        if (msd2.get(entry3Key) < entry3.getValue()) {
                            msd2.put(entry3Key, entry3.getValue());
                        }
                        Average.put(entry2.getKey(), msd1);
                        MaxAll.put(entry2.getKey(), msd2);
                        ws.addCell(new Number(col, row, value));
                        ++col;
                    }
                    ++row;
                }
                ++row;
            }
            ++row;
            int col = 1;
            int innerRow = row + 1;
            ws.addCell(new Label(0, row, "平均值"));
            for (Map.Entry<String, Map<String, Double>> entry : Average.entrySet()) {
                ws.addCell(new Label(col, row, entry.getKey()));
                ws.addCell(new Label(0, innerRow, entry.getKey()));
                ++col;
                ++innerRow;
            }
            ++row;
            for (Map.Entry<String, Map<String, Double>> entry1 : Average.entrySet()) {
                col = 1;
                for (Map.Entry<String, Double> entry2 : entry1.getValue().entrySet()) {
                    ws.addCell(new Number(col, row, entry2.getValue() / lc.size()));
                    ++col;
                }
                ++row;
            }
            ++row;
            col = 1;
            innerRow = row + 1;
            ws.addCell(new Label(0, row, "最大值"));
            for (Map.Entry<String, Map<String, Double>> entry : MaxAll.entrySet()) {
                ws.addCell(new Label(col, row, entry.getKey()));
                ws.addCell(new Label(0, innerRow, entry.getKey()));
                ++col;
                ++innerRow;
            }
            ++row;
            for (Map.Entry<String, Map<String, Double>> entry1 : MaxAll.entrySet()) {
                col = 1;
                for (Map.Entry<String, Double> entry2 : entry1.getValue().entrySet()) {
                    ws.addCell(new Number(col, row, entry2.getValue()));
                    ++col;
                }
                ++row;
            }
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ReadFile() {
        try {
            String FileName = "E:\\Platform Program\\Mytlab\\各国指标";
            File rf = new File(FileName);
            if (!rf.exists() && !rf.isDirectory()) {
                System.out.println(rf.getName() + " not exists");
            }
            File excelName[] = rf.listFiles();
            for (File itemName : excelName) {
                String itemPath = FileName + "\\" + itemName.getName();
                File file = new File(itemPath);
                if (file.isFile() && file.exists()) {
                    String Name=itemName.getName().substring(0,itemName.getName().lastIndexOf("."));
                    ReadFromExcel(itemPath, Name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ReadFromExcel(String InFilePath, String IndicatorName) {
        try {
            InputStream is = new FileInputStream(InFilePath);
            Workbook rwb = Workbook.getWorkbook(is);
            Sheet st = rwb.getSheet(0);
            int rowSize = st.getRows();
            for (int i = 4; i < rowSize; ++i) {
                String counName = st.getCell(0, i).getContents() + "(" + st.getCell(1, i).getContents() + ")";
                List<Double> indicator = new ArrayList<>();
                Country coun = new Country();
                Map<String, List<Double>> msld = new HashMap<>();
                if (lc.containsKey(counName)) {
                    coun = lc.get(counName);
                    msld = coun.indicators;
                }
                coun.Name = st.getCell(1, i).getContents();
                for (int j = 28; j < 59; ++j) {
                    if (st.getCell(j, i).getContents().equals(""))
                        indicator.add(0.0);
                    else indicator.add(Double.valueOf(st.getCell(j, i).getContents()));
                }
                msld.put(IndicatorName, indicator);
                coun.indicators = msld;
                lc.put(counName, coun);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Cluster() throws IOException {
        Kmeans kmeansCluster=new Kmeans();
        List<Item> li=new ArrayList<>();
        for (Map.Entry<String, Country> mesc :lc.entrySet()){
            Item item=new Item();
            List<Double> ld =mesc.getValue().indicators.get("专利申请-居民");
            for (int i=0;i<ld.size()-1;++i){
                if (ld.get(i) == 0){
                    item.vec.add(0.0);
                    continue;
                }
                double tp=(ld.get(i + 1) - ld.get(i)) / ld.get(i);
                if (tp == 0) item.vec.add(0.0);
                else item.vec.add(tp);
            }
            item.label=mesc.getValue().Name;
            li.add(item);
        }
        li=kmeansCluster.TestK(li, 4);
        int ClusterK = kmeansCluster.ClusterK;
        Map<Integer, List<Double>> patterns = new HashMap<>();
        for (int i = 0; i < ClusterK; ++i) {
            int size = 0;
            List<Double> ld = new ArrayList<>();
            for (Item item : li) {
                if (item.clusteredClass == i) {
                    if (ld.size() == 0) {
                        ld.addAll(item.vec);
                    } else {
                        for (int j = 0; j < ld.size(); ++j) {
                            ld.set(j, ld.get(j) + item.vec.get(j));
                        }
                    }
                    size++;
                }
            }
            for (int j = 0; j < ld.size(); ++j) {
                ld.set(j, ld.get(j) / size);
            }
            patterns.put(size, ld);
        }
        WritePatternsToExcel("E:\\R1.xls",patterns);
    }

    public static void WritePatternsToExcel(String OutFilePath,Map<Integer,List<Double>> patterns) throws IOException {
        try {
            File fileWrite = new File(OutFilePath);
            if (!fileWrite.exists()) fileWrite.createNewFile();
            OutputStream os;
            os = new FileOutputStream(fileWrite);
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            WritableSheet ws = wwb.createSheet("Patterns", 0);
            int row = 0;
            for (Map.Entry<Integer,List<Double>> entry1 : patterns.entrySet()) {
                if (row==0){
                    ws.addCell(new Label(0, row, "Pattern"));
                    int col = 1;
                    for (int i=1985;i<(entry1.getValue().size()+1985);++i) {
                        ws.addCell(new Number(col, row, i));
                        ++col;
                    }
                    row++;
                }
                ws.addCell(new Number(0, row, entry1.getKey()));
                int col = 1;
                for (Double d:entry1.getValue()) {
                    ws.addCell(new Number(col, row, d));
                    ++col;
                }
                row++;
            }
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
