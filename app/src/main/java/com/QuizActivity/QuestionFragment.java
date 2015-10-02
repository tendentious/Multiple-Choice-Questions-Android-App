package com.QuizActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Data.MyServerData;
import com.Data.Question;
import com.example.andrei.multiplechoicequestions.R;

/**
 * Created by Andrei on 9/6/2015.
 */
public class QuestionFragment extends Fragment {

    LinearLayout answersContainer;
    int currentPageNr;

    @Override
    public View onCreateView(LayoutInflater lInflater, ViewGroup container,Bundle saveInstanceState){
        currentPageNr = getArguments().getInt("position");
        //initialize some variables
        final Question currentQuestion = MyServerData.getInstance().getQuestion(currentPageNr);
        View rootView = lInflater.inflate(R.layout.quiz_activity_fragment,container,false);
        //initialize questionText
        TextView question = (TextView) rootView.findViewById(R.id.questionText);
        question.setMovementMethod(new ScrollingMovementMethod());
        question.setText(currentQuestion.getQuestionText());

        //initialize answers
        answersContainer = (LinearLayout)rootView.findViewById(R.id.answers_container);
        String[] answers = currentQuestion.getAllAnswersText();
        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            RelativeLayout checkboxContainer = (RelativeLayout)answersContainer.getChildAt(i);
            CheckBox cb = (CheckBox)checkboxContainer.getChildAt(0);
            Boolean current = currentQuestion.isChecked(i);
            cb.setMovementMethod(new ScrollingMovementMethod());
            cb.setText(answers[i]);
            if(MyServerData.getInstance().getTestState() == "finished"){
                cb.setEnabled(false);

                //check if answer is right or wrong
                if(currentQuestion.isCorrectAnswer(i)){
                    cb.setTextColor(Color.parseColor("#4DAD47"));
                } else {
                    cb.setTextColor(Color.parseColor("#CE0B0B"));
                    cb.setTypeface(null, Typeface.BOLD);
                }
            }else{
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = ((CheckBox) v);
                        int number = Integer.parseInt(cb.getHint().toString());
                        currentQuestion.setChecked(number, cb.isChecked());
                    }
                });

            }
        }
        return rootView;
    }
}
