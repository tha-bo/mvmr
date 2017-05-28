package mvmr.mvmr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class HomeNoSurveyFragment extends Fragment {

    private Context _context;

    public HomeNoSurveyFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment HomeNoSurveyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeNoSurveyFragment newInstance() {
        HomeNoSurveyFragment fragment = new HomeNoSurveyFragment();
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
        return inflater.inflate(R.layout.fragment_home_no_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSurvey2 = (Button) view.findViewById(R.id.survey_button2);
        btnSurvey2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(_context, Survey.class);
                startActivity(i);
            }
        });
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
