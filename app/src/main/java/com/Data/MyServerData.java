package com.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Andrei on 9/4/2015.
 *  Singleton Pattern to store questions.
 */
public class MyServerData {
    private static MyServerData myInstance = null;
    private static LinkedHashMap<String, Object> myCategories;
    private int totalQuestions;

    //test states: notStarted, inProgress, finished
    private static String testState = "notStarted";


    protected MyServerData() {
        //initialize questions from server, shared preferences, files etc.
        //questions are hardcoded here for testing
        Question[] FirstCategory  = new Question[3];;
        Question[] SecondCategory = new Question[3];;
        String q = "What is the capital of France ?";
        String[] a = new String[3];
        Boolean[] r = new Boolean[3];
        a[0] = "Paris";
        a[1] = "Prague";
        a[2] = "Berlin";
        r[0] = true;
        r[1] = false;
        r[2] = false;

        Question question = new Question(q,a,r);
        FirstCategory[0] = question;
        q = "Which of the following is an OS ?";
        a[0] = "PHP";
        a[1] = "LINUX";
        a[2] = "WINDOWS";
        r[0] = false;
        r[1] = true;
        r[2] = true;
        question = new Question(q,a,r);
        FirstCategory[1] = question;
        q = "What number is the highest ?";
        a[0] = "12";
        a[1] = "14";
        a[2] = "18";
        r[0] = false;
        r[1] = false;
        r[2] = true;
        question = new Question(q,a,r);
        FirstCategory[2] = question;
        q = "Which of the following is flying ?";
        a[0] = "Bird";
        a[1] = "Lion";
        a[2] = "Bee";
        r[0] = true;
        r[1] = false;
        r[2] = true;
        SecondCategory[0] = new Question(q,a,r);
        q = "What material is the strongest ?";
        a[0] = "Wood";
        a[1] = "Metal";
        a[2] = "Diamond";
        r[0] = false;
        r[1] = false;
        r[2] = true;
        SecondCategory[1] = new Question(q,a,r);q = "What number is the lowest ?";
        a[0] = "15";
        a[1] = "11";
        a[2] = "12";
        r[0] = false;
        r[1] = true;
        r[2] = false;
        SecondCategory[2] = new Question(q,a,r);

        myCategories = new LinkedHashMap<String,Object>();
        myCategories.put("FirstCategory", FirstCategory);
        myCategories.put("SecondCategory", SecondCategory);
        for(String entry: myCategories.keySet()){
            totalQuestions += ((Question[])myCategories.get(entry)).length;
        }
    }


    public String getTestState() {
        return testState;
    }
    public void setTestState(String testState) {
        MyServerData.testState = testState;
    }

    public static synchronized  MyServerData getInstance(){
        if(myInstance == null){
            myInstance = new MyServerData();
        }
        return myInstance;

    }

    public LinkedHashMap<String, Object> getAllQuestions() {
        return myCategories;
    }
    public ArrayList<String> getCategoryList(){
        ArrayList<String> myList = new ArrayList<String>();
        myList.addAll(myCategories.keySet());
        return myList;
    }
    public Question[] getCategory(String category){
        return (Question[])myCategories.get(category);
    }
    public int getTotalQuestions() {
        return totalQuestions;
    }
    public Question getQuestion(int questionNr){

        //return last question if 0
        if(questionNr == 0){
            String[] keys = myCategories.keySet().toArray(new String[myCategories.size()]);
            String theLastKey = keys[keys.length-1];
            Question[] lastCategory = (Question[])myCategories.get(theLastKey);
            return lastCategory[lastCategory.length-1];
        }
        //return first question if index is the last +1
        if(questionNr == totalQuestions+1){
            String[] keys = myCategories.keySet().toArray(new String[myCategories.size()]);
            String theFirstKey = keys[0];
            Question[] lastCategory = (Question[])myCategories.get(theFirstKey);
            return ((Question[])myCategories.entrySet().iterator().next().getValue())[0];
        }
        //return question
        Iterator<Map.Entry<String,Object>> it = myCategories.entrySet().iterator();
        while(it.hasNext()){
            Question[] category = (Question[])it.next().getValue();
            if(questionNr <= category.length){
                return category[questionNr-1];
            }else{
                questionNr -= category.length;
            }
        }
        //if number is higher or lower than expected -> return first category first question
        return ((Question[])myCategories.entrySet().iterator().next().getValue())[0];
    }
    public String getQuestionCategory(int questionNr){
        Iterator<Map.Entry<String,Object>> it = myCategories.entrySet().iterator();
        String name = null;

        //return last category
        if(questionNr == 0){
           String[] keys = myCategories.keySet().toArray(new String[myCategories.keySet().size()]);
            return keys[keys.length-1];
        }
        if(questionNr == totalQuestions+1){
            String[] keys = myCategories.keySet().toArray(new String[myCategories.keySet().size()]);
            return keys[0];
        }
        while(it.hasNext()){
            Map.Entry entry = it.next();
            if(questionNr > ((Question[]) entry.getValue()).length){
                questionNr -= ((Question[]) entry.getValue()).length;
            }else{
                name = entry.getKey().toString();
                break;
            }
        }
        return name;
    }
    public int getFirstQuestionNumberFromCategory(String category){
        Iterator<Map.Entry<String,Object>> it = myCategories.entrySet().iterator();
        int questionNumber = 1;
        while(it.hasNext()){ 
            Map.Entry next = it.next();
            if(!next.getKey().equals(category)){
                questionNumber += ((Question[])next.getValue()).length;
            }else{
                break;
            }
        }
        return questionNumber;
    }
    public int getQuestionListNumber(String category,int questionNumber){
        if(questionNumber >= ((Question[])myCategories.get(category)).length){
            return 0;
        }
        questionNumber++;
        Iterator<Map.Entry<String,Object>> it = myCategories.entrySet().iterator();
        while(it.hasNext()){
            String checkedCategory = it.next().getKey();
            if(checkedCategory == category){
                return questionNumber;
            }else{
                questionNumber += ((Question[])myCategories.get(checkedCategory)).length;
            }
        }
        return questionNumber;
    }
    public void clearAnswers(){
        for(String currentKey : myCategories.keySet()){
            Question[] Category = (Question[]) myCategories.get(currentKey);
            for(int i = 0; i < Category.length;i++){
                Category[i].resetChecked();
            }
        }
    }
}
