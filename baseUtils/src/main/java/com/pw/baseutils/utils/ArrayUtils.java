package com.pw.baseutils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayUtils {

    public static List<? extends Object> getArrayIntersection(List<? extends Object> arr1, List<? extends Object> arr2) {
        List<Object> result = new ArrayList<>();
        if (arr1 == null || arr2 == null || arr1.isEmpty() || arr2.isEmpty()) {
            return result;
        }
        Map<Object, Integer> valueMap = new HashMap<>();
        for (int i = 0; i < arr1.size(); i++) {
            Object obj = arr1.get(i);
            if (obj != null) {
                valueMap.put(obj, 1);
            }
        }
        for (int i = 0; i < arr2.size(); i++) {
            Object obj = arr2.get(i);
            if (obj != null) {
                Integer existCount = valueMap.get(obj);
                if (existCount != null && existCount > 0) {
                    result.add(obj);
                }
            }
        }
        return result;
    }

    public static List<? extends Object> getArrayUnionSet(List<? extends Object> arr1, List<? extends Object> arr2) {
        List<Object> result = new ArrayList<>();
        if (arr1 == null || arr2 == null || arr1.isEmpty() || arr2.isEmpty()) {
            return result;
        }
        result.addAll(arr1);
        for (int i = 0; i < arr2.size(); i++) {
            Object obj = arr2.get(i);
            if (obj != null) {
                if (!result.contains(obj)) {
                    result.add(obj);
                }
            }
        }
        return result;
    }

    public static boolean isArrayEnable(List<? extends Object> arr) {
        if (arr == null || arr.isEmpty()) {
            return false;
        }
        return true;
    }

}
