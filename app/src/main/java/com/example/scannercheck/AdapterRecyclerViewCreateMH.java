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
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerViewCreateMH extends RecyclerView.Adapter<AdapterRecyclerViewCreateMH.DataViewHolder> {

    private List<Mathang> mathangs;
    private Context context;

    public AdapterRecyclerViewCreateMH(Context context, List<Mathang> mathangs) {
        this.context = context;
        this.mathangs = mathangs;
    }

    @Override
    public int getItemCount() {
        return mathangs == null ? 0 : mathangs.size();
    }

    @Override
    public AdapterRecyclerViewCreateMH.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_dsmh, parent, false);


        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        Mathang mathang = mathangs.get(position);
        String id = mathangs.get(position).getId();
//        holder.tvTenMH.setText(id);
        String tenmh = mathangs.get(position).getName();
        holder.tvTenMH.setText(tenmh);

        List<Donvitinh> donvitinhs = mathangs.get(position).getDonvitinhs();

        for(Donvitinh donvitinh : donvitinhs) {
            if(donvitinh.getQuydoi()==1){
                holder.tvDvtMH.setText("Đơn vị tính: " + donvitinh.getTendvt());
                holder.tvGiaMH.setText("Giá bán: " + String.format("%.0f", donvitinh.getDongia()));
            }
        }

        // Dung picasso để load ảnh
        String image = mathangs.get(position).getImage();
        Picasso.with(context).load(image).into(holder.imgMH);
        holder.imgMH.setVisibility(View.VISIBLE);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(mathang);
            }
        });
    }

    private void onClickGoToDetail(Mathang mathang){
        Intent intent = new Intent(context,DetailMathang.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_mathang", mathang);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTenMH;
        private TextView tvDvtMH;
        private CircleImageView imgMH;
        private TextView tvGiaMH;
        private LinearLayout layoutItem;
        public DataViewHolder(View itemView) {
            super(itemView);

            tvTenMH =  itemView.findViewById(R.id.tvTenMH);
            tvDvtMH =  itemView.findViewById(R.id.tvDvtMH);
            imgMH   = itemView.findViewById(R.id.imgMH);
            tvGiaMH = itemView.findViewById(R.id.tvGiaMH);
            layoutItem = itemView.findViewById(R.id.layoutMathang);
        }
    }
}