package mvmr.mvmr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import mvmr.mvmr.models.SurveyModel;
import mvmr.mvmr.models.UserModel;

public class Survey extends AppCompatActivity  implements SurveyQuestionFragment.OnFragmentInteractionListener {
    SharedPreferences surveyCache;
    String[] surveyQuestions;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        surveyCache = getSharedPreferences("MVMR_Survey", 0);
        surveyQuestions = getResources().getStringArray(R.array.survey);

        for (int i = 0; i < surveyQuestions.length; i++) {
            int result = surveyCache.getInt("question_" + i, 0);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            SurveyQuestionFragment fragment = SurveyQuestionFragment.newInstance(i, "Q" + (i + 1) + ". " + surveyQuestions[i] + "\n", result);
            fragmentTransaction.add(R.id.questionsContainer, fragment);
            fragmentTransaction.commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(int uri, int result) {
        SharedPreferences.Editor surveyCacheEditor = surveyCache.edit();
        surveyCacheEditor.putInt("question_" + uri, result);
        surveyCacheEditor.commit();
    }

    public void onSubmit(View view) {
        SharedPreferences settings = getSharedPreferences("MVMR", 0);
        SurveyModel model = new SurveyModel(settings.getString("user_id", null), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

        for(int i = 0; i < surveyQuestions.length; i++)
        {
            int value = surveyCache.getInt("question_" + i, 0);
            if(value == 0)
            {
                Toast.makeText(Survey.this, "Please Enter all values", Toast.LENGTH_SHORT).show();
                return;
            }
            model.Result += value + ",";
        }
        SharedPreferences.Editor surveyCacheEditor = surveyCache.edit();
        surveyCacheEditor.putString("question_Results", model.Result);
        surveyCacheEditor.commit();
        SendResults(model);
    }

    private void SendResults(final SurveyModel model)
    {
        final boolean[] triedSendEmail = {false};
        try {

            mAuth = FirebaseAuth.getInstance();
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Survey.this, "login success", Toast.LENGTH_SHORT).show();

                        String modelId = java.util.UUID.randomUUID().toString() + new SimpleDateFormat("_HH:mm:ss").format(new Date());

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("survey")
                                .child(modelId)
                                .setValue(model);
                        getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
                        if(EmailSender.IsOnline(Survey.this))
                        {
                            EmailSender.Send(Survey.this);
                        }
                    }
                    else{
                        Toast.makeText(Survey.this, "login failed", Toast.LENGTH_SHORT).show();
                        if(EmailSender.IsOnline(Survey.this))
                        {
                            triedSendEmail[0] = true;
                            EmailSender.Send(Survey.this);
                        }
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(Survey.this, "login error", Toast.LENGTH_SHORT).show();
            if(!triedSendEmail[0] && EmailSender.IsOnline(this))
            {
                EmailSender.Send(this);
            }
        }
    }
}
