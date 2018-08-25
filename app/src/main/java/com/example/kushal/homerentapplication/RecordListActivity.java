package com.example.kushal.homerentapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class RecordListActivity extends AppCompatActivity
{

    ListView mListView;

    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Record List");

        //To enable back button in actionbar set parent activity main activity in manifest

        mListView = (ListView) findViewById(R.id.listView);
        // 1) Now design Row for listview, 2) then create RecordListAdapter to set data in view

        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this,R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        // 3) Get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            String number = cursor.getString(3);
            String room = cursor.getString(4);
            String price = cursor.getString(5);

            // Get image
            byte[] image = cursor.getBlob(6);

            // After getting add to list
            mList.add(new Model(id, name, address, number, room, price, image));
        }

        mAdapter.notifyDataSetChanged();

        if (mList.size() == 0)
        {
            // If there is no record in table, means listview is empty
            Toast.makeText(this, "No Record Found...!!!", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l)
            {
                // Alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i == 0)
                        {
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext())
                            {
                                arrID.add(c.getInt(0));
                            }
                            // Show update dialog
                            showDialogUpdate(RecordListActivity.this,arrID.get(position));
                        }
                        if (i == 1)
                        {
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext())
                            {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void showDialogDelete(final int idRecord)
    {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are You Sure To Delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i)
            {
                try
                {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Deleted Successsfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e("error", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(final Activity activity, final int position)
    {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtName = dialog.findViewById(R.id.edtName1);
        final EditText edtAddress = dialog.findViewById(R.id.edtAddress1);
        final EditText edtNumber = dialog.findViewById(R.id.edtNumber1);
        final EditText edtRoom = dialog.findViewById(R.id.edtRoomsize1);
        final EditText edtPrice = dialog.findViewById(R.id.edtPrice1);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        // Set width of Dialogbox
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);

        // Set height of Dialogbox
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        // to update image
        imageViewIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Check external storage permission
                ActivityCompat.requestPermissions(RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},888);

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    MainActivity.mSQLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtAddress.getText().toString().trim(),
                            edtNumber.getText().toString().trim(),
                            edtRoom.getText().toString().trim(),
                            edtPrice.getText().toString().trim(),
                            imageViewToByte(imageViewIcon),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Update Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error)
                {
                    Log.e("Update Error", error.getMessage());
                }

                updateRecordList();
            }
        });
    }

    private void updateRecordList()
    {
        // Get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            String number = cursor.getString(3);
            String room = cursor.getString(4);
            String price = cursor.getString(5);

            byte[] image = cursor.getBlob(6);
            mList.add(new Model(id,name,address,number,room,price,image));
        }
        mAdapter.notifyDataSetChanged();
    }

    public static byte[] imageViewToByte(ImageView image)
    {
        // Conversion of image
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,30,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 888)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Gallery Intent
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,888);
            }
            else
            {
                Toast.makeText(RecordListActivity.this,"You Don't Have Permission",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON) // Enable Image Guidelines
                    .setAspectRatio(1,1) // Image will be in square
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();

                // Set Image choosed from Gallery
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
