package android.bignerdranch.com;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";

    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";

    private static final String CHEAT_COUNT =
            "com.bignerdranch.android.geoquiz.cheater_used";

    private static final String API_LEVEL =
             Integer.toString(Build.VERSION.SDK_INT);

    private boolean mAnswerIsTrue;
    private int mCheatCount;

    private TextView mAnswerTextView;
    private TextView mAPITextView;
    private Button mShowAnswerButton;

    public static Intent newIntent(Context packageContext,
                                   boolean answerIsTrue, int cheatCount){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(CHEAT_COUNT, cheatCount);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getCheatCount(Intent result){
        return  result.getIntExtra(CHEAT_COUNT, 3);
    }

//    public static int cheatCount(Intent result){
//        return result.getIntExtra(CHEAT_COUNT, 3);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAPITextView = (TextView) findViewById(R.id.api_text_view);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mCheatCount = getIntent().getIntExtra(CHEAT_COUNT, 3);

        Log.d("COMM", Integer.toString(mCheatCount));
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheatCount > 0) {
                    if (mAnswerIsTrue) {
                        mAnswerTextView.setText(R.string.true_button);
                    } else {
                        mAnswerTextView.setText(R.string.false_button);
                    }
                    mCheatCount--;
                    Log.d("COMM", "Changed .. " + Integer.toString(mCheatCount));

                    setAnswerShownResult(true, mCheatCount);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int cx = mShowAnswerButton.getWidth() / 2;
                        int cy = mShowAnswerButton.getHeight() / 2;
                        float radius = mShowAnswerButton.getWidth();
                        Animator anim = ViewAnimationUtils
                                .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mShowAnswerButton.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    } else {
                        mShowAnswerButton.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(CheatActivity.this, "You've used up all your cheats!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAPITextView.setText(mAPITextView.getText() + API_LEVEL);
    }

    private void setAnswerShownResult(boolean isAnswerShown, int cheatCount) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(CHEAT_COUNT, cheatCount);
        setResult(RESULT_OK,   data);
    }
}
