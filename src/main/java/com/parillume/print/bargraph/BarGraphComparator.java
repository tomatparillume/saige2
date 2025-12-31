/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.parillume.print.bargraph;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author tmargolis
 * @author tom@parillume.com
 */
public class BarGraphComparator implements Comparator<BarGraphData> {

    private static final double[] WEIGHTS = {-4.0, -3.0, 3.0, 4.0};

    @Override
    public int compare(BarGraphData a, BarGraphData b) {
        int[] barA = a.getBarPercentages();
        int[] barB = b.getBarPercentages();
        
        int comp = Double.compare(
            weightedScore(barB),
            weightedScore(barA)
        );
        if(comp != 0)
            return comp;
                
        // Elite-first lexicographic (4 → 1)
        for (int i = 3; i >= 0; i--) {
            if (barA[i] != barB[i]) {
                return Integer.compare(barB[i], barA[i]);
            }
        }

        // Worst-case minimization (1 → 4)
        for (int i = 0; i < 4; i++) {
            if (barA[i] != barB[i]) {
                return Integer.compare(barA[i], barB[i]);
            }
        }
        
        return 0;
    }
    
    private double weightedScore(int[] bar) {
        double score = 0.0;
        for (int i = 0; i < bar.length; i++) {
            score += bar[i] * WEIGHTS[i];
        }
        return score;
    }
}
