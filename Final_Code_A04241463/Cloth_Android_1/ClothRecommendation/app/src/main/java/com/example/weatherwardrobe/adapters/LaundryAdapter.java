package com.example.weatherwardrobe.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherwardrobe.ClothingItem;
import com.example.weatherwardrobe.ItemsDataSource;
import com.example.weatherwardrobe.LaundryBasket;
import com.example.weatherwardrobe.R;
import com.example.weatherwardrobe.WardrobeItemInfo;

import java.util.ArrayList;


public class LaundryAdapter extends RecyclerView.Adapter<LaundryAdapter.ItemViewHolder> {

    private ArrayList<ClothingItem> mItemList;
    private Context mcontext;
    private String user;
    private ItemsDataSource dh;


    public LaundryAdapter(@NonNull ArrayList<ClothingItem> ItemList, Context context, String user, ItemsDataSource dh) {
        this.mItemList = ItemList;
        this.user = user;
        this.dh = dh;
        this.mcontext = context;

        if (mItemList.isEmpty()) {
            Toast.makeText(mcontext, "No Data Found", Toast.LENGTH_LONG).show();
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_item, parent, false);
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
        holder.clothType.setText(type.toUpperCase());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dh.clear_item(clothingItems.getId());
                Intent intent = new Intent(mcontext, LaundryBasket.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);

//                clothingItems.remove(clothingItems.indexOf(searchById(itemId)));

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

        private TextView clothType;
        private Button remove;
        private ImageView img;


        public ItemViewHolder(View itemView) {
            super(itemView);
            clothType = itemView.findViewById(R.id.clothType);
            remove = itemView.findViewById(R.id.remove);
            img = itemView.findViewById(R.id.cloth_img);


        }

    }


}
