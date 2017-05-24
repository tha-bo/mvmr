package mvmr.mvmr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import java.util.Calendar;
import java.util.Date;

import mvmr.mvmr.models.ReportModel;
import mvmr.mvmr.models.SurveyModel;

public class Report extends AppCompatActivity implements DatePickerFragment.OnDatePickerInteractionListener {
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;
    SharedPreferences reportCache;

    Calendar incidentDate = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reportCache = getSharedPreferences("MVMR_Report", 0);

        EditText r_date = (EditText) findViewById(R.id.report_date);
        r_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        Button btnSubmit = (Button) findViewById(R.id.report_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit(view);
            }
        });

        EditText r_description = (EditText) findViewById(R.id.report_description);
        r_description.setOnFocusChangeListener(  new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && ((EditText)v).getText() != null);
                {
                    reportCache.edit().putString("description", ((EditText)v).getText().toString()).commit();
                }
            }
        });

        Spinner r_platform = (Spinner) findViewById(R.id.report_platform);
//        r_platform.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                reportCache.edit().putInt("platform", (int)((Spinner)view).getSelectedItemId()).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        Spinner r_victim = (Spinner) findViewById(R.id.report_victim);
//        r_victim.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                reportCache.edit().putInt("victim", (int)((Spinner)view).getSelectedItemId()).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        r_date.setText(reportCache.getString("date", null));
        r_description.setText(reportCache.getString("description", null));
        r_platform.setSelection(reportCache.getInt("platform", 0));
        r_victim.setSelection(reportCache.getInt("victim", 0));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDatePickerInteraction(int year, int month, int day) {
        incidentDate.set(year, month, day);//calender is 0 based
        EditText r_date = (EditText) findViewById(R.id.report_date);
        String date = new SimpleDateFormat("EEEE d MMM, yyyy").format(incidentDate.getTime());
        r_date.setText(date);
        reportCache.edit().putString("date", date).commit();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onSubmit(View v)
    {
        EditText r_date = (EditText) findViewById(R.id.report_date);
        EditText r_description = (EditText) findViewById(R.id.report_description);
        Spinner r_platform = (Spinner) findViewById(R.id.report_platform);
        Spinner r_victim = (Spinner) findViewById(R.id.report_victim);

        if(r_date.getText().toString() == null || r_date.getText().toString().equals("") ||
           r_description.getText().toString() == null || r_description.getText().toString().equals("") ||
           r_platform.getSelectedItemId() == 0 || r_victim.getSelectedItemId() == 0 )
        {
            Toast.makeText(Report.this, "Please enter all values", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences settings = getSharedPreferences("MVMR", 0);
        ReportModel model = new ReportModel(settings.getString("user_id", null),
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()),
                r_description.getText().toString(),
                r_platform.getSelectedItem().toString(),
                r_victim.getSelectedItem().toString()
        );
        SendResults(model);
    }

    private void SendResults(final ReportModel model)
    {
        final boolean[] triedSendEmail = {false};
        try {

            mAuth = FirebaseAuth.getInstance();
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Report.this, "login success", Toast.LENGTH_SHORT).show();

                        String modelId = java.util.UUID.randomUUID().toString() + new SimpleDateFormat("_HH:mm:ss").format(new Date());

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("survey")
                                .child(modelId)
                                .setValue(model);
                        getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
                        if(EmailSender.IsOnline(Report.this))
                        {
                            EmailSender.SendReport(Report.this, model);
                        }
                        reportCache.edit().clear().commit();
                    }
                    else{
                        Toast.makeText(Report.this, "login failed", Toast.LENGTH_SHORT).show();
                        if(EmailSender.IsOnline(Report.this))
                        {
                            triedSendEmail[0] = true;
                            EmailSender.SendReport(Report.this, model);
                            reportCache.edit().clear().commit();
                        }
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(Report.this, "login error", Toast.LENGTH_SHORT).show();
            if(!triedSendEmail[0] && EmailSender.IsOnline(this))
            {
                EmailSender.SendReport(this, model);
                reportCache.edit().clear().commit();
            }
            else
            {
                Toast.makeText(Report.this, "Send Report Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
