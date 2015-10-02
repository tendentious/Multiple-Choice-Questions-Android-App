package com.example.andrei;

import com.Data.MyServerData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Andrei on 9/13/2015.
 */
@RunWith(Parameterized.class)
public class getQuestionNumberTest_withDummyData {
    @Parameterized.Parameters(name = "Params :{0}-{1}-{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1,"SecondCategory"}, {1, 0,"FirstCategory"}, {2, 1,"FirstCategory"}, {3, 0,"SecondCategory"},
                {4, 1,"SecondCategory"}, {5,0,"FirstCategory"}
        });
    }
    @Parameterized.Parameter
    public int myInput;
    @Parameterized.Parameter(value = 1)
    public int myExpected;
    @Parameterized.Parameter(value = 2)
    public String myCategory;

    @Test
    public void getQuestionByNumber_returnsQuestion(){
        assertEquals(MyServerData.getInstance().getQuestion(myInput).getQuestionText(), MyServerData.getInstance().getCategory(myCategory)[myExpected].getQuestionText());
      }

}

