package test.java;


import Lesson6.Main;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.*;

import java.util.Arrays;
import java.util.Collection;
import  org.junit.Assert;

@RunWith(value = Parameterized.class)
public class ArraysMethodTest {
    private static Main main = null;

    private int[] inArrays;
    private int[] outArrays;
    //private int[] res;
    //private int[] arr7 = new int[]{1,2,3};

    public ArraysMethodTest(int[] inArrays, int[] outArrays) {
        this.inArrays = inArrays;
        this.outArrays = outArrays;
       // this.res = res;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[] {0, 1, 2, 3, 4, 5, 6, 7}, new int[] {5, 6, 7}},
                {new int[] {0, 1, 2, 4, 4, 4, 4, 7}, new int[] {7}},
                {new int[] {0, 1, 2, 3, 4}, new int[0]},
                {new int[] {0, 1, 2, 3, 4, 5, 4, 7}, new int[] {7}},
                {new int[] {4, 1, 2, 3, 1, 5, 6, 7}, new int[] {1, 2, 3, 1, 5, 6, 7}},
        });
    }

    @Test
    public void ArrayEqualsTest() { Assert.assertArrayEquals(main.newArrayLaterNumber(inArrays), outArrays);}


    @Before
    public void init() {
        System.out.println("init main");
        main = new Main();
    }

    @After
    public void tearDown() throws Exception {
        main = null;
    }

}
