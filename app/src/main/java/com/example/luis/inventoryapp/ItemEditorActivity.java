package com.example.luis.inventoryapp;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Created by Luis on 11/21/2017.
 */

public class ItemEditorActivity extends AppCompatActivity {

    /** EditText field to enter stock id*/
    private EditText mStockIdEditText;

    /** EditText field to enter supplier name*/
    private  EditText mSupplierNameEditText;

    /** EditText field to enter details*/
    private  EditText mDetailsEditText;

    /** EditText field to enter quantity*/
    private  EditText mQuantityEditText;

    /** EditText field to enter cost*/
    private  EditText mCostEditText;

    /** EditText field to enter price*/
    private  EditText mPriceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_editor_activity);

        // Find all relevant views that we will need to read user input from
        mStockIdEditText = (EditText) findViewById(R.id.edit_stock_id);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mDetailsEditText = (EditText) findViewById(R.id.edit_details);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mCostEditText = (EditText) findViewById(R.id.edit_cost);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
