package com.example.kyungjoo.maestro.main.board.thumbnail;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-15.
 */
public class PostBoard extends Activity {
    private int PICK_IMAGE = 1;
    private String MULTIPART = "multipart/form-data";
    private String TEXT = "text/plain";
    private String IMAGE = "image/jpeg";
    private int PERMISSION_OK = 1;
    Uri uri;
    NetworkService networkService;
    MultipartBody.Part photo;
    ThumbnailAdapter adapter;
    EditText editTitle, editContent;
    TextView postBtn;
    ImageView uploadPhoto, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_post);
        networkService = ApplicationController.getInstance().getNetworkService();
        adapter = ThumbnailAdapter.getInstance();
        initView();
    }

    private void initView(){
        editTitle = (EditText) findViewById(R.id.post_title);
        editContent = (EditText) findViewById(R.id.post_content);
        backBtn = (ImageView) findViewById(R.id.post_back);
        postBtn = (TextView) findViewById(R.id.post_post);
        uploadPhoto = (ImageView) findViewById(R.id.post_upload_photo);
        photo = null;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((Intent.ACTION_PICK));
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTitle.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "fill title please", Toast.LENGTH_SHORT).show();
                }else {
                    RequestBody requestTitle = RequestBody.create(MediaType.parse(TEXT), editTitle.getText().toString());
                    RequestBody requestContent = RequestBody.create(MediaType.parse(TEXT), editContent.getText().toString());
                    Call<Thumbnail> thumbnailCall = networkService.newContent(getIntent().getStringExtra("ID"), requestTitle, requestContent, photo);
                    thumbnailCall.enqueue(new Callback<Thumbnail>() {
                        @Override
                        public void onResponse(Call<Thumbnail> call, Response<Thumbnail> response) {
                            if (response.isSuccessful()) {
                                Thumbnail thumbnail = response.body();
                                adapter.append(thumbnail);
                                editTitle.setText("");
                                editContent.setText("");
                                Log.i("TEST", "title : " + thumbnail.title);
                                finish();
                            } else {
                                int statusCode = response.code();
                                Log.i("TEST", "response code : " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Call<Thumbnail> call, Throwable t) {
                            Log.d("TEST", "err : " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get permission to access user gallery
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "need permission to access external storage", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_OK);
            } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_OK);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Log.d("TEST", "permission ok");
                    String url = getPath(this, uri);
                    File file = new File(url);
                    photo = MultipartBody.Part.createFormData("photo", file.getPath(), RequestBody.create(MediaType.parse(IMAGE), file));
                    ImageView imageView = new ImageView(this);
                    LinearLayout linearLayout= (LinearLayout) findViewById(R.id.post_layout);
                    imageView.setImageURI(uri);
                    linearLayout.addView(imageView);
                }else{
                    //Log.d("TSET", "permission denyed");
                    Toast.makeText(getApplicationContext(), "permission denyed", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    // make image file using uri
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
