package test.java;

import Lesson6.Main;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class TestMethodArraysEquals1or4 {
    private static Main main = null;

    private int[] inArrays;
    private boolean res;

    public TestMethodArraysEquals1or4(int[] inArrays, boolean res) {
        this.inArrays = inArrays;
        this.res = res;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[] {0, 1, 2, 3, 4, 5, 6, 7}, true},
                {new int[] {0, 1, 2, 4, 4, 4, 4, 7}, true},
                {new int[] {0, 1, 2, 3, 4}, true},
                {new int[] {0, 2, 3, 5, 4, 7},false},
                {new int[] {2, 3, 1, 5, 6, 7}, false}
        });
    }

    @Test
    public void EqualsTest() { Assert.assertEquals(main.equals1or4NumberArrays(inArrays), res);}

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
