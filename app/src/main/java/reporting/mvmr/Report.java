package reporting.mvmr;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import reporting.mvmr.models.ReportModel;

public class Report extends AppCompatActivity implements DatePickerFragment.OnDatePickerInteractionListener {
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;
    SharedPreferences reportCache;
    final int galleryRequestCode = 1;
    Uri _imageUri = null;

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
        EditText r_contactDetails = (EditText) findViewById(R.id.report_contactDetails);

        Spinner r_platform = (Spinner) findViewById(R.id.report_platform);
        r_platform.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportCache.edit().putInt("platform", (int)(parent).getSelectedItemId()).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner r_victim = (Spinner) findViewById(R.id.report_victim);
        r_victim.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportCache.edit().putInt("victim", (int)(parent).getSelectedItemId()).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton btnImage = (ImageButton) findViewById(R.id.report_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, galleryRequestCode);
            }
        });

        r_date.setText(reportCache.getString("date", null));
        r_description.setText(reportCache.getString("description", null));
        r_contactDetails.setText(reportCache.getString("contactDetails", null));
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == galleryRequestCode)
        {
            if (resultCode == RESULT_OK) {
                try {
                    _imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(_imageUri);
                    ImageButton btnImage = (ImageButton) findViewById(R.id.report_image);
                    Bitmap bitmap= BitmapFactory.decodeStream(imageStream );
                    btnImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(Report.this, "We could not save this image", Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(Report.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onSubmit(View v)
    {
        EditText r_date = (EditText) findViewById(R.id.report_date);
        EditText r_description = (EditText) findViewById(R.id.report_description);
        EditText r_contactDetails = (EditText) findViewById(R.id.report_contactDetails);
        Spinner r_platform = (Spinner) findViewById(R.id.report_platform);
        Spinner r_victim = (Spinner) findViewById(R.id.report_victim);
        CheckBox r_inform = (CheckBox) findViewById(R.id.inform_school);

        if(r_date.getText().toString() == null || r_date.getText().toString().equals("") )
        {
            Toast.makeText(Report.this, "Please enter the date of occurrence", Toast.LENGTH_SHORT).show();
            return;
        }

        if(r_description.getText().toString() == null || r_description.getText().toString().equals(""))
        {
            Toast.makeText(Report.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if(r_platform.getSelectedItemId() == 0)
        {
            Toast.makeText(Report.this, "Please enter the platform of occurrence", Toast.LENGTH_SHORT).show();
            return;
        }

        if(r_victim.getSelectedItemId() == 0 )
        {
            Toast.makeText(Report.this, "Please enter who the victim was", Toast.LENGTH_SHORT).show();
            return;
        }

        //SharedPreferences settings = getSharedPreferences("MVMR", 0);
        TrayModule tray = new TrayModule(Report.this);
        ReportModel model = new ReportModel(tray.getString("user_id", null),
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()),
                r_description.getText().toString(),
                r_platform.getSelectedItem().toString(),
                r_victim.getSelectedItem().toString(),
                r_inform.isChecked(), r_contactDetails.getText().toString()
        );
        SendResults(model);
    }

    private void UploadImage(String reportId)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("images/"+reportId);
        UploadTask uploadTask = riversRef.putFile(_imageUri);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Report.this, "MVMR failed to upload the report image file. Please try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
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
//                        Toast.makeText(Report.this, "login success", Toast.LENGTH_SHORT).show();

                        String modelId = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();

                        if(_imageUri != null)
                        {
                            UploadImage(modelId);
                            model.HasImage = true;
                        }

                        mDatabase =FirebaseRepository.getDatabaseInstance().getReference();
                        mDatabase.child("report")
                                .child(modelId)
                                .setValue(model);

                        if(EmailSender.IsOnline(Report.this))
                        {
                            EmailSender.SendReport(Report.this, model);
                        }
                        reportCache.edit().clear().commit();
                        getSharedPreferences("MVMR", 0).edit().putInt("submittedReport", 1).commit();
                        ShowResult(true);
                    }
                    else{
//                        Toast.makeText(Report.this, "login failed", Toast.LENGTH_SHORT).show();
                        if(EmailSender.IsOnline(Report.this))
                        {
                            triedSendEmail[0] = true;
                            EmailSender.SendReport(Report.this, model);
                            reportCache.edit().clear().commit();
                            getSharedPreferences("MVMR", 0).edit().putInt("submittedReport", 1).commit();
                            ShowResult(true);
                        }

                        else
                        {
                            Toast.makeText(Report.this, "Send Report Error", Toast.LENGTH_SHORT).show();
                            ShowResult(false);
                        }
                    }
                }
            });

        } catch (Exception e) {
//            Toast.makeText(Report.this, "login error", Toast.LENGTH_SHORT).show();
            if(!triedSendEmail[0] && EmailSender.IsOnline(this))
            {
                EmailSender.SendReport(this, model);
                reportCache.edit().clear().commit();
                getSharedPreferences("MVMR", 0).edit().putInt("submittedReport", 1).commit();
                ShowResult(true);
            }
            else
            {
                Toast.makeText(Report.this, "Send Report Error", Toast.LENGTH_SHORT).show();
                ShowResult(false);
            }
        }
    }

    private void ShowResult(boolean isSuccess)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if(isSuccess)
            builder1.setMessage("Your report has been received.");
        else
            builder1.setMessage("We have failed to send your report, please check your network connection or try again later. We' ll save your responses so you can pick up where you left off");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        EditText r_description = (EditText) findViewById(R.id.report_description);
                        r_description.setText(null);
                        Report.this.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        EditText r_description = (EditText) findViewById(R.id.report_description);
        if(r_description.getText() != null)
            reportCache.edit().putString("description", r_description.getText().toString()).commit();
    }
}
