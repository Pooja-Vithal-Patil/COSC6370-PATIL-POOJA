package com.example.weatherwardrobe.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherwardrobe.ClothingItem;
import com.example.weatherwardrobe.R;
import com.example.weatherwardrobe.WardrobeItemInfo;

import java.util.ArrayList;
import java.util.Map;


public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ItemViewHolder> {

    private ArrayList<ClothingItem> mItemList;
    private Context mcontext;
    private String user;


    public ClothAdapter(@NonNull ArrayList<ClothingItem> ItemList, Context context, String user) {
        this.mItemList = ItemList;
        this.user = user;
        this.mcontext = context;

        if (mItemList.isEmpty()) {
            Toast.makeText(mcontext, "No Data Found", Toast.LENGTH_LONG).show();
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cloth_item, parent, false);
        return new ItemViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {


        final ClothingItem clothingItems = mItemList.get(position);

        String type = clothingItems.getType();
        Bitmap bitmap = null;
        //convert byte array to bitmap
        byte[] byteArr = clothingItems.getImg();
        if (byteArr != null) {
            bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        }
        //set views
        if (bitmap != null) {
            holder.img.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
        }
        holder.clothType.setText(type);
        holder.clothSession.setText(clothingItems.getDescription().toString());

        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle values = new Bundle();
                values.putLong("ID", clothingItems.getId());
                values.putByteArray("Image", clothingItems.getImg());
                values.putString("Colour", clothingItems.getColour());
                values.putString("Type", clothingItems.getType());
                values.putString("Description", clothingItems.getDescription());
                Intent intent = new Intent(mcontext, WardrobeItemInfo.class);
                intent.putExtras(values);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });


    }

//    public void setProductImage(ImageView product_img, String imageName) {
//
//
//        if (imageName.contains("htt")) {
//            new ImageLoadTask(imageName, product_img).execute();
//        } else if (imageName.contains(".jpg")) {
//            product_img.setImageResource(mcontext.getResources().getIdentifier((String) imageName.toLowerCase().replace(" ", "_").replace(".jpg", ""), "raw", mcontext.getPackageName()));
//        } else if (imageName.contains(".jpeg")) {
//            product_img.setImageResource(mcontext.getResources().getIdentifier((String) imageName.toLowerCase().replace(" ", "_").replace(".jpeg", ""), "raw", mcontext.getPackageName()));
//        } else if (imageName.contains(".png")) {
//            product_img.setImageResource(mcontext.getResources().getIdentifier((String) imageName.toLowerCase().replace(" ", "_").replace(".png", ""), "raw", mcontext.getPackageName()));
//        }
//
//    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView clothType, clothSession;
        private ImageView img;
        private LinearLayout header;


        public ItemViewHolder(View itemView) {
            super(itemView);
            clothType = itemView.findViewById(R.id.clothType);
            clothSession = itemView.findViewById(R.id.clothSession);
            header = itemView.findViewById(R.id.header);
            img = itemView.findViewById(R.id.cloth_img);


        }

    }


}
