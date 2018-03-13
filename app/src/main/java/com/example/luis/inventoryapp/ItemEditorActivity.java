package com.example.luis.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;



import static java.lang.String.*;

/**
 * Created by Luis on 11/21/2017.
 */

public class ItemEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the ring data loader
     */
    private static final int RING_LOADER = 0;

    /**
     * EditText field to enter stock id
     */
    private EditText mStockIdEditText;

    /**
     * EditText field to enter supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter details
     */
    private EditText mDetailsEditText;

    /**
     * EditText field to enter quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter cost
     */
    private EditText mCostEditText;

    /**
     * EditText field to enter price
     */
    private EditText mPriceEditText;

    /**
     * Content URI for the existing ring(null if it's a new ring)
     */
    private Uri mCurrentRingUri;

    /**
     * Boolean flag that keeps track of whether the ring has been edited (true) or not (false)
     */
    private boolean mRingHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the  mRingHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mRingHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_editor_activity);

        //Examine the intent that was use to launch this activity,
        //in order to figure out if we're creating a new ring or editing an existing one.
        Intent intent = getIntent();
        mCurrentRingUri = intent.getData();

        // If the intent DOES NOT contain a ring content URI, then we know that we are
        //creating a new ring.
        if (mCurrentRingUri == null) {
            // This is a new ring so change the appbar to say " Add a Ring"
            setTitle(getString(R.string.editor_activity_title_new_ring));

            // invalidate the options menu, so the "Delete" menu option can be hidden.
            // (it doesn't make sense to delete a ring that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // other wise this is an existing ring, so change app bar to say "Edit Ring"
            setTitle(getString(R.string.editor_activity_edit_ring));

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(RING_LOADER, null, this);
        }

        Button sold = findViewById(R.id.sold_button);
        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityNumber = mQuantityEditText.getText().toString().trim();
                int quantityField = Integer.parseInt(quantityNumber);
                if (quantityField > 0) {
                    quantityField = quantityField - 1;
                    EditText textElement = findViewById(R.id.edit_quantity);
                    Log.i("Luis", "value of quantityField is: " +
                            quantityField);
                    textElement.setText(String.valueOf(quantityField));

                } else {
                    Toast.makeText(getApplicationContext(), "Out Stock Please Re Order", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button reOrder = findViewById(R.id.re_order_button);
        final String textOrder = "Supplier Name: " + "\n" +
                "Item details: " + "\n" + "quantity: ";
        reOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Re Order Jewelery item");
                intent.putExtra(Intent.EXTRA_TEXT, textOrder);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


        // Find all relevant views that we will need to read user input from
        mStockIdEditText = (EditText) findViewById(R.id.edit_stock_id);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mDetailsEditText = (EditText) findViewById(R.id.edit_details);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mCostEditText = (EditText) findViewById(R.id.edit_cost);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);

        //Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us Know of there are unsaved changes
        // or not, if the user  tries to leave the editor without saving.
        mStockIdEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mDetailsEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mCostEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save  ring into database.
     */
    private void saveRing() {

        int stock = -1;
        int quantity = -1;
        int cost = -1;
        int price = -1;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String stockNumber = mStockIdEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String details = mDetailsEditText.getText().toString().trim();
        String quantityNumber = mQuantityEditText.getText().toString().trim();
        String costNumber = mCostEditText.getText().toString().trim();
        String priceNumber = mPriceEditText.getText().toString().trim();


        // Check if this is supposed to be a new ring
        // and check if all the fields in the editor are blank
        if (mCurrentRingUri == null &&
                TextUtils.isEmpty(stockNumber) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(details) && TextUtils.isEmpty(quantityNumber) &&
                TextUtils.isEmpty(costNumber) && TextUtils.isEmpty(priceNumber)) {
            // Since no fields were modified, we can return early without creating a new ring.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        else if (stockNumber.equals("")  || quantityNumber.equals("") ||
                costNumber.equals("") || priceNumber.equals("")){

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            //showUnFinishForm();
            OnClickListener discardButtonClickListener =
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnFinishForm(discardButtonClickListener);
        }


        if(stockNumber.equals("")) {
            //showUnFinishForm();
            OnClickListener discardButtonClickListener =
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnFinishForm(discardButtonClickListener);
        }
        else {
            stock = Integer.parseInt(stockNumber);
        }


        if (quantityNumber.equals("")){
            //showUnFinishForm();
            OnClickListener discardButtonClickListener =
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnFinishForm(discardButtonClickListener);
        }
        else {
            quantity = Integer.parseInt(quantityNumber);
        }

        if (costNumber.equals("")){
            //showUnFinishForm();
            OnClickListener discardButtonClickListener =
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnFinishForm(discardButtonClickListener);
        }
        else{
            cost = Integer.parseInt(costNumber);
        }

        if (priceNumber.equals("")){
            //showUnFinishForm();
            OnClickListener discardButtonClickListener =
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnFinishForm(discardButtonClickListener);
        }
        else {
            price = Integer.parseInt(priceNumber);
        }






        // Create a ContentValues object where column names are the keys.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_STOCK_ID, stock);
        values.put(InventoryEntry.COLUMN_SUPPLIER, supplierName);
        values.put(InventoryEntry.COLUMN_DETAILS, details);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_COST, cost);
        values.put(InventoryEntry.COLUMN_PRICE, price);



        // Determine if this is a new or existing ring by checking if MCurrentRingUri is null or not
        if (mCurrentRingUri == null) {
            // This is a new ring , so insert a new ring into the provider,
            // returning the content URI for the new ring.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            //Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_ring_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_ring_successful),
                        Toast.LENGTH_LONG).show();
            }
        }else {
            // Otherwise this is an EXISTING ring , so update the ring with content URI: mCurrentRingUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // mCurrentRingUri will already indentify the correct row in the database that
            //wi want to modify.
            int rowsAffected = getContentResolver().update(mCurrentRingUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // if no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_ring_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_ring_successful),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new ring, hide the "Delete" menu item.
        if (mCurrentRingUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert ring in the database
                saveRing();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the ring hasn't changed, continue with navigating up to parent activity
                // which is the {@link Catalog Activity}.
                if (!mRingHasChanged) {
                    NavUtils.navigateUpFromSameTask(ItemEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                OnClickListener discardButtonClickListener =
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ItemEditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the usr they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the ring hasn't changed, continue with handling back button press
        if (!mRingHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        OnClickListener discardButtonClickListener =
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_STOCK_ID,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_DETAILS,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_COST,
                InventoryEntry.COLUMN_PRICE};
        //This loader will execute the  ContentProvider's query method on a background thread
        return new CursorLoader(this, //Parent activity context
                mCurrentRingUri,             // query the content URI for the current ring
                projection,                  // columns to include in the resulting cursor
                null,                // no selection clause
                null,             //no selection arguments
                null);             // default sor order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than q row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            //Find the columns of ring attributes that we're interested in
            int stockColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_STOCK_ID);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
            int detailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_DETAILS);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int costColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_COST);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

            // Extract out the value from the Cursor for the given column index
            int stockId = cursor.getInt(stockColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String details = cursor.getString(detailColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int cost = cursor.getInt(costColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            //Update the views on the screen with the values from the database
            mStockIdEditText.setText(valueOf(stockId));
            mSupplierNameEditText.setText(supplier);
            mDetailsEditText.setText(details);
            mQuantityEditText.setText(valueOf(quantity));
            mCostEditText.setText(valueOf(cost));
            mPriceEditText.setText(valueOf(price));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mStockIdEditText.setText("");
        mSupplierNameEditText.setText("");
        mDetailsEditText.setText("");
        mQuantityEditText.setText("");
        mCostEditText.setText("");
        mPriceEditText.setText("");

    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the ring.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this ring.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               // User clicked the "Delete" button, so delete the ring.
               deleteRing();
           }});
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the ring.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the ring in the database.
     */
    private void deleteRing() {
        // Only perform the delete if this is an existing ring.
        if (mCurrentRingUri != null) {
            // Call the ContentResolver to delete the ring at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentRingUri
            // content URI already identifies the ring that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentRingUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_ring_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_ring_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    /**
     * Prompt the  user that not all the fields are complete
     */
    private void showUnFinishForm(OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the ring.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}