package com.example.andrei;

import com.Data.MyServerData;
import com.Data.Question;

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
public class getQuestionCategoryTest_withDummyData {
    @Parameterized.Parameters(name = "Params :{0}-{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0,"SecondCategory"}, {1,"FirstCategory"}, {2,"FirstCategory"}, {3, "SecondCategory"},
                {4,"SecondCategory"}, {5,"FirstCategory"}
        });
    }
    @Parameterized.Parameter
    public int myInput;
    @Parameterized.Parameter(value = 1)
    public String myExpected;

    @Test
    public void getCategoryByQuestionNumber_returnsQuestion(){
        assertEquals(MyServerData.getInstance().getQuestionCategory(myInput), myExpected);
    }

//    @Test
//    public void getQuestionTest(){
//        assertEquals(MyServerData.getInstance().getQuestion(myInput).getQuestionText(),MyServerData.getInstance().getQuestion(0).getQuestionText());
//    }

}

