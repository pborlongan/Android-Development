package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Pat on 2019-04-15.
 */

public class CardsAdapter extends SimpleCursorAdapter
{
    static final String[] columns = {DBManager.C_BOOKING_DATE, DBManager.C_BOOKING_TIME_IN, DBManager.C_BOOKING_PEOPLE};
    static final int[] ids = {R.id.tv_date, R.id.tv_time, R.id.tv_guests};

    public CardsAdapter(Context context, Cursor cursor)
    {
        super(context, R.layout.cards_layout, cursor, columns, ids);
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor)
    {
        super.bindView(row, context, cursor);
    }
}
