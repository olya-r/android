package by.minsk.csc.watering.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import by.minsk.csc.watering.R;
import by.minsk.csc.watering.model.SmsData;

public class SmsDataListAdapter extends RecyclerView.Adapter<SmsDataListAdapter.ViewHolder> {
    private List<SmsData> mData;

    public void setSmsDataList( List<SmsData> data ) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder( LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_view, parent, false ));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        holder.mDateText.setText( dateFormat.format( mData.get( position ).getDate() ));
        holder.mCountText.setText( String.valueOf( mData.get( position ).getCount() ));
    }

    @Override
    public int getItemCount() {
        return ( mData != null ) ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateText;
        public TextView mCountText;

        public ViewHolder( View view ) {
            super( view );
            mDateText = ( TextView ) view.findViewById( R.id.dateText );
            mCountText = ( TextView ) view.findViewById( R.id.countText );
        }
    }

}
