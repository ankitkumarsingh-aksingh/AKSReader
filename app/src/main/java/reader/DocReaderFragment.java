package reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gp.reader.aks.com.aksreader.R;

/**
 * Created by AKS on 4/16/2017.
 */

public class DocReaderFragment extends Fragment {

    private static final String TAG = DocReaderFragment.class.getSimpleName();
    private static DocReaderFragment myFragment;


    public static DocReaderFragment newInstance() {
        myFragment = new DocReaderFragment();
        return myFragment;
    }

    public DocReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);

        if (rootView != null) {

        }

        // Inflate the layout for this fragment
        return rootView;
    }

}
