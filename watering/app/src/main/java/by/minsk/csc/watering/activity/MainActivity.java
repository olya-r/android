package by.minsk.csc.watering.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import by.minsk.csc.watering.R;
import by.minsk.csc.watering.adapter.SmsDataListAdapter;
import by.minsk.csc.watering.fragment.NumberListDialogFragment;
import by.minsk.csc.watering.model.SmsData;

public class MainActivity extends AppCompatActivity
        implements NumberListDialogFragment.OnNumberChangedListener {

    private static final String PREFERENCE_FILE_KEY = "by.minsk.csc.watering.preferences";
    private static final String PREFERENCE_SMS_ADDRESS = "sms_address";

    private static final int PERMISSION_REQUEST_FOR_READ_NUMBERS = 100;
    private static final int PERMISSION_REQUEST_FOR_READ_DATA = 101;

    private String mSmsAddress;
    private SmsDataListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        initNumber();
        initListView();
    }

    private void initNumber() {
        SharedPreferences preferences = getSharedPreferences( PREFERENCE_FILE_KEY, Context.MODE_PRIVATE );
        mSmsAddress = preferences.getString( PREFERENCE_SMS_ADDRESS, "" );
    }

    private void saveNumber() {
        SharedPreferences preferences = getSharedPreferences( PREFERENCE_FILE_KEY, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString( PREFERENCE_SMS_ADDRESS, mSmsAddress );
        editor.apply();
    }

    private void initListView() {
        RecyclerView listView = ( RecyclerView ) findViewById( R.id.listView );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        listView.setLayoutManager( layoutManager );

        mListAdapter = new SmsDataListAdapter();
        listView.setAdapter( mListAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.action_settings:
                selectNumber();
                return true;

            case R.id.action_refresh:
                refreshData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNumberChanged( String number ) {
        mSmsAddress = number;
        saveNumber();
        refreshData();
    }

    @Override
    public void onRequestPermissionsResult( int requestCode,
                                            @NonNull String permissions[],
                                            @NonNull int[] grantResults) {
        switch ( requestCode ) {
            case PERMISSION_REQUEST_FOR_READ_NUMBERS:
                if ( isPermissionsGranted( grantResults )) {
                    selectNumber();
                }
                break;

            case PERMISSION_REQUEST_FOR_READ_DATA:
                if ( isPermissionsGranted( grantResults )) {
                    refreshData();
                }
                break;
        }
    }

    private boolean isPermissionsGranted( @NonNull int[] grantResults ) {
        return ( grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED );
    }

    private boolean checkSmsPermissions( int requestCode ) {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_SMS )
                == PackageManager.PERMISSION_GRANTED ) {
            return true;
        }

        ActivityCompat.requestPermissions( this,
                new String[] { Manifest.permission.READ_SMS },
                requestCode );

        return false;
    }

    private void selectNumber() {
        if ( !checkSmsPermissions( PERMISSION_REQUEST_FOR_READ_NUMBERS )) {
            return;
        }

        List<String> numberList = readNumberList();

        NumberListDialogFragment dialog = NumberListDialogFragment.newInstance( mSmsAddress );
        dialog.setNumberList( numberList );
        dialog.show( getSupportFragmentManager(), "number_dialog" );
    }

    private void refreshData() {
        if ( !checkSmsPermissions( PERMISSION_REQUEST_FOR_READ_DATA )) {
            return;
        }

        List<SmsData> dataList = readSmsDataList();

        mListAdapter.setSmsDataList( dataList );
    }

    private List<String> readNumberList() {
        List<String> result = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                Telephony.Sms.Inbox.CONTENT_URI,
                new String[] { Telephony.Sms.ADDRESS },
                null,
                null,
                Telephony.Sms.ADDRESS + " ASC" );

        if ( cursor != null ) {
            String currentAddress = "";
            while ( cursor.moveToNext()) {
                if ( !currentAddress.equals( cursor.getString( 0 ))) {
                    currentAddress = cursor.getString( 0 );
                    result.add( currentAddress );
                }
            }
            cursor.close();
        }

        return result;
    }

    private List<SmsData> readSmsDataList() {
        SmsData.Builder builder = new SmsData.Builder();

        Cursor cursor = getContentResolver().query(
                Telephony.Sms.Inbox.CONTENT_URI,
                new String[] { Telephony.Sms.DATE_SENT },
                Telephony.Sms.ADDRESS + " = ?",
                new String[] { mSmsAddress },
                null );

        if ( cursor != null ) {
            while ( cursor.moveToNext()) {
                builder.add( cursor.getLong( 0 ));
            }
            cursor.close();
        }

        return builder.toList();
    }

}
