package com.example.kushal.homerentapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity
{
    EditText mEdtName,mEdtNumber,mEdtRoom,mEdtPrice,mEdtAddress;
    Button mBtnAdd,mBtnList;
    ImageView mImageView;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Record");

        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtNumber = (EditText) findViewById(R.id.edtNumber);
        mEdtRoom = (EditText) findViewById(R.id.edtRoomsize);
        mEdtPrice = (EditText) findViewById(R.id.edtPrice);
        mEdtAddress = (EditText) findViewById(R.id.edtAddress);
        mBtnAdd = (Button) findViewById(R.id.btnAdd);
        mBtnList = (Button) findViewById(R.id.btnList);
        mImageView = (ImageView) findViewById(R.id.imageView);

        // Creating Database
        mSQLiteHelper = new SQLiteHelper(this, "HomeDB.sqlite", null, 1);

        // Creating Table
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, number VARCHAR, address VARCHAR, room VARCHAR, price VARCHAR, image BLOB)");


        /* select image on click on imageview */
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Runtime Permission to select image from gallery
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GALLERY);
            }
        });

        /* Add Records TO SQLite Database*/
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    mSQLiteHelper.insertData(
                            mEdtName.getText().toString().trim(),
                            mEdtAddress.getText().toString().trim(),
                            mEdtNumber.getText().toString().trim(),
                            mEdtRoom.getText().toString().trim(),
                            mEdtPrice.getText().toString().trim(),
                            imageViewToByte(mImageView)
                    );
                    Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                    // Reset all fields
                    mEdtName.setText("");
                    mEdtAddress.setText("");
                    mEdtNumber.setText("");
                    mEdtRoom.setText("");
                    mEdtPrice.setText("");
                    mImageView.setImageResource(R.drawable.addphoto);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        /* Show Records From SQLite Database*/
        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Start RecordList Activity
                startActivity(new Intent(MainActivity.this,RecordListActivity.class));
            }
        });
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
        if(requestCode == REQUEST_CODE_GALLERY)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Gallery Intent
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,REQUEST_CODE_GALLERY);
            }
            else
            {
                Toast.makeText(MainActivity.this,"You Don't Have Permission",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK)
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
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
