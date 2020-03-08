package Lesson6;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println(Arrays.toString( newArrayLaterNumber(new int[]{0,1,2,3,4,5,5,5,5})));
        System.out.println(equals1or4NumberArrays(new int[]{0,1,2,3,4,5,5,5,5}));
    }

    public static int[] newArrayLaterNumber(int[] arr) {

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                return Arrays.copyOfRange(arr, i + 1, arr.length);
            }
        }
        throw new RuntimeException("Arrays without 4");

    }

    public static boolean equals1or4NumberArrays(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 4 || arr[i] == 1){
                return true;
            }
        }
        return false;
    }
}
