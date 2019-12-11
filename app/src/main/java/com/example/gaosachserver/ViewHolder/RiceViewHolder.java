package com.example.gaosachserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gaosachserver.Common.Common;
import com.example.gaosachserver.Interface.ItemClickListener;
import com.example.gaosachserver.R;

import androidx.recyclerview.widget.RecyclerView;

public class RiceViewHolder  extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener
{
    public TextView rice_name,rice_price;
    public ImageView rice_image;

    private ItemClickListener itemClickListener;


    public RiceViewHolder(View itemView) {
        super(itemView);


//
        rice_name= (TextView)itemView.findViewById(R.id.rice_name);
        rice_image= (ImageView)itemView.findViewById(R.id.rice_image);
        rice_price= (TextView)itemView.findViewById(R.id.rice_price);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}