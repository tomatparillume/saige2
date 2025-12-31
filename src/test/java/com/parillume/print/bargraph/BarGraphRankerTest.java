/*
 * Copyright(c) 2024, Billtrust Inc0, All rights reserved worldwide
 */
package com.parillume.print.bargraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author tmargolis
 * @author tom@parillume.com
 */
public class BarGraphRankerTest {
    @Test
    public void testRanking()
    throws Exception {  
        
        Map<Integer,BarGraphData> expectedOrderMap = new HashMap<>();
        expectedOrderMap.put(0, new BarGraphData(null,null,1, new int[]{0, 60, 10, 30}));
        expectedOrderMap.put(1, new BarGraphData(null,null,1, new int[]{0, 60, 20, 20}));
        expectedOrderMap.put(2, new BarGraphData(null,null,1, new int[]{20, 40, 10, 30}));
        expectedOrderMap.put(3, new BarGraphData(null,null,1, new int[]{0, 60, 30, 10}));
        expectedOrderMap.put(4, new BarGraphData(null,null,1, new int[]{20, 40, 20, 20}));
        expectedOrderMap.put(5, new BarGraphData(null,null,1, new int[]{10, 50, 30, 10}));
        expectedOrderMap.put(6, new BarGraphData(null,null,1, new int[]{0, 60, 40, 0}));
        expectedOrderMap.put(7, new BarGraphData(null,null,1, new int[]{20, 40, 30, 10}));
        expectedOrderMap.put(8, new BarGraphData(null,null,1, new int[]{40, 20, 20, 20}));
        expectedOrderMap.put(9, new BarGraphData(null,null,1, new int[]{60, 0, 20, 20}));        
        
        List<BarGraphData> barGraphs = new ArrayList(expectedOrderMap.values());
        
        Collections.shuffle(barGraphs);
        verify(barGraphs, expectedOrderMap);
        
        Collections.shuffle(barGraphs);
        verify(barGraphs, expectedOrderMap);
        
        Collections.shuffle(barGraphs);
        verify(barGraphs, expectedOrderMap);
        
        Collections.shuffle(barGraphs);
        verify(barGraphs, expectedOrderMap);
        
        Collections.shuffle(barGraphs);
        verify(barGraphs, expectedOrderMap);
    }  
    
    private void verify(List<BarGraphData> barGraphs, Map<Integer,BarGraphData> expectedOrderMap) {
        barGraphs.sort(new BarGraphComparator());
        
        int i = 0;
        for(BarGraphData data: barGraphs) {
            assertEquals(data, expectedOrderMap.get(i++));
        }   
    }
}
