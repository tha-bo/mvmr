package mvmr.mvmr;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static mvmr.mvmr.ContactEnum.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String contactName;
    private String contactNumber;
    private ContactEnum contactType;


    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String name, String contact, ContactEnum contactType) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("ContactName", name);
        args.putString("ContactNumber", contact);
        args.putInt("ContactType", contactType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contactName = getArguments().getString("ContactName");
            contactNumber = getArguments().getString("ContactNumber");
            contactType = values()[getArguments().getInt("ContactType")];
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = (TextView) view.findViewById(R.id.txt_contact_name);
        name.setText(contactName);

        Button btn = (Button) view.findViewById(R.id.btn_contact);
        btn.setText(contactNumber);
        Drawable img = null;

        switch (contactType){
            case Mobile:{
                img = getContext().getResources().getDrawable( R.drawable.ic_phone );
                break;
            }
            case Sms:{
                img = getContext().getResources().getDrawable( R.drawable.ic_sms );
                break;
            }
            case Email:{
                img = getContext().getResources().getDrawable( R.drawable.ic_email );
                break;
            }
            default:{
                img = getContext().getResources().getDrawable( R.drawable.ic_globe );
            }
        }

        img.setBounds( 0, 0, 32, 32 );
        btn.setCompoundDrawables( img, null, null, null );

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = null;

                if(contactType == Sms)
                {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + contactNumber));
                }

                else if(contactType == Mobile)
                {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactNumber));
                }

                else if(contactType == Web)
                {
                    Uri webpage = Uri.parse(contactNumber);
                    intent = new Intent(Intent.ACTION_VIEW, webpage);
                }

                else if(contactType == Email)
                {
                    intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, contactNumber);
                }

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
