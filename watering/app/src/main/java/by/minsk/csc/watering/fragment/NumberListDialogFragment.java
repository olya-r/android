package by.minsk.csc.watering.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.List;

public class NumberListDialogFragment extends DialogFragment {
    private static final String ARG_CURRENT_NUMBER = "current_number";
    private String mCurrentNumber;

    private List<String> mNumberList;
    private ArrayAdapter<String> mNumberListAdapter;

    private OnNumberChangedListener mNumberDialogListener;

    public interface OnNumberChangedListener {
        void onNumberChanged( String path );
    }

    public static NumberListDialogFragment newInstance( String currentNumber ) {
        NumberListDialogFragment fragment = new NumberListDialogFragment();
        Bundle args = new Bundle();
        args.putString( ARG_CURRENT_NUMBER, currentNumber );
        fragment.setArguments( args );
        return fragment;
    }

    public NumberListDialogFragment() {
    }

    public void setNumberList( List<String> numberList ) {
        mNumberList = numberList;
        if ( mNumberListAdapter != null ) {
            mNumberListAdapter.clear();
            mNumberListAdapter.addAll( mNumberList );
        }
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        setNumberDialogListener( ( OnNumberChangedListener ) context );
    }

    @Override
    public void onDetach() {
        setNumberDialogListener( null );
        super.onDetach();
    }

    private void setNumberDialogListener(OnNumberChangedListener listener ) {
        mNumberDialogListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null ) {
            mCurrentNumber = getArguments().getString( ARG_CURRENT_NUMBER );
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mNumberListAdapter = new ArrayAdapter<>( getContext(),
                android.R.layout.simple_list_item_1, mNumberList );

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setTitle( mCurrentNumber );
        builder.setAdapter( mNumberListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                if ( mNumberDialogListener != null ) {
                    mNumberDialogListener.onNumberChanged( mNumberList.get( which ) );
                }
            }
        });
        return builder.create();
    }

}
