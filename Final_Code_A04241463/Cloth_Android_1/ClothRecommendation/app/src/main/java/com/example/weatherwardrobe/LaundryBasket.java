package com.example.weatherwardrobe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weatherwardrobe.adapters.ClothAdapter;
import com.example.weatherwardrobe.adapters.LaundryAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class LaundryBasket extends AppCompatActivity {
    public Button b;
    public ArrayList<ClothingItem> clothingItems;
    public ListView l;
    public LaundryListAdapter laundryAdapter;
    public long positionInArray;
    private ItemsDataSource dh;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_basket);
        l = findViewById(R.id.laundry_list);
        ClothingItem temp;
        dh = new ItemsDataSource(this);
        clothingItems = new ArrayList<ClothingItem>();
//        laundryAdapter = new LaundryBasket.LaundryListAdapter(this);
//        l.setAdapter(laundryAdapter);
        dh.open();
        String sql = "SELECT * FROM items WHERE isClean = 0";
        clothingItems = dh.getItems(sql);
//        laundryAdapter.notifyDataSetChanged();
        recyclerView = findViewById(R.id.order_transaction_recycle_view);
        recyclerView.removeAllViews();

        LaundryAdapter pendingOrderTransactionAdapter = new LaundryAdapter(clothingItems, getApplicationContext(), "role",dh);
        GridLayoutManager gridLayout = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setAdapter(pendingOrderTransactionAdapter);
        recyclerView.setLayoutManager(gridLayout);

    }

    public void clear_basket(View v){
        if (clothingItems.size() > 0) {
            dh.clear_basket();
            clothingItems.clear();
//            laundryAdapter.notifyDataSetChanged();
        }
    }

    public void delete_item(View v){
        long itemId = v.getId();
        dh.clear_item(itemId);
        clothingItems.remove(clothingItems.indexOf(searchById(itemId)));
        laundryAdapter.notifyDataSetChanged();
    }

    public ClothingItem searchById(long id){
        for (int i = 0; i < clothingItems.size(); i++){
            if (clothingItems.get(i).getId() == id){
                return clothingItems.get(i);
            }
        }
        return null;
    }

    private class LaundryListAdapter extends ArrayAdapter<ClothingItem> {
        public LaundryListAdapter(Context ctx) {
            super(ctx, 0);
        }
        public int getCount(){
            return clothingItems.size();
        }
        public ClothingItem getItem(int position){
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

            }            //inflate view
            LayoutInflater inflater = LaundryBasket.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.clothing_item_layout, null);
            //get views
            ImageView image = (ImageView)result.findViewById(R.id.image);
            TextView type_text = (TextView)result.findViewById(R.id.type);
            Button delete_button = (Button)result.findViewById(R.id.delete);
            delete_button.setId((int)item.getId());
            //set views
            if (bitmap != null) {
                image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
            }
            type_text.setText(type);

            return result;
        }
    }

    public void onDestroy(){
        super.onDestroy();
        dh.close();
    }
}
