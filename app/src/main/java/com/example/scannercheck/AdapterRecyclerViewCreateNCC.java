package com.example.scannercheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerViewCreateNCC extends RecyclerView.Adapter<AdapterRecyclerViewCreateNCC.DataViewHolder> {

    private List<Nhacungcap> nhacungcaps;
    private Context context;

    public AdapterRecyclerViewCreateNCC(Context context, List<Nhacungcap> nhacungcaps) {
        this.context = context;
        this.nhacungcaps = nhacungcaps;
    }

    @Override
    public int getItemCount() {
        return nhacungcaps == null ? 0 : nhacungcaps.size();
    }

    @Override
    public AdapterRecyclerViewCreateNCC.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_dsncc, parent, false);


        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        Nhacungcap nhacungcap = nhacungcaps.get(position);

        String tenncc = nhacungcaps.get(position).getName();
        holder.tvTenNCC.setText(tenncc);
        String diachi = nhacungcaps.get(position).getDiachi();
        String textDiachi = "Địa chỉ: "+diachi;
        holder.tvdiachi.setText(textDiachi);

        String sdt = nhacungcaps.get(position).getSdt();
        String textSDT = "Số điện thoại: "+sdt;
        holder.tvSDT.setText(textSDT);

        String image = nhacungcaps.get(position).getImage();
        Picasso.with(context).load(image).into(holder.imgNCC);
        holder.imgNCC.setVisibility(View.VISIBLE);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(nhacungcap);
            }
        });

    }

    private void onClickGoToDetail(Nhacungcap nhacungcap){
        Intent intent = new Intent(context,DetailNhacungcap.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_nhacungcap", nhacungcap);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTenNCC, tvSDT;
        private TextView tvdiachi;
        private CircleImageView imgNCC;
        private LinearLayout layoutItem;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvTenNCC =  itemView.findViewById(R.id.tvTenNCC);
            tvdiachi =  itemView.findViewById(R.id.tvDiachiNCC);
            imgNCC   = itemView.findViewById(R.id.imgNCC);
            tvSDT = itemView.findViewById(R.id.tvSDT);
            layoutItem = itemView.findViewById(R.id.layoutNhacungcap);

        }
    }
}