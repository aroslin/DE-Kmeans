package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linheng on 2016/5/30.
 */
public class Country {
    String Name;
    Map<String, List<Double>> indicators;

    public Country() {
        this.Name = "";
        this.indicators = new HashMap<>();
    }

    public Map<String, Map<String, Double>> GetPearson() {
        Map<String, Map<String, Double>> pearson = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry1 : indicators.entrySet()) {
            Map<String, Double> msd = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry2 : indicators.entrySet()) {
                if (entry1.getKey().equals(entry2.getKey())) {
                    msd.put(entry2.getKey(), 1.0);
                } else {
                    List<Double> ld1 = entry1.getValue();
                    List<Double> ld2 = entry2.getValue();
                    int N = ld1.size();
                    double sumX = 0;
                    double sumY = 0;
                    double sumDoubleX = 0;
                    double sumDoubleY = 0;
                    double sumXY = 0;
                    for (int j = 0; j < ld2.size(); ++j) {
                        double Y = ld2.get(j);
                        sumY += Y;
                        sumDoubleY += Y * Y;
                    }
                    for (int i = 0; i < ld1.size(); ++i) {
                        double X = ld1.get(i);
                        sumX += X;
                        sumDoubleX += X * X;
                        for (int j = 0; j < ld2.size(); ++j) {
                            if(i==j){
                            double Y = ld2.get(j);
                            sumXY += X * Y;
                            }
                        }
                    }
                    if ((N * sumDoubleX - sumX * sumX) * (N * sumDoubleY - sumY * sumY)==0){
                        msd.put(entry2.getKey(),0.0);
                    }
                    else{
                        double r = (N * sumXY - sumX * sumY) / Math.sqrt(
                                (N * sumDoubleX - sumX * sumX) * (N * sumDoubleY - sumY * sumY));
                        msd.put(entry2.getKey(),r);
                    }
                }
            }
            pearson.put(entry1.getKey(),msd);
        }
        return pearson;
    }
}
