package com.QuizActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Data.MyServerData;
import com.Data.Question;
import com.MainActivity.MainActivity;
import com.example.andrei.multiplechoicequestions.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    Spinner spinCategory;
    EditText questionNr;
    ArrayList<String> allCategories;
    int totalQuestions;
    AlertDialog dialog;

    ViewPager pager;
    QuestionPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity_view_pager);

        allCategories = new ArrayList<String>(MyServerData.getInstance().getCategoryList());
        totalQuestions = MyServerData.getInstance().getTotalQuestions();

        //initialize category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,allCategories);
        spinCategory = (Spinner) findViewById(R.id.category);
        spinCategory.setAdapter(categoryAdapter);
        spinCategory.setSelection(0);

        //set Category spinner callback
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentQuestionRealCategory = MyServerData.getInstance().getQuestionCategory(pager.getCurrentItem());
                String selectedCategory = spinCategory.getItemAtPosition(position).toString();
                if (selectedCategory != currentQuestionRealCategory) {
                    int firstQuestionNumberFromCategory = MyServerData.getInstance().getFirstQuestionNumberFromCategory(selectedCategory);
                    ViewPager pager = (ViewPager) findViewById(R.id.qPager);
                    pager.setCurrentItem(firstQuestionNumberFromCategory, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //initialize number
        questionNr = (EditText) findViewById(R.id.dialog_question_number);
        questionNr.clearFocus();
        //set number callbacks
        questionNr.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    v.clearFocus();
                    CharSequence s = questionNr.getText();
                    if (!s.toString().isEmpty()) {
                        Integer questionNumber = Integer.parseInt(s.toString());
                        //avoids out of range indexes
                        if (questionNumber > totalQuestions + 1) {
                            questionNumber = totalQuestions;
                        }
                        if (questionNumber < 0) {
                            questionNumber = 1;
                        }
                        //create loopting effet
                        if (questionNumber == totalQuestions + 1) {
                            questionNumber = 1;
                            ((EditText)v).setText("1");
                        }
                        if (questionNumber == 0) {
                            questionNumber = totalQuestions;
                            ((EditText)v).setText(String.valueOf(totalQuestions));
                        }
                        ViewPager pager = (ViewPager) findViewById(R.id.qPager);
                        pager.setCurrentItem(questionNumber, false);
                        questionNr.clearFocus();
                    } else {
                        questionNr.setText("1");
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });
        //initialize pager
        pager = (ViewPager)findViewById(R.id.qPager);
        pagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1, false);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                Integer currentQuestion = pager.getCurrentItem();
                //change spinner
                String currentCategory = MyServerData.getInstance().getQuestionCategory(currentQuestion);
                int categoryPosition = MyServerData.getInstance().getCategoryList().indexOf(currentCategory);
                spinCategory.setSelection(categoryPosition);

                //change numberPicker
                if(currentQuestion <= 0){currentQuestion = totalQuestions;}
                if(currentQuestion > totalQuestions){currentQuestion = 1;}
                questionNr.setText(currentQuestion.toString());
                questionNr.clearFocus();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                int totalQuestions = MyServerData.getInstance().getTotalQuestions();
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (pager.getCurrentItem() == totalQuestions + 1) {
                        pager.setCurrentItem(1, false);
                    }
                    if (pager.getCurrentItem() == 0) {
                        pager.setCurrentItem(totalQuestions, false); // false will prevent sliding animation of view pager
                    }
                }
            }
        });

    }

    public void FinishTest(View v){

        //check if there are unanswered questions
        if(MyServerData.getInstance().getTestState() == "inProgress"){
            ArrayList<String> UnansweredQuestions = new ArrayList<>();
            LinkedHashMap<String,Object> allQuestions = MyServerData.getInstance().getAllQuestions();
            for(Map.Entry category: allQuestions.entrySet()){
                Question[] questions = (Question[])category.getValue();
                for(int i = 0; i < questions.length;i++){
                    Boolean[] userAnswers = questions[i].getUserAnswers();
                    if(!Arrays.asList(userAnswers).contains(true)){
                        String checkedCategory = (String)category.getKey();
                        Integer questionNumberInList = MyServerData.getInstance().getQuestionListNumber(checkedCategory,i);
                        UnansweredQuestions.add(String.valueOf(questionNumberInList));
                    }
                }
            }

            if(UnansweredQuestions.size() > 0){
                dialog = new AlertDialog.Builder(this)
                        .create();
                LayoutInflater infl = LayoutInflater.from(this);
                dialog.setView(infl.inflate(R.layout.dialog_message,null));
                dialog.show();
                TextView message = (TextView)dialog.findViewById(R.id.message);
                String unfinished = getResources().getString(R.string.unfinished_text);
                String questions =   TextUtils.join(",",UnansweredQuestions);
                message.setText(unfinished + "\n" + questions + ".");

                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showResults();
                    }
                });
            }else{showResults();}

        }else{
            showResults();
        }


    }

    public void showResults(){
        int animationDuration;
        if(MyServerData.getInstance().getTestState() == "finished"){
            animationDuration = 100;
        }else{
            animationDuration = 2000;
        }
        View mainView = LayoutInflater.from(this).inflate(R.layout.quiz_activity_show_results,null);
        LinearLayout mainContainer = (LinearLayout)mainView.findViewById(R.id.mainContainer);
        mainView.findViewById(R.id.btnMainMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish the test and go to main menu
                Intent Main = new Intent(getApplicationContext(),MainActivity.class);
                finish();
                startActivity(Main);
                MyServerData.getInstance().setTestState("notStarted");
                MyServerData.getInstance().clearAnswers();
                Toast.makeText(getBaseContext(),R.string.text_ended,Toast.LENGTH_LONG).show();
            }
        });
        mainView.findViewById(R.id.check_results).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        Question[] currentCategory;
        int totalCategoryQuestions;
        int correctCategoryQuestions;
        int totalCorrectQuestions = 0;
        //checking and adding each category
        for(String category: allCategories){
            View categoryContainer = LayoutInflater.from(this).inflate(R.layout.quiz_activity_category_results,null);
            TextView categoryName = (TextView)categoryContainer.findViewById(R.id.categoryName);
            //set name
            categoryName.setText(category);
            currentCategory = MyServerData.getInstance().getCategory(category);
            totalCategoryQuestions = currentCategory.length;
            //check answers
            correctCategoryQuestions = 0;
            for(int i=0; i < currentCategory.length;i++){
                Boolean isCorrect = Arrays.equals(currentCategory[i].getAllCorrectAnswers(),currentCategory[i].getUserAnswers());
                if(isCorrect){ correctCategoryQuestions++;}
            }
            totalCorrectQuestions += correctCategoryQuestions;

            //set results
            String result = String.valueOf(correctCategoryQuestions) + "/" + String.valueOf(totalCategoryQuestions);
            final ProgressBar progress = (ProgressBar)categoryContainer.findViewById(R.id.progressBar);
            progress.setMax(totalCategoryQuestions*100);
            final TextView myResult = (TextView)categoryContainer.findViewById(R.id.categoryResult);
            final String myResultText = "/" + String.valueOf(totalCategoryQuestions);
            ValueAnimator val = new ValueAnimator();
            val.setObjectValues(0, correctCategoryQuestions*100);
            val.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    myResult.setText(String.valueOf((Integer)animation.getAnimatedValue()/100) + myResultText);
                    progress.setProgress( ((Integer) animation.getAnimatedValue()));
                }
            });
            val.setDuration(animationDuration);
            val.start();

            mainContainer.addView(categoryContainer);
        }

        //animate results
        final TextView tvTotalResult =(TextView)mainView.findViewById(R.id.myTotalAnswers);
        final String totalResultS =  "/" + String.valueOf(totalQuestions);
        ValueAnimator totalResultsAnimator = new ValueAnimator();
        totalResultsAnimator.setObjectValues(0, totalCorrectQuestions);
        totalResultsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tvTotalResult.setText(String.valueOf(animation.getAnimatedValue()) + totalResultS);
            }
        });
        totalResultsAnimator.setDuration(animationDuration);
        totalResultsAnimator.start();

        MyServerData.getInstance().setTestState("finished");
        setContentView(mainView);
    }
}

