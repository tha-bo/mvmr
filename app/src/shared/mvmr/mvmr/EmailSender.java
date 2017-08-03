package mvmr.mvmr;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mvmr.mvmr.models.ReportModel;
import mvmr.mvmr.models.SurveyModel;
import mvmr.mvmr.models.UserModel;
import javax.activation.DataHandler;

/**
 * Created by PeerlessGate on 5/21/2017.
 * Credited to http://androidhelpd.blogspot.co.za/2015/07/send-email-programmatically-in-android_6.html
 * and http://stackoverflow.com/questions/25136025/sending-mail-in-android-without-intents-using-smtp
 */

public class EmailSender {

    private static final String username = "mvmrClient@gmail.com";
    private static final String password = "mvmrPass123";


    public static void SendSurvey(Activity context, SurveyModel model)
    {
        Send(context, "Survey Result", "School: " + model.School + " ;" + "\n\n" +
                "Grade: " + model.Grade + " ;" + "\n\n" +
                "Survey Result: " + model.Result);
    }

    public static void SendReport(Activity context, ReportModel model)
    {
        Send(context, "Report Result", "Date: " + model.Date + " ;" + "\n\n" +
                "Description: " + model.Description + " ;" + "\n\n" +
                "Platform Occurred: " + model.PlatformOccurred + " ;" + "\n\n" +
                "Is Victim: " + model.IsVictim);
    }

    private static void Send(Activity context,String subject, String text)
    {
        UserModel user = new UserModel(
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.RELEASE + " | " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
        );

        String[] socialMedia = context.getResources().getStringArray(R.array.known_sm);

        for (String sm: socialMedia) {
            if(appInstalledOrNot(sm.split(",")[0], context))
            {
                user.SocialMedia += sm.split(",")[1] + " ";
            }
        }

        String user_id = context.getSharedPreferences("MVMR", 0).getString("user_id", null);


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mvmrClient@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("mvmrClient@gmail.com"));
            message.setSubject(subject);
            message.setText(text + " ," + "\n\n" +
                    "UserId:" + user_id + " ," + "\n\n" +
                    "Device Manufacturer:" + user.Manufacterer + " ," + "\n\n" +
                    "Device Model:" + user.Model + " ," + "\n\n" +
                    "Device version:" + user.Version + " ," + "\n\n" +
                    "Social Media:" + user.SocialMedia + " ," + "\n\n" +
                    "UserTimeStamp:" + new SimpleDateFormat("_HH:mm:ss").format(new Date())
            );
            new SendMailTask(context).execute(message);
        }

        catch(Exception e)
        {

        }
    }

    private static boolean appInstalledOrNot(String uri, Activity context)
    {
        PackageManager pm = context.getPackageManager();
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


    public static boolean IsOnline(Activity context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static class SendMailTask extends AsyncTask<Message, Void, Void> {

        private Activity _context;
        public SendMailTask(Activity context) {
            super();
            _context = context;
            // do stuff
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //_context.getSharedPreferences("MVMR_Survey", 0).edit().clear().commit();//clear
            _context.getSharedPreferences("MVMR", 0).edit().putInt("submittedSurvey", 1).commit();
        }

        protected Void doInBackground(javax.mail.Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
