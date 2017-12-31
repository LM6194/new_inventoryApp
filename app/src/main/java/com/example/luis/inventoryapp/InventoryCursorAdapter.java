package com.example.luis.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Luis on 12/26/2017.
 */
/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of ring data as its data source. This adapter knows
 * how to create list items for each row of rings data in the {@link Cursor}.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param viewGroup  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find fields to populate in inflate template
        TextView textViewSupplier = (TextView)view.findViewById(R.id.supplier);
        TextView textViewDetails = (TextView)view.findViewById(R.id.details);

        //Find the columns of rings attributes that we're  interested in
        int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
        int detailsColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_DETAILS);

        //Read the pet attributes from the cursor for the current pet
        String ringSupplier = cursor.getString(supplierColumnIndex);
        String ringDetails = cursor.getString(detailsColumnIndex);

        //Populate fields with extracted properties
        textViewSupplier.setText(ringSupplier);
        textViewDetails.setText(ringDetails);

    }
}
