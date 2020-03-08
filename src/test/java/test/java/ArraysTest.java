package test.java;

import Lesson6.Main;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ArraysTest {

    private static Main main = null;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[] {0, 1, 2, 3}},
                {new int[] {0, 1}},
                {new int[] {0, 1, 2, 3, 3, 3, 3, 3}},
                {new int[] {0, 1, 2, 3, 5, 4, 7}},
                {new int[] {4, 1, 2, 3, 1, 5, 6, 7}},
        });
    }

    private int[] data;

    public ArraysTest(int[] data) {
        this.data = data;
    }

    @Test(expected = RuntimeException.class)
    public void ArrayEqualsTest() { main.newArrayLaterNumber(data); }

}
