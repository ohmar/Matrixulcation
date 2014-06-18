package com.sandovoid.matrixulcation;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.ClipboardManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import java.math.BigDecimal;
import android.view.View;
import android.widget.Toast;
import android.app.DialogFragment;
import android.app.ActionBar;


public class MatrixActivity extends Activity {

    public double[][] matrix_A_array, matrix_B_array, C;

    public static final int CHECK_DIMENSION_AXB = 1;
    public static final int CHECK_DIMENSION_APLUSB = 2;
    public static final int CHECK_DIMENSION_APLUSA = 3;
    public static final int CHECK_DIMENSION_BPLUSB = 4;
    public static final int CHECK_DIMENSION_AMINUSB = 5;
    public static final int CHECK_DIMENSION_TRANSPOSEA = 6;
    public static final int CHECK_DIMENSION_TRANSPOSEB = 7;
    public static final int CLOSE_ID = 1;
    public static final int ABOUT_ID = 2;
    public static final int SETTINGS_ID = 3;
    public boolean dimension_valid;
    public boolean dimension_good=true;

    public int i,j,k;
    public static String c_result = "", R_tmp;
    EditText matrix_A, matrix_B, matrix_C;
    Button equal;
    Button swap;
    Spinner spinner_operations;
    private int[] piv;
    private static final String[] array = {"A - B", "A + B", "A x B", "A x A", "B x B", "Transpose A", "Transpose B"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        matrix_A = (EditText)findViewById(R.id.matrix_A);
        matrix_B = (EditText)findViewById(R.id.matrix_B);

        spinner_operations = (Spinner) findViewById (R.id.spinner_operations);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        spinner_operations.setAdapter(adapter);

        equal = (Button)findViewById(R.id.equal);
        equal.setOnClickListener(new clicker());

        swap = (Button)findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String OriginalA = matrix_A.getText().toString();
                String OriginalB = matrix_B.getText().toString();
                matrix_A.setText(OriginalB);
                matrix_B.setText(OriginalA);
            }
        });
    }

    class clicker implements Button.OnClickListener {
        public void onClick(View v) {
            if(v == equal) {
                String s = (String) spinner_operations.getSelectedItem();
                StartFunctions(s);
            }
        }

        private void StartFunctions(String v){

            if (v.equals("A x B")) {
                CreateA_Array();
                CreateB_Array();
                if(DimensionalCheck(CHECK_DIMENSION_AXB)){
                    if(dimension_good) Equal_AxB();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }
            if (v.equals("A + B")) {
                CreateA_Array();
                CreateB_Array();
                if(DimensionalCheck(CHECK_DIMENSION_APLUSB)) {
                    if(dimension_good) Equal_AplusB();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }

            if (v.equals("A - B")) {
                CreateA_Array();
                CreateB_Array();
                if(DimensionalCheck(CHECK_DIMENSION_APLUSB)) {
                    if(dimension_good) Equal_AminusB();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }

            if (v.equals("A x A")) {
                CreateA_Array();
                if(DimensionalCheck(CHECK_DIMENSION_APLUSA)) {
                    if(dimension_good) Equal_AxA();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }

            if (v.equals("B x B")) {
                CreateB_Array();
                if(DimensionalCheck(CHECK_DIMENSION_BPLUSB)) {
                    if(dimension_good) Equal_BxB();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }
            if (v.equals("Transpose A")) {
                CreateA_Array();
                if(DimensionalCheck(CHECK_DIMENSION_TRANSPOSEA)) {
                    if(dimension_good) Transpose_A();
                    if(dimension_good) SetResult();
                    if(dimension_good) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }
            if (v.equals("Transpose B")) {
                CreateB_Array();
                if( DimensionalCheck(CHECK_DIMENSION_TRANSPOSEB)) {
                    if( dimension_good ) Transpose_B();
                    if( dimension_good ) SetResult();
                    if( dimension_good ) ShowResultDialog();
                } else {
                    ShowDimensionDialog();
                }
            }
        }
    }

    // Creating our Spinner items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // OnClick monitoring our menu items
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) { //onOptionsItemSelected calling functions
        switch (item.getItemId()) {
            case CLOSE_ID:
                finish(); // end the program
            case ABOUT_ID:
                ShowAboutDialog();
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
                ShowAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Create an initial 2-dimensional array in input box
    private void CreateA_Array() {
        dimension_good = true;

        String S_matrix_A = matrix_A.getText().toString();

        S_matrix_A = S_matrix_A.replace(" ", "");
        S_matrix_A = S_matrix_A.replace("\n\n", "\n");

        String[] A_m = S_matrix_A.split("\n");
        String[] A_n = A_m[0].split(",");
        String[] A_tmp;
        matrix_A_array = new double[A_m.length][A_n.length];

        i=0;j=0;

        for(i=0; i < A_m.length; i++) {
            A_tmp = A_m[i].split(",");
            for(j=0; j < A_n.length; j++) {
                try {
                    matrix_A_array[i][j] = Double.valueOf(A_tmp[j]).doubleValue();
                } catch(NumberFormatException nfe){
                    dimension_good=false;
                    ShowInputErrorDialog();
                }
            }
        }
    }

    // Create an initial 2-dimensional array in our second input box
    private void CreateB_Array() {

        String S_matrix_B = matrix_B.getText().toString();

        S_matrix_B = S_matrix_B.replace(" ", "");
        S_matrix_B = S_matrix_B.replace("\n\n", "\n");

        String[] B_m = S_matrix_B.split("\n");
        String[] B_n = B_m[0].split(",");
        String[] B_tmp;
        matrix_B_array = new double[B_m.length][B_n.length];

        i=0;j=0;
        for( i=0; i < B_m.length; i++ ) {
            B_tmp = B_m[i].split(",");

            for( j=0; j < B_n.length; j++ ) {
                try {
                    matrix_B_array[i][j] = Double.valueOf(B_tmp[j]).doubleValue();
                } catch( NumberFormatException nfe ) {
                    dimension_good = false;
                    ShowInputErrorDialog();
                }
            }
        }

    }

    protected boolean DimensionalCheck(int type) {
        switch(type) {
            case CHECK_DIMENSION_AXB:
                if( matrix_A_array[0].length == matrix_B_array.length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_APLUSB:

                // if A number of rows equals B number of rows AND
                // number of A columns equals number of B columns
                if( matrix_A_array.length == matrix_B_array.length &&
                   matrix_A_array[0].length == matrix_B_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_AMINUSB:

                // if A number of rows equals B number of rows AND
                // number of A columns equals number of B columns
                if( matrix_A_array.length == matrix_B_array.length &&
                    matrix_A_array[0].length == matrix_B_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_APLUSA:

                // if number of rows is equal to number of columns, dimensions valid
                if( matrix_A_array.length == matrix_A_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_BPLUSB:
                if( matrix_B_array.length == matrix_B_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_TRANSPOSEA:
                if( matrix_A_array.length < matrix_A_array[0].length ||
                    matrix_A_array.length > matrix_A_array[0].length ||
                    matrix_A_array.length == matrix_A_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_TRANSPOSEB:
                if( matrix_B_array.length < matrix_B_array[0].length ||
                    matrix_B_array.length > matrix_B_array[0].length ||
                    matrix_B_array.length == matrix_B_array[0].length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
        }
        return dimension_valid;
    }

    // Compute A * B
    private void Equal_AxB() {
        int m = matrix_A_array.length; // A rows
        int n = matrix_A_array[0].length; // A columns
        int p = matrix_B_array[0].length; // B columns

        C = new double[m][p];
        for(int i = 0; i < m; i++) { // Keep iterating until the last row of A
            for(int j = 0; j < p; j++) { // Keep iterating until the last column of B
                for(int k = 0; k < n; k++) { // Keep iterating until last column of A
                    C[i][j] += matrix_A_array[i][k] * matrix_B_array[k][j]; // Keep sum
                }
            }
        }
    }

    // Compute A + B
    private void Equal_AplusB() {
        int m = matrix_A_array.length; // A rows
        int n = matrix_B_array.length; // B rows
        C = new double[m][n];
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                C[i][j] = matrix_A_array[i][j]+matrix_B_array[i][j];
            }
        }
    }

    private void Equal_AminusB() {
        int mA = matrix_A_array.length;
        int nA = matrix_A_array[0].length;
        int mB = matrix_B_array.length;
        int nB = matrix_B_array[0].length;
        C = new double[mA][mB];
        for( int i = 0; i < mA; i++) {
            for( int j = 0; j< mB; j++) {
                C[i][j] = matrix_A_array[i][j] - matrix_B_array[i][j];
            }
        }
    }

    // Compute A * A
    private void Equal_AxA() {
        int m = matrix_A_array.length; // A rows
        int n = matrix_A_array[0].length; // A columns
        C = new double[m][n];

        for(int i = 0; i < m; i++) { // Keep iterating until the last A row is reached
            for(int j = 0; j < n; j++) { // Keep iterating until the last A columns is reached
                for(int k = 0; k < n; k++) {
                    C[i][j] += matrix_A_array[i][k]*matrix_A_array[k][j];
                }
            }
        }
    }

    // Compute B * B
    private void Equal_BxB() {
        int m = matrix_B_array.length;
        int n = matrix_B_array[0].length;
        C = new double[m][n];
        for( int i = 0; i < m; i++ ) {
            for( int j = 0; j < n; j++ ) {
                for( int k = 0; k < n; k++ ){
                    C[i][j] += matrix_B_array[i][k]*matrix_B_array[k][j];
                }
            }
        }
    }

    // A matrix M x N will have transpose N x M
    private void Transpose_A() {
        int m = matrix_A_array.length;
        int n = matrix_A_array[0].length;
        C = new double[n][m];
        for( int i = 0; i < m; i++ ) {
            for( int j = 0; j < n; j++ ) {
                C[j][i] = matrix_A_array[i][j];
            }
        }
    }

    // A matrix M x N will have transpose N x M
    private void Transpose_B() {
        int m = matrix_B_array.length;
        int n = matrix_B_array[0].length;
        C = new double[n][m];
        for( int i = 0; i < m; i++ ) {
            for( int j = 0; j < n; j++ ) {
                C[j][i] = matrix_B_array[i][j];
            }
        }
    }

    // Converting calculated results to string
    private void SetResult(){
        c_result="";
        for( i = 0; i < C.length; i++ ) {
            for( j = 0; j < C[0].length; j++ ) {

                int decimalPlace = 3;
                BigDecimal bd = new BigDecimal(C[i][j]);
                bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
                C[i][j] = bd.doubleValue();

                if( (C[i][j] - Double.valueOf(C[i][j]).intValue() ) == 0){
                    R_tmp =  ""+Double.valueOf(C[i][j]).intValue();
                } else {
                    R_tmp =  ""+Double.valueOf(C[i][j]).floatValue();
                }
                if(j!=0)
                    c_result = c_result + "," + R_tmp;
                else
                    c_result = c_result + "" + R_tmp;
            }
            c_result = c_result  + "\n";
        }
    }

    /* Creating dialog functions */

    public class ResultDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK);
            builder.setMessage(c_result)
                   .setTitle( R.string.dialog_result_title )
                   .setPositiveButton(R.string.dialog_result_copy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                           // Copy result
                           ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                           ClipData clip = ClipData.newPlainText("Result", c_result);
                           clipboard.setPrimaryClip(clip);
                           dismiss();
                           Toast WhereToast = Toast.makeText(getApplicationContext(), R.string.toast_copied, Toast.LENGTH_SHORT);
                           WhereToast.show();
                       }
                    })
                   .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                           // Close result dialog
                           dismiss();
                       }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    // TODO: Change style for dialogs.
    public void ShowResultDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new ResultDialogFragment();
        newFragment.show(ft, "Result");
    }

    public static class DimensionErrorDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
            builder.setIcon(android.R.drawable.ic_dialog_info)
                   .setTitle(R.string.dialog_dimension_title)
                   .setMessage(R.string.dialog_dimension_message)
                   .setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Close dialog
                        }
                    });
            return builder.create();
        }
    }

    public void ShowDimensionDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DimensionErrorDialog();
        // newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BlackDialogs);
        newFragment.show(ft, "Dimension");
    }

    public static class AboutDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK);
            builder.setMessage( R.string.dialog_about )
                   .setPositiveButton(R.string.dialog_about_thanks, new DialogInterface.OnClickListener() {
                       public void onClick( DialogInterface dialog, int whichButton ) {
                           // Thanks
                       }
                   })
                   .setNegativeButton( R.string.dialog_close, new DialogInterface.OnClickListener() {
                       public void onClick( DialogInterface dialog, int whichButton) {
                           // Cancelled dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public void ShowAboutDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new AboutDialogFragment();
        newFragment.show(ft, "About");
    }

    public static class DialogInputError extends DialogFragment {
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
            builder.setIcon( android.R.drawable.ic_dialog_info )
                   .setTitle( R.string.dialog_input_error_title )
                   .setMessage( R.string.dialog_input_error_message )
                   .setPositiveButton( R.string.dialog_close, new DialogInterface.OnClickListener() {
                       public void onClick( DialogInterface dialog, int whichButton ) {
                           //
                       }
                   });
            return builder.create();
        }
    }

    public void ShowInputErrorDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DialogInputError();
        newFragment.show(ft, "InputError");
    }

}
