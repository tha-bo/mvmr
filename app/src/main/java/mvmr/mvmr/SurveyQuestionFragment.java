package mvmr.mvmr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SurveyQuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SurveyQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyQuestionFragment extends Fragment {
    private OnFragmentInteractionListener mListener; // parent activity
    private int result;
    private int questionId;
    private String question;

    public SurveyQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SurveyQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SurveyQuestionFragment newInstance(int id, String question, int result) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle args = new Bundle();
        args.putInt("Id", id);
        args.putInt("Result", result);
        args.putString("Question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = questionId = 0;
        if (getArguments() != null) {
            questionId = getArguments().getInt("Id");
            result = getArguments().getInt("Result");
            question = getArguments().getString("Question");


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_question, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup group = (RadioGroup) view.findViewById(R.id.rGroup);
        switch(result) {
            case 1:
                group.check(R.id.r1);
                break;
            case 2:
                group.check(R.id.r2);
                break;
            case 3:
                group.check(R.id.r3);
                break;
            case 4:
                group.check(R.id.r4);
                break;
            case 5:
                group.check(R.id.r5);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.r1:
                if (checked)
                    result = 1;
                    break;
            case R.id.r2:
                if (checked)
                    result = 2;
                    break;
            case R.id.r3:
                if (checked)
                    result = 3;
                    break;
            case R.id.r4:
                if (checked)
                    result = 4;
                    break;
            case R.id.r5:
                if (checked)
                    result = 5;
                    break;
        }
        if (mListener != null) {
            mListener.onFragmentInteraction(questionId, result);
        }
    }

    public int GetResult()
    {
        return result;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int uri, int result);
    }
}
