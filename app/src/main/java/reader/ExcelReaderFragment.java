package reader;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import gp.reader.aks.com.aksreader.R;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by AKS on 4/16/2017.
 */

public class ExcelReaderFragment extends Fragment {

    private static final String TAG = ExcelReaderFragment.class.getSimpleName();
    private static final String SAMPLE_FILE = "Excel Test.xlsx";
    private static ExcelReaderFragment myFragment;
    private TableLayout table;

    public static ExcelReaderFragment newInstance() {
        myFragment = new ExcelReaderFragment();
        return myFragment;
    }

    public ExcelReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_excel, container, false);

        if (rootView != null) {
            table = (TableLayout) rootView.findViewById(R.id.table_layout);
            onRead();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private void onRead() {

        AssetManager am = getActivity().getAssets();
        LinearLayout ll;
        TextView tv_itemName;
        try {
            InputStream stream = am.open(SAMPLE_FILE);
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                android.widget.LinearLayout.LayoutParams llLP = new android.widget.LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                llLP.gravity = Gravity.CENTER_VERTICAL;
                ll.setLayoutParams(llLP);

                View v_Hr = new View(getActivity());
                android.widget.LinearLayout.LayoutParams llLPV_H = new android.widget.LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2);
                v_Hr.setLayoutParams(llLPV_H);
                llLPV_H.setMargins(0, 0, 0, 0);
                v_Hr.setBackgroundColor(android.graphics.Color.BLACK);

                for (int c = 0; c < cellsCount; c++) {
                    String value = getCellAsString(row, c, formulaEvaluator);

                    if (value == null) {
                        value = "";
                    }

                    tv_itemName = new TextView(getActivity());
                    android.widget.LinearLayout.LayoutParams item_text = new android.widget.LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    item_text.setMargins(6, 4, 6, 4);
                    tv_itemName.setLayoutParams(item_text);
                    tv_itemName.setText(value);
                    tv_itemName.setId(c);
                    tv_itemName.setTextSize(16);
                    tv_itemName.setPadding(2, 0, 2, 0);
                    tv_itemName.setGravity(Gravity.CENTER_VERTICAL);
                    tv_itemName.setTextColor(android.graphics.Color.BLACK);

                    if (ll != null && tv_itemName != null) {
                        ll.addView(tv_itemName);
                    }
                }
                table.addView(ll);
                if (r != (rowsCount - 1)) {
                    table.addView(v_Hr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return value;
    }
}
