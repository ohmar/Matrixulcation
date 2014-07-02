package com.sandovoid.matrixulcation;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.ClipboardManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.AdapterView.OnItemClickListener;
import android.view.Gravity;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MatrixActivity extends Activity implements OnItemClickListener {

    public double[][] matrix_A_array, matrix_B_array, C;

    public static final int CHECK_DIMENSION_AXB = 1;
    public static final int CHECK_DIMENSION_AXA = 2;
    public static final int CHECK_DIMENSION_BTIMESB = 3;
    public static final int CHECK_DIMENSION_APLUSB = 4;
    public static final int CHECK_DIMENSION_APLUSA = 5;
    public static final int CHECK_DIMENSION_BPLUSB = 6;
    public static final int CHECK_DIMENSION_AMINUSB = 7;
    public static final int CHECK_DIMENSION_TRANSPOSEA = 8;
    public static final int CHECK_DIMENSION_TRANSPOSEB = 9;
    public boolean dimension_valid;
    public boolean dimension_good=true;

    public int i,j;
    public static String c_result = "", R_tmp;
    static EditText matrix_A, matrix_B, d_matrix_A, d_matrix_B;
    static TextView welcome, description;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mOperationTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mOperationTitles = getResources().getStringArray(R.array.operations_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mOperationTitles));
        mDrawerList.setOnItemClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_about:
                ShowAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), mOperationTitles[position] + " selected",
                    Toast.LENGTH_SHORT).show();
        }
    }
    */

    private void selectItem(int position) {
        // Update the main content by replacing fragments
        if (position == 0) {
            // Show the "home" fragment
            Fragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putInt(HomeFragment.ARG_OPERATION_NUMBER, position);
            homeFragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, homeFragment).commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mOperationTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        }
        if (position == 1) {
            // Show the binary operations fragment
            Fragment binaryFragment = new BinaryOperationFragment();
            Bundle args = new Bundle();
            args.putInt(BinaryOperationFragment.ARG_OPERATION_NUMBER, position);
            binaryFragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, binaryFragment).commit();

            // Update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mOperationTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        if (position == 2) {
            // TODO: Implement "Solve" operation fragment
            Fragment decompFragment = new DecompositionOperationsFragment();
            Bundle args = new Bundle();
            args.putInt(DecompositionOperationsFragment.ARG_OPERATION_NUMBER, position);
            decompFragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, decompFragment).commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(mOperationTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        if (position == 3) {
            // TODO: Implement a few "test" operations
        }
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occures.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears is just a basic "home" view
     */
    public static class HomeFragment extends Fragment {
        public static final String ARG_OPERATION_NUMBER = "operation_number";

        public HomeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int i = getArguments().getInt(ARG_OPERATION_NUMBER);
            View rootView = inflater.inflate(R.layout.activity_home, container, false);

            welcome = (TextView) rootView.findViewById(R.id.welcome);
            description = (TextView) rootView.findViewById(R.id.welcome_description);

            return rootView;
        }
    }

    /**
     * Fragment that appears in content_frame will show matrix
     */
    public static class BinaryOperationFragment extends Fragment {
        public static final String ARG_OPERATION_NUMBER = "operation_number";

        public BinaryOperationFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int i = getArguments().getInt(ARG_OPERATION_NUMBER);
            String operation = getResources().getStringArray(R.array.operations_array)[i];

            View rootView = inflater.inflate(R.layout.activity_binary_matrix, container, false);

            matrix_A = (EditText) rootView.findViewById(R.id.matrix_A);
            matrix_B = (EditText) rootView.findViewById(R.id.matrix_B);

            return rootView;
        }
    }

    /**
     * Fragment that appears for Decomposition Operations
     */
    public static class DecompositionOperationsFragment extends Fragment {
        public static final String ARG_OPERATION_NUMBER = "operation_number";

        public DecompositionOperationsFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int i = getArguments().getInt(ARG_OPERATION_NUMBER);
            String operation = getResources().getStringArray(R.array.operations_array)[i];

            View rootView = inflater.inflate(R.layout.activity_decomposition, container, false);

            d_matrix_A = (EditText) rootView.findViewById(R.id.d_matrix_A);
            d_matrix_B = (EditText) rootView.findViewById(R.id.d_matrix_B);
            return rootView;
        }
    }

    public void aMinusB(View view) {
        CreateA_Array();
        CreateB_Array();
        if(DimensionalCheck(CHECK_DIMENSION_AMINUSB)) {
            if(dimension_good) Equal_AminusB();
            if(dimension_good) SetResult();
            if(dimension_good) ShowResultDialog();
        } else {
            ShowDimensionDialog();
        }
    }

    public void bMinusA(View view) {
        CreateA_Array();
        CreateB_Array();
        if (DimensionalCheck(CHECK_DIMENSION_AMINUSB)) {
            if (dimension_good) {
                Equal_BminusA();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void aTimesB(View view) {
        CreateA_Array();
        CreateB_Array();
        if (DimensionalCheck(CHECK_DIMENSION_AXB)) {
            if (dimension_good) Equal_AxB();
            if (dimension_good) SetResult();
            if (dimension_good) ShowResultDialog();
        } else {
            ShowDimensionDialog();
        }
    }

    public void bTimesA(View view) {
        CreateA_Array();
        CreateB_Array();
        if (DimensionalCheck(CHECK_DIMENSION_AXB)) {
            if (dimension_good) {
                Equal_BtimesA();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void aTimesA(View view) {
        CreateA_Array();
        if (DimensionalCheck(CHECK_DIMENSION_AXA)) {
            if (dimension_good) {
                Equal_AxA();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void bTimesB(View view) {
        CreateB_Array();
        if (DimensionalCheck(CHECK_DIMENSION_BTIMESB)) {
            if (dimension_good) {
                Equal_BxB();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void transposeA(View view) {
        CreateA_Array();
        if (DimensionalCheck(CHECK_DIMENSION_TRANSPOSEA)) {
            if (dimension_good) {
                Transpose_A();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void transposeB(View view) {
        CreateB_Array();
        if (DimensionalCheck(CHECK_DIMENSION_TRANSPOSEB)) {
            if (dimension_good) {
                Transpose_B();
                SetResult();
                ShowResultDialog();
            } else {
                ShowDimensionDialog();
            }
        }
    }

    public void croutonTest(View view) {
        Crouton.showText(
                this,
                getString(R.string.crouton_message),
                Style.ALERT);
    }

    // Create an initial 2-dimensional array in input box
    public void CreateA_Array() {
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
    public void CreateB_Array() {

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
            case CHECK_DIMENSION_AXA:
                if ( matrix_A_array[0].length == matrix_A_array.length ) {
                    dimension_valid = true;
                } else {
                    dimension_valid = false;
                }
                break;
            case CHECK_DIMENSION_BTIMESB:
                if (matrix_B_array[0].length == matrix_B_array.length) {
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

    private void Equal_BtimesA() {
        int m = matrix_A_array.length;
        int n = matrix_B_array[0].length;
        int p = matrix_A_array[0].length;
        C = new double[m][n];
        for(int i = 0; i < m; i++) { // Keep iterating until the last row of A
            for(int j = 0; j < p; j++) { // Keep iterating until the last column of B
                for(int k = 0; k < n; k++) { // Keep iterating until last column of A
                    C[i][j] += matrix_A_array[k][i] * matrix_B_array[j][k]; // Keep sum
                }
            }
        }
    }

    private void Equal_AminusB() {
        int mA = matrix_A_array.length;
        int mB = matrix_B_array.length;
        C = new double[mA][mB];
        for( int i = 0; i < mA; i++) {
            for( int j = 0; j< mB; j++) {
                C[i][j] = matrix_A_array[i][j] - matrix_B_array[i][j];
            }
        }
    }

    private void Equal_BminusA() {
        int mA = matrix_A_array.length;
        int mB = matrix_B_array.length;
        C = new double[mA][mB];
        for( int i = 0; i < mA; i++) {
            for( int j = 0; j< mB; j++) {
                C[i][j] = matrix_A_array[j][i] - matrix_B_array[j][i];
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
    public void SetResult(){
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
