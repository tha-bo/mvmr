package mvmr.mvmr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
        EditText schoolText = (EditText) findViewById(R.id.school);
        EditText candidateText = (EditText) findViewById(R.id.candidateId);

        Spinner gradeSpinner = (Spinner) findViewById(R.id.grade);
        gradeSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                surveyCache.edit().putInt("grade", (int)(parent).getSelectedItemId()).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        schoolText.setText(surveyCache.getString("school", null));
        candidateText.setText(surveyCache.getString("candidate", null));
        gradeSpinner.setSelection(surveyCache.getInt("grade", 0));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(int uri, int result) {
        SharedPreferences.Editor surveyCacheEditor = surveyCache.edit();
        surveyCacheEditor.putInt("question_" + uri, result);
        surveyCacheEditor.commit();
        HideKeyboad();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor surveyCacheEditor = surveyCache.edit();
        EditText schoolText = (EditText) findViewById(R.id.school);
        if(schoolText.getText() != null)
            surveyCacheEditor.putString("school", schoolText.getText().toString()).commit();

        EditText candidateText = (EditText) findViewById(R.id.candidateId);
        if(candidateText.getText() != null)
            surveyCacheEditor.putString("candidate", candidateText.getText().toString()).commit();
    }

    public void onSubmit(View view) {
        SharedPreferences settings = getSharedPreferences("MVMR", 0);
        SurveyModel model = new SurveyModel(settings.getString("user_id", null), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()), "");

        EditText schoolText = (EditText) findViewById(R.id.school);
        EditText candidateText = (EditText) findViewById(R.id.candidateId);

        Spinner gradeSpinner = (Spinner) findViewById(R.id.grade);

        if(schoolText.getText().toString() == null || schoolText.getText().toString().equals(""))
        {
            Toast.makeText(Survey.this, "Please enter your school", Toast.LENGTH_SHORT).show();
            return;
        }

        if(gradeSpinner.getSelectedItemId() == 0)
        {
            Toast.makeText(Survey.this, "Please enter your grade", Toast.LENGTH_SHORT).show();
            return;
        }

        if(candidateText.getText().toString() == null || candidateText.getText().toString().equals("")|| candidateText.getText().toString().length() < 6)
        {
            Toast.makeText(Survey.this, "Please enter your candidate ID", Toast.LENGTH_SHORT).show();
            return;
        }

        model.School = schoolText.getText().toString();
        model.Grade = gradeSpinner.getSelectedItem().toString();
        model.CandidateId = candidateText.getText().toString();

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
        surveyCacheEditor.putString("school", schoolText.getText().toString());
        surveyCacheEditor.putString("candidate", candidateText.getText().toString());
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
//                        Toast.makeText(Survey.this, "login success", Toast.LENGTH_SHORT).show();

                        String modelId = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();

                        mDatabase = FirebaseRepository.getDatabaseInstance().getReference();
                        mDatabase.child("survey")
                                .child(model.User)
                                .setValue(model);

                        if(EmailSender.IsOnline(Survey.this))
                        {
                            EmailSender.SendSurvey(Survey.this, model);
                        }
                        surveyCache.edit().clear().commit();
                        getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
                        ShowResult(true);
                    }
                    else{
//                        Toast.makeText(Survey.this, "login failed", Toast.LENGTH_SHORT).show();
                        if(EmailSender.IsOnline(Survey.this))
                        {
                            triedSendEmail[0] = true;
                            EmailSender.SendSurvey(Survey.this, model);
                            surveyCache.edit().clear().commit();
                            getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
                            ShowResult(true);
                        }
                        else
                        {
                            Toast.makeText(Survey.this, "Send Survey Error", Toast.LENGTH_SHORT).show();
                            ShowResult(false);
                        }
                    }
                }
            });

        } catch (Exception e) {
//            Toast.makeText(Survey.this, "login error", Toast.LENGTH_SHORT).show();
            if(!triedSendEmail[0] && EmailSender.IsOnline(this))
            {
                EmailSender.SendSurvey(this, model);
                surveyCache.edit().clear().commit();
                getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
                ShowResult(true);
            }

            else
            {
                Toast.makeText(Survey.this, "Send Survey Error", Toast.LENGTH_SHORT).show();
                ShowResult(false);
            }
        }
    }

    private void ShowResult(boolean isSuccess)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if(isSuccess)
            builder1.setMessage("Your survey has been received.");
        else
            builder1.setMessage("We have failed to send your survey, please check your network connection or try again later. We' ll save your responses so you can pick up where you left off");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Survey.this.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void HideKeyboad(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((getCurrentFocus() == null) ? null : getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
