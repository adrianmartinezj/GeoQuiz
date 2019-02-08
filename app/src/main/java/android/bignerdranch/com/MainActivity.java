package android.bignerdranch.com;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String ANSWER_BANK = "answer_bank";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };
    //0 is null, 1 is true, 2 is false.
    private ArrayList<Integer> mAnswerBank = new ArrayList<Integer>(
            Arrays.asList(0,0,0,0,0,0));

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mCheatCount = 3;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntegerArrayList(ANSWER_BANK, mAnswerBank);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate(Bundle)");
        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswerBank = savedInstanceState.getIntegerArrayList(ANSWER_BANK);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Does nothing yet, but soon!
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Does nothing yet, but soon!
                checkAnswer(false);
            }
        });
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mQuestionBank.length + mCurrentIndex - 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                int cheatCount = mCheatCount;
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue, cheatCount);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
        }
        mIsCheater = CheatActivity.wasAnswerShown(data);
        mCheatCount = CheatActivity.getCheatCount(data);
    }

    private void updateQuestion() {
        Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mAnswerBank.get(mCurrentIndex) != 0){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
        else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
            mAnswerBank.set(mCurrentIndex % mQuestionBank.length, 3);
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mAnswerBank.set(mCurrentIndex % mQuestionBank.length, 1);

            } else {
                messageResId = R.string.incorrect_toast;
                mAnswerBank.set(mCurrentIndex % mQuestionBank.length, 2);
            }
        }
        if (mAnswerBank.get(mCurrentIndex) != 0){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        if (!mAnswerBank.contains(0)){
            gradeQuiz();
        }
    }

    private void gradeQuiz() {
        double totalRight = 0;
        for (int a:
             mAnswerBank) {
            if (a == 1)
                totalRight++;
        }
        double percentCalc = (totalRight / mAnswerBank.size()) * 100;
        String s = "You got a " + percentCalc + "%!";
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
