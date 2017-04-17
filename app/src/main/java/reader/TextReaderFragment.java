package reader;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import gp.reader.aks.com.aksreader.R;

/**
 * Created by AKS on 4/16/2017.
 */

public class TextReaderFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TextReaderFragment.class.getSimpleName();
    private static final String SAMPLE_URL = "http://sites.google.com/site/androidersite/text.txt";
    private static TextReaderFragment myFragment;

    private EditText urlText;
    private TextView content;
    private Button fetchContent;
    private boolean displayFromAssets = true;
    private String urlValue;

    public static TextReaderFragment newInstance() {
        myFragment = new TextReaderFragment();
        return myFragment;
    }

    public TextReaderFragment() {
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
            urlText = (EditText) rootView.findViewById(R.id.file_Url);
            fetchContent = (Button) rootView.findViewById(R.id.btn_fetch);
            content = (TextView) rootView.findViewById(R.id.fileContent);
            urlText.setText(SAMPLE_URL);
            fetchContent.setOnClickListener(this);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_fetch) {
            if (isConnectingToInternet()) {
                if (urlText != null && urlText.getText() != null) {
                    urlValue = urlText.getText().toString();

                    if (urlValue != null && (URLUtil.isValidUrl(urlValue)) && urlValue.endsWith(".txt")) {

                        new ReadTextfromUrl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getActivity(), "Please enter a valid urlValue", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid urlValue", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please connect to internet connection first", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class ReadTextfromUrl extends AsyncTask<String, Void, String> {

        ProgressDialog dialog ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // show progress dialog when downloading
            dialog = ProgressDialog.show(getActivity(), null, "Downloading...");
        }

        @Override
        protected String doInBackground(String... params) {

            // @BadSkillz codes with same changes
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urlValue);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                BufferedHttpEntity buf = new BufferedHttpEntity(entity);

                InputStream is = buf.getContent();

                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line + "\n");
                }
                String result = total.toString();
                //Log.e("Get URL", "Downloaded string: " + result);
                r.close();
                return result;
            } catch (Exception e) {
                Log.e("Get Url", "Error in downloading: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // show result in textView
            if (result == null) {
                content.setText("Error in downloading. Please try again.");
            } else {
                content.setText(result);
            }

            // close progresses dialog
            dialog.dismiss();
        }
    }

}
