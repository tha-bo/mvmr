package mvmr.mvmr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeNoSurveyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeSurveyFragment extends Fragment {

    private Context _context;
    //private ImagePagerAdapter imageAdapter;

    public HomeSurveyFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment HomeNoSurveyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeSurveyFragment newInstance() {
        HomeSurveyFragment fragment = new HomeSurveyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnAbout = (Button) view.findViewById(R.id.about_button);
        btnAbout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(_context, AboutActivity.class);
                startActivity(i);
            }
        });

        Button btnSurvey = (Button) view.findViewById(R.id.survey_button);
        btnSurvey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(_context, Survey.class);
                startActivity(i);
            }
        });

        Button btnReport = (Button) view.findViewById(R.id.report_button);
        btnReport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(_context, Report.class);
                startActivity(i);
            }
        });

        Button btnContacts = (Button) view.findViewById(R.id.contact_button);
        btnContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(_context, Contacts.class);
                startActivity(i);
            }
        });

//        imageAdapter = new ImagePagerAdapter(_context);
//
//        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
//        viewPager.setAdapter(imageAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
