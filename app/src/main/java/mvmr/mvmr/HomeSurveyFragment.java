package mvmr.mvmr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private ImagePagerAdapter imageAdapter;
    private ImageViewPager viewPager;
    Runnable _autoscrollTimer;
    final Handler _autoscrollHandler = new Handler();

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

        Drawable img = getContext().getResources().getDrawable( R.drawable.ic_report );;
        img.setBounds( 0, 0, 96, 96 );
        btnReport.setCompoundDrawables( img, null, null, null );


        img = getContext().getResources().getDrawable( R.drawable.ic_about );;
        img.setBounds( 0, 0, 96, 96 );
        btnAbout.setCompoundDrawables( img, null, null, null );

        img = getContext().getResources().getDrawable( R.drawable.ic_contacts );;
        img.setBounds( 0, 0, 96, 96 );
        btnContacts.setCompoundDrawables( img, null, null, null );

        imageAdapter = new ImagePagerAdapter(_context);
        viewPager = (ImageViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(imageAdapter);
        SetUpAutoScroll(viewPager);
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

    private void SetUpAutoScroll(final ImageViewPager viewPager)
    {
        final int count = imageAdapter.getCount();

        Runnable _autoscrollTimer = new Runnable() {

            @Override
            public void run() {
                int newIndex = 0;
                if(viewPager.getCurrentItem() + 1 < count ){
                    newIndex = viewPager.getCurrentItem() + 1;
                }
                viewPager.setCurrentItem(newIndex);
                _autoscrollHandler.postDelayed(this, 6*1000);

            }
        };
        _autoscrollHandler.post(_autoscrollTimer);
    }

    @Override
    public void onDestroy() {
        _autoscrollHandler.removeCallbacks(_autoscrollTimer);
        super.onDestroy();
    }
}
