package mvmr.mvmr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import mvmr.mvmr.models.UserModel;

public class Home extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Button btnAbout = (Button) findViewById(R.id.about_button);
//        btnAbout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent i = new Intent(Home.this, AboutActivity.class);
//                startActivity(i);
//            }
//        });
//
//        Button btnSurvey = (Button) findViewById(R.id.survey_button);
//        btnSurvey.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent i = new Intent(Home.this, Survey.class);
//                startActivity(i);
//            }
//        });
//
//        Button btnSurvey2 = (Button) findViewById(R.id.survey_button2);
//        btnSurvey.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent i = new Intent(Home.this, Survey.class);
//                startActivity(i);
//            }
//        });
//
//        Button btnReport = (Button) findViewById(R.id.report_button);
//        btnReport.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent i = new Intent(Home.this, Report.class);
//                startActivity(i);
//            }
//        });
//
//        Button btnContacts = (Button) findViewById(R.id.contact_button);
//        btnContacts.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent i = new Intent(Home.this, Contacts.class);
//                startActivity(i);
//            }
//        });


        loadPage();
        HandleLogin();
        Intent i = new Intent(this, ListennerService.class);
        startService(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onResume() {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onResume();
        loadPage();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadPage();

    }

    private void loadPage()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentByTag("content");
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commitAllowingStateLoss();
        }

        settings = getSharedPreferences("MVMR", 0);
        if(settings.getInt("submittedSurvey", 0) == 0)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeNoSurveyFragment fragment = HomeNoSurveyFragment.newInstance();
            fragmentTransaction.add(R.id.home_container, fragment, "content");
            fragmentTransaction.commitAllowingStateLoss();
        }

        else
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeSurveyFragment fragment = HomeSurveyFragment.newInstance();
            fragmentTransaction.add(R.id.home_container, fragment, "content");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean appInstalledOrNot(String uri)
    {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }

    private void HandleLogin(){
        settings = getSharedPreferences("MVMR", 0);

        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = Home.this.settings.getString("user_id", null);
                        if (user_id == null) {
                            user_id = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();
                            SharedPreferences.Editor settingsEditor = settings.edit();
                            settingsEditor.putString("user_id", user_id);
                            settingsEditor.commit();
                            //get phone type and apps here
                            mDatabase = FirebaseRepository.getDatabaseInstance().getReference();

                            UserModel user = new UserModel(
                                    Build.MANUFACTURER,
                                    Build.MODEL,
                                    Build.VERSION.RELEASE + " | " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
                            );

                            String[] socialMedia = getResources().getStringArray(R.array.known_sm);

                            for (String sm : socialMedia) {
                                if (appInstalledOrNot(sm.split(",")[0])) {
                                    user.SocialMedia += sm.split(",")[1] + " ";
                                }
                            }

                            mDatabase.child("users").child(user_id).setValue(user);
                        }
                        return;
                    }
//                    Log.w("asdasdasd", "signInAnonymously:failure", task.getException());
                }
            });
        }

        catch(Exception e)
        {
            //experience shouldnt end;
//            Toast.makeText(this, "login error", Toast.LENGTH_SHORT).show();
        }
    }
}
