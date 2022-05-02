package com.example.weatherwardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.weatherwardrobe.api.ApiClient;
import com.example.weatherwardrobe.api.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClothing extends AppCompatActivity {

    private Spinner typesDropdown;
    private Spinner colourDropdown;
    private Button addButton;
    private ImageView doneButton;
    ArrayList<String> cloths;
    private ImageView clickedImage;
    private EditText descText;
    private ItemsDataSource dh;
    private static final int Image_Capture_Code = 1;
    private static final String TAG = "AddClothing";
    private byte[] image;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private Uri filePath;
    private static final int REQUEST_PERMISSION = 0;
    String[] types;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
            if (!permissions.isEmpty()) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION);
            }
        }


        dh = new ItemsDataSource(this);
        // open to write into database
        dh.open();

        // get the spinner from the xml.
        typesDropdown = findViewById(R.id.types_spinner);
        colourDropdown = findViewById(R.id.colour_spinner);
        clickedImage = findViewById(R.id.selected_img);

        //create a list of items for the spinner.
        //String[] colours = new String[]{"white", "black", "grey", "red", "orange", "yellow", "green", "blue", "purple"};
        //String[] types = new String[]{"coat", "tshirt", "longsleeve", "sweater", "pants", "shorts"};
        String[] colours = new String[]{"Select Colour", getResources().getString(R.string.white), getResources().getString(R.string.black), getResources().getString(R.string.grey), getResources().getString(R.string.red), getResources().getString(R.string.orange), getResources().getString(R.string.yellow), getResources().getString(R.string.green), getResources().getString(R.string.blue), getResources().getString(R.string.purple)};
        types = new String[]{"Select Cloth Type", getResources().getString(R.string.coat), getResources().getString(R.string.tshirt), getResources().getString(R.string.longsleeve), getResources().getString(R.string.sweater), getResources().getString(R.string.pants), getResources().getString(R.string.shorts)};
        cloths = new ArrayList<>();
        cloths.add("Select Cloth Type");
        cloths.add(getResources().getString(R.string.coat));
        cloths.add(getResources().getString(R.string.tshirt));
        cloths.add(getResources().getString(R.string.longsleeve));
        cloths.add(getResources().getString(R.string.sweater));
        cloths.add(getResources().getString(R.string.pants));
        cloths.add(getResources().getString(R.string.shorts));


        //create an adapter to describe how items are displayed. There's multiple variations but this is the basic one.
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colours);

        //set the spinners adapter to the previously created one.
        typesDropdown.setAdapter(typesAdapter);
        colourDropdown.setAdapter(colourAdapter);

        addButton = (Button) findViewById(R.id.camera_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent, SELECT_FILE);

                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
            }
        });

        doneButton = (ImageView) findViewById(R.id.done_outfit);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (image == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.if_no_image), Toast.LENGTH_LONG).show();
                } else {
                    String type = typesDropdown.getSelectedItem().toString();
                    String colour = colourDropdown.getSelectedItem().toString();

                    descText = findViewById(R.id.descText);
                    String description = descText.getText().toString();
                    Log.d("EditText", descText.getText().toString());
                    ClothingItem newItem = new ClothingItem("name", image, colour, type, 1, description);
                    dh.createItem(newItem);
                    finish();
                    startActivity(new Intent(getApplicationContext(), MyWardrobe.class));

                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                findViewById(R.id.crd_img).setVisibility(View.VISIBLE);
                clickedImage.setVisibility(View.VISIBLE);
                clickedImage.setImageBitmap(bp);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = stream.toByteArray();
                predictImage(null);
                Log.d(TAG, "Finished adding image to item.");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.cancelled), Toast.LENGTH_LONG).show();
            }
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK) {
//
//
//            if (requestCode == REQUEST_CAMERA) {
//
//                Bundle bundle = data.getExtras();
//                Bitmap photo = (Bitmap) bundle.get("data");
//                clickedImage.setVisibility(View.VISIBLE);
//                clickedImage.setImageBitmap(photo);
//                filePath = getImageUri(AddClothing.this.getApplicationContext(), photo);
//
//            } else if (requestCode == SELECT_FILE) {
//                findViewById(R.id.crd_img).setVisibility(View.VISIBLE);
//                clickedImage.setVisibility(View.VISIBLE);
//                filePath = data.getData();
//                Uri selectedImageUri = data.getData();
////                clickedImage.setImageURI(selectedImageUri);
//                Bitmap image=null;
//                try {
//                    ParcelFileDescriptor parcelFileDescriptor =
//                            getContentResolver().openFileDescriptor(selectedImageUri, "r");
//                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//                    image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                    parcelFileDescriptor.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                predictImage(image);
//
//            }
//
//        }
//    }
//

    public Uri getImageUri(Context inContext, Bitmap inImage) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void predictImage(Bitmap bp) {

//        clickedImage.setVisibility(View.VISIBLE);
//        clickedImage.setImageBitmap(bp);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bp.compress(Bitmap.CompressFormat.PNG, 80, stream);
//        image = stream.toByteArray();
//
//        findViewById(R.id.crd_img).setVisibility(View.VISIBLE);
//        findViewById(R.id.crd_img).setVisibility(View.VISIBLE);

        try {

            // Creating Object for Api Client
            final ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


            final ProgressDialog progressDialog = new ProgressDialog(this.getApplicationContext());
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Detecting cloth type...");
            progressDialog.show();


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Call<HashMap<String, String>> call = apiInterface.predict("https:slashslashfirebasestorage.googleapis.comslashv0slashbslashcloth-recommendation.appspot.comslashoslashimagesPER2F2bd4787a-e63f-45df-9d92-971c3810f0a4?alt=mediaANDtoken=1df0c6d7-2202-4650-b3cc-adfa7672df1c");
                    call.enqueue(new Callback<HashMap<String, String>>() {
                        @Override
                        public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                            progressDialog.dismiss();

                            if (call.isExecuted()) {
                                // Show Loading Dialog

                                if (response.body().get("error").equalsIgnoreCase("false")) {
                                    typesDropdown.setSelection(Integer.parseInt(response.body().get("result")));
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "NET_ISSUE", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error " + t.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }, 2000);





        } catch (Exception e) {
            typesDropdown.setSelection(Integer.parseInt("2"));
        }

    }


    public void onDestroy() {
        super.onDestroy();
        dh.close();
    }
}
