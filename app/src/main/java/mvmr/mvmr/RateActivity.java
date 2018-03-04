package mvmr.mvmr;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import mvmr.mvmr.models.RatingsModel;
import mvmr.mvmr.models.SurveyModel;

public class RateActivity extends AppCompatActivity implements RatingFragment.OnFragmentInteractionListener{
    String[] ratingsQuestions;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;
    SharedPreferences ratingsCache;
    EditText interfaceC = null;
    EditText access = null;
    EditText report = null;
    EditText howto = null;
    EditText about = null;
    EditText general = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingsCache = getSharedPreferences("MVMR_Ratings", 0);
        ratingsQuestions = getResources().getStringArray(R.array.ratings);

        for (int i = 0; i < ratingsQuestions.length; i++) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            RatingFragment fragment = RatingFragment.newInstance(i, ratingsQuestions[i] + "\n", 0);
            fragmentTransaction.add(R.id.ratingsContainer, fragment);
            fragmentTransaction.commit();
        }
        interfaceC = (EditText) findViewById(R.id.interfaceInput);
        access = (EditText) findViewById(R.id.accessInput);
        report = (EditText) findViewById(R.id.reportInput);
        howto = (EditText) findViewById(R.id.howToInput);
        about = (EditText) findViewById(R.id.aboutInput);
        general = (EditText) findViewById(R.id.generalInput);

        interfaceC.setText(ratingsCache.getString("interface", null));
        access.setText(ratingsCache.getString("access", null));
        report.setText(ratingsCache.getString("report", null));
        howto.setText(ratingsCache.getString("howto", null));
        about.setText(ratingsCache.getString("about", null));
        general.setText(ratingsCache.getString("general", null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(int uri, int result) {
        SharedPreferences.Editor surveyCacheEditor = ratingsCache.edit();
        surveyCacheEditor.putInt("question_" + uri, result);
        surveyCacheEditor.commit();
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor ratingsCacheEditor = ratingsCache.edit();


        if(interfaceC.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();
        if(access.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();
        if(report.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();
        if(howto.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();
        if(about.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();
        if(general.getText() != null)
            ratingsCacheEditor.putString("school", interfaceC.getText().toString()).commit();

    }

    public void onSubmit(View view) {
        SharedPreferences settings = getSharedPreferences("MVMR", 0);
        RatingsModel model = new RatingsModel();
        String ratings = "";

        for(int i = 0; i < ratingsQuestions.length; i++) {
            int value = ratingsCache.getInt("question_" + i, 0);
            ratings += value + ",";
        }
        model.Interface = interfaceC.getText().toString();
        model.Accessability = access.getText().toString();
        model.HowTo = howto.getText().toString();
        model.About = about.getText().toString();
        model.Report = report.getText().toString();
        model.General = general.getText().toString();
        model.Ratings = ratings;
        model.User = settings.getString("user_id", null);
        SendResults(model);
    }

    private void SendResults(final RatingsModel model)
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
                        mDatabase.child("ratings")
                                .child(model.User)
                                .setValue(model);

                        if(EmailSender.IsOnline(RateActivity.this))
                        {
                            EmailSender.SendRatings(RateActivity.this, model);
                        }
                        ratingsCache.edit().clear().commit();
                        ShowResult(true);
                    }
                    else{
//                        Toast.makeText(Survey.this, "login failed", Toast.LENGTH_SHORT).show();
                        if(EmailSender.IsOnline(RateActivity.this))
                        {
                            triedSendEmail[0] = true;
                            EmailSender.SendRatings(RateActivity.this, model);
                            ratingsCache.edit().clear().commit();
                            ShowResult(true);
                        }
                        else
                        {
                            Toast.makeText(RateActivity.this, "Send Ratings Error", Toast.LENGTH_SHORT).show();
                            ShowResult(false);
                        }
                    }
                }
            });

        } catch (Exception e) {
//            Toast.makeText(Survey.this, "login error", Toast.LENGTH_SHORT).show();
            if(!triedSendEmail[0] && EmailSender.IsOnline(this))
            {
                EmailSender.SendRatings(this, model);
                ratingsCache.edit().clear().commit();
                ShowResult(true);
            }

            else
            {
                Toast.makeText(RateActivity.this, "Send Ratings Error", Toast.LENGTH_SHORT).show();
                ShowResult(false);
            }
        }
    }

    private void ShowResult(boolean isSuccess)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if(isSuccess)
            builder1.setMessage("Your ratings have been received.");
        else
            builder1.setMessage("We have failed to send ratings, please check your network connection or try again later. We' ll save your responses so you can pick up where you left off");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        RateActivity.this.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
