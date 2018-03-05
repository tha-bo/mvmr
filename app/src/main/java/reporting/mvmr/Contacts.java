package reporting.mvmr;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Contacts extends AppCompatActivity {

    String[] email;
    String[] web;
    String[] sms;
    String[] phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = getResources().getStringArray(R.array.contact_email);
        web = getResources().getStringArray(R.array.contact_web);
        sms = getResources().getStringArray(R.array.contact_sms);
        phone = getResources().getStringArray(R.array.contact_phone);

        addContacts(phone, ContactEnum.Mobile);
        addContacts(sms, ContactEnum.Sms);
        addContacts(email, ContactEnum.Email);
        addContacts(web, ContactEnum.Web);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addContacts(String[] contacts, ContactEnum type)
    {
        for (int i = 0; i < contacts.length; i++) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            String [] details = contacts[i].split("\\|");
            ContactFragment fragment = ContactFragment.newInstance(details[0].toUpperCase(), details[1].trim(), type);
            fragmentTransaction.add(R.id.contactsContainer, fragment);
            fragmentTransaction.commit();
        }
    }

}
