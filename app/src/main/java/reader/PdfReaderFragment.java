package reader;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gp.reader.aks.com.aksreader.R;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PdfReaderFragment extends Fragment implements OnPageChangeListener, OnLoadCompleteListener, View.OnClickListener {

    private static final String TAG = PdfReaderFragment.class.getSimpleName();
    public final static int REQUEST_CODE = 42;
    //public static final int PERMISSION_CODE = 42042;
    private static final String SAMPLE_FILE = "JAVA Syllabus.pdf";
    //public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;
    private TextView file;
    private Button assets, sdCard;
    private  boolean displayFromAssets = true;
    private static PdfReaderFragment myFragment;

    public static PdfReaderFragment newInstance() {
        myFragment = new PdfReaderFragment();
        return myFragment;
    }

    public PdfReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);

        if (rootView != null) {
            assets = (Button) rootView.findViewById(R.id.btn_assets);
            sdCard = (Button) rootView.findViewById(R.id.btn_sdCard);
            file = (TextView) rootView.findViewById(R.id.fileName);

            pdfView = (PDFView) rootView.findViewById(R.id.pdfView);

            displayFromAsset(SAMPLE_FILE);

            assets.setOnClickListener(this);
            sdCard.setOnClickListener(this);
        }

        // Inflate the layout for this fragment
        return rootView;
    }


    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        displayFromAssets = true;
        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .load();

    }

    private void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(getActivity(), R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);
        displayFromAssets = false;
        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .load();
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void loadComplete(int nbPages) {
        printBookmarksTree(pdfView.getTableOfContents(), "-");
        String message = getActivity().getString(R.string.display_assets);
        if (!displayFromAssets)
        {
            message = getActivity().getString(R.string.display_sdcard);
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        try {
            if (file != null) {
                file.setText(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_assets:
                displayFromAsset(SAMPLE_FILE);
                break;

            case R.id.btn_sdCard:
                launchPicker();
                break;
        }

    }
}
