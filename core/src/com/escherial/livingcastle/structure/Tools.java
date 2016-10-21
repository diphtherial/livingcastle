package com.escherial.livingcastle.structure;

public class Tools {
    public static int[] range(int from, int to) {
        int arr[] = new int[Math.abs(to - from)];
        int shift = (from < to) ? 1 : -1;

        for (int v = from, i = 0; v != to && i < arr.length; v += shift, ++i) {
            arr[i] = v;
        }

        return arr;
    }
}
