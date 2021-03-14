package controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topquiz.R;

import java.util.Arrays;

import model.Question;
import model.QuestionBank;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mQuestionText;
    // The buttons will contains the different answers to the question
    private Button mFirstAnswer, mSecondAnswer, mThirdAnswer, mForthAnswer;

    private QuestionBank mQuestionBank;

    private Question mCurrentQuestion;

    private int mNumberOfQuestions;
    private int mScore;

    public int getScore() {
        return mScore;
    }

    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    public static final String BUNDLE_STATE_SCORE = "currentScore";
    public static final String BUNDLE_STATE_QUESTION = "currentQuestion";

    private boolean mEnableTouchEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mQuestionBank = this.generateQuestions();

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
        } else {
            mScore = 0;
            mNumberOfQuestions = 5;
        }

        mEnableTouchEvents = true;

        mQuestionText = (TextView) findViewById(R.id.activity_game_question_text);
        mFirstAnswer = (Button) findViewById(R.id.activity_game_answer1_btn);
        mSecondAnswer = (Button) findViewById(R.id.activity_game_answer2_btn);
        mThirdAnswer = (Button) findViewById(R.id.activity_game_answer3_btn);
        mForthAnswer = (Button) findViewById(R.id.activity_game_answer4_btn);

        // We set a tag to each answer so we can catch what answer the user clicked
        mFirstAnswer.setTag(0);
        mSecondAnswer.setTag(1);
        mThirdAnswer.setTag(2);
        mForthAnswer.setTag(3);

        // Use the same listener for all four buttons.
        mFirstAnswer.setOnClickListener(this);
        mSecondAnswer.setOnClickListener(this);
        mThirdAnswer.setOnClickListener(this);
        mForthAnswer.setOnClickListener(this);

        mCurrentQuestion = mQuestionBank.getQuestion();
        this.displayQuestion(mCurrentQuestion);

    }

    @Override
    public void onClick(View v) {

        int responseIndex = (int) v.getTag();

        if (responseIndex == mCurrentQuestion.getAnswerIndex()) {
            // Good answer
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            mScore++;

        } else {
            // Wrong answer
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;

                // If this is the last question, ends the game.
                // Else, display the next question.
                if (--mNumberOfQuestions == 0) {
                    // End the game
                    endGame();
                } else {
                    mCurrentQuestion = mQuestionBank.getQuestion();
                    displayQuestion(mCurrentQuestion);
                }
            }
        }, 1500); // LENGTH_SHORT is usually 1.5 second long

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    private String message(int score) {
        String msg;
        switch(score) {
            case 0:
                msg =  "Sorry! Not your lucky day!";
                break;
            case 1:
                msg =  "OUCH!!";
                break;
            case 2:
                msg = "Average! You can do more than that.";
                break;
            case 3:
                msg = "Nice job! Well Done!";
                break;
            case 4:
                msg = "SUCCESS!!";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + score);
        }
        
        return msg;
    }

    private void endGame() {
        // The score will be displayed to the user in a dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(message(mScore))
                .setMessage("Your score is " + mScore)
                .setPositiveButton("OKAY!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // End the activity
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                        setResult(RESULT_OK, intent);
                        finish();  // stop the activity and return to the main screen
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void displayQuestion(final Question question) {
        // Set the text for the question text view and the four buttons
        mQuestionText.setText(question.getQuestion());

        mFirstAnswer.setText(question.getChoiceList().get(0));
        mSecondAnswer.setText(question.getChoiceList().get(1));
        mThirdAnswer.setText(question.getChoiceList().get(2));
        mForthAnswer.setText(question.getChoiceList().get(3));
    }

    private QuestionBank generateQuestions() {

        Question question1 = new Question("Who created Android?",
                            Arrays.asList("Andy Rubin",
                                          "Steve Wozniak",
                                          "Jake Wharton",
                                          "Paul Smith"), 0);

        Question question2 = new Question("When did the first person land on the moon?",
                            Arrays.asList("1958",
                                          "1962",
                                          "1967",
                                          "1969"), 3);

        Question question3 = new Question("What is the house number of The Simpsons?",
                            Arrays.asList("42",
                                          "101",
                                          "666",
                                          "742"), 3);

        Question question4 = new Question("When Microsoft was founded?",
                            Arrays.asList("1974",
                                          "1975",
                                          "1976",
                                          "1977"), 1);

        Question question5 = new Question("How many characters did Twitter originally restrict users to?",
                            Arrays.asList("120",
                                          "140",
                                          "160",
                                          "180"), 1);

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5));
    }
}