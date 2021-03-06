package com.example.weatherwardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weatherwardrobe.adapters.ClothAdapter;

import java.util.ArrayList;

public class MyWardrobe extends AppCompatActivity {
    public Button wardrobeButton;
    public ArrayList<ClothingItem> clothingItems;
    public ListView wardrobeList;
    public WardrobeListAdapter wardrobeAdapter;
    private ItemsDataSource dh;
    public String sql = "SELECT * FROM items WHERE isClean = 1";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wardrobe);
        wardrobeButton = findViewById(R.id.add_clothing);
        wardrobeList = findViewById(R.id.wardrobe_list);
        dh = new ItemsDataSource(this);
        clothingItems = new ArrayList<ClothingItem>();
//        wardrobeAdapter = new WardrobeListAdapter(this);
//        wardrobeList.setAdapter(wardrobeAdapter);
        dh.open();
        clothingItems = dh.getItems(sql);
//        wardrobeAdapter.notifyDataSetChanged();
        recyclerView = findViewById(R.id.order_transaction_recycle_view);

//        wardrobeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Bundle values = new Bundle();
//                values.putLong("ID", clothingItems.get(position).getId());
//                values.putByteArray("Image", clothingItems.get(position).getImg());
//                values.putString("Colour", clothingItems.get(position).getColour());
//                values.putString("Type", clothingItems.get(position).getType());
//                values.putString("Description", clothingItems.get(position).getDescription());
//                Intent intent = new Intent(MyWardrobe.this, WardrobeItemInfo.class);
//                intent.putExtras(values);
//                startActivity(intent);
//            }
//        });


        findViewById(R.id.addCloth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyWardrobe.this, AddClothing.class);
                startActivity(intent);

            }
        });

        recyclerView.removeAllViews();

        ClothAdapter pendingOrderTransactionAdapter = new ClothAdapter(clothingItems, getApplicationContext(), "role");
        GridLayoutManager gridLayout = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setAdapter(pendingOrderTransactionAdapter);
        recyclerView.setLayoutManager(gridLayout);


    }

    public void onClick(View view) {
        Intent intent = new Intent(MyWardrobe.this, AddClothing.class);
        startActivity(intent);
    }

    public void clear_basket(View v) {
        if (clothingItems.size() > 0) {
            dh.clear_basket();
            clothingItems.clear();
            wardrobeAdapter.notifyDataSetChanged();
        }
    }

    private class WardrobeListAdapter extends ArrayAdapter<ClothingItem> {
        public WardrobeListAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return clothingItems.size();
        }

        public ClothingItem getItem(int position) {
            return clothingItems.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //get clothing item and attributes
            ClothingItem item = getItem(position);

            String type = item.getType();
            Bitmap bitmap = null;
            //convert byte array to bitmap
            byte[] byteArr = item.getImg();
            if (byteArr != null) {
                bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
            }
            //inflate view
            LayoutInflater inflater = MyWardrobe.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.wardrobe_item_layout, null);
            //get views
            ImageView image = (ImageView) result.findViewById(R.id.image);
            TextView type_text = (TextView) result.findViewById(R.id.type);
            TextView desc_text = (TextView) result.findViewById(R.id.desc);

            //set views
            if (bitmap != null) {
                image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
            }
            type_text.setText(type);
            desc_text.setText(item.getDescription().toString());

            return result;
        }
    }

    public void onResume() {
        super.onResume();
        clothingItems = dh.getItems(sql);
//        wardrobeAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        super.onDestroy();
        dh.close();
    }
}
