package by.minsk.csc.watering.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SmsData {
    private Date mDate;
    private List<Date> mTimeList = new ArrayList<>();

    public Date getDate() {
        return mDate;
    }

    public void setDate( Date date ) {
        mDate = date;
    }

    public int getCount() {
        return mTimeList.size();
    }

    public void addTime( Date time ) {
        mTimeList.add( time );
    }

    // Просто какие-то данные для проверки отрисовки списка
    public static List<SmsData> createFakeDataList() {
        List<SmsData> list = new ArrayList<>();

        SmsData data1 = new SmsData();
        Calendar calendar1 = new GregorianCalendar( 2016, 3, 14 );
        data1.setDate( calendar1.getTime() );
        calendar1.add( Calendar.HOUR_OF_DAY, 8 );
        data1.addTime( calendar1.getTime() );
        list.add(data1);

        SmsData data2 = new SmsData();
        Calendar calendar2 = new GregorianCalendar( 2016, 4, 25 );
        data2.setDate( calendar2.getTime() );
        calendar2.add( Calendar.HOUR_OF_DAY, 8 );
        data2.addTime( calendar2.getTime() );
        calendar2.add( Calendar.HOUR_OF_DAY, -3 );
        data2.addTime( calendar2.getTime() );
        list.add(data2);

        SmsData data3 = new SmsData();
        Calendar calendar3 = new GregorianCalendar( 2016, 5, 11 );
        data3.setDate( calendar3.getTime() );
        for ( int i = 0; i < 6; ++i ) {
            calendar3.add( Calendar.HOUR_OF_DAY, 1 );
            data3.addTime( calendar3.getTime() );
        }
        list.add(data3);

        return list;
    }

    public static class Builder {
        private Map<Date, SmsData> mData = new TreeMap<>();

        public void add( long millis ) {
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTimeInMillis( millis );

            Calendar dateCalendar = new GregorianCalendar(
                    timeCalendar.get( Calendar.YEAR ),
                    timeCalendar.get( Calendar.MONTH ),
                    timeCalendar.get( Calendar.DATE ));

            Date time = timeCalendar.getTime();
            Date date = dateCalendar.getTime();

            SmsData data;
            if ( mData.containsKey( date )) {
                data = mData.get( date );
            }
            else {
                data = new SmsData();
                data.setDate( date );
                mData.put( date, data );
            }
            data.addTime( time );
        }

        public List<SmsData> toList() {
            List<SmsData> result = new ArrayList<>();
            result.addAll( mData.values() );
            Collections.reverse( result );
            return result;
        }

    }

}
