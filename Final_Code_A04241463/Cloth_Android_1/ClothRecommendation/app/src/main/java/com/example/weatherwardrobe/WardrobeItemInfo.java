package com.example.weatherwardrobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WardrobeItemInfo extends AppCompatActivity {
    public long id;
    private ItemsDataSource dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_item_info);
        dh = new ItemsDataSource(this);
        dh.open();
        id = getIntent().getExtras().getLong("ID");
        byte[] img = getIntent().getExtras().getByteArray("Image");
        Bitmap bitmap = null;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        }
        String colour = getIntent().getExtras().getString("Colour");
        String type = getIntent().getExtras().getString("Type");
        String desc = getIntent().getExtras().getString("Description");
        TextView id_text = (TextView) findViewById(R.id.item_id);
        EditText colour_text = (EditText) findViewById(R.id.color);
        TextView clothType = (EditText) findViewById(R.id.clothType);
        EditText description = (EditText) findViewById(R.id.description);
        ImageView img_picture = (ImageView) findViewById(R.id.image);

        colour_text.setText(colour);
        clothType.setText(type);
        description.setText(desc);

        if (bitmap != null) {
            img_picture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, false));
        }

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dh.deleteItem(id);
                finish();
                startActivity(new Intent(getApplicationContext(), MyWardrobe.class));
            }
        });
    }

    public void onClick(View v) {
        dh.deleteItem(id);
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        dh.close();
    }
}
