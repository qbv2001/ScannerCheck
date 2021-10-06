package com.example.scannercheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

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
        int soluongmh = mathangs.get(position).getSoluong();
        String textSoluong = "Số lượng: "+soluongmh;
        holder.tvSoluongMH.setText(textSoluong);
        int image = mathangs.get(position).getImage();
        holder.imgMH.setImageResource(image);
        holder.imgMH.setVisibility(View.VISIBLE);

        String datetime = mathangs.get(position).getDateTime();
        String textDate = "Ngày cập nhật: ";
        if(datetime!=null){
            textDate += datetime;
        }
        holder.tvDateTime.setText(textDate);

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
        private TextView tvSoluongMH;
        private RoundedImageView imgMH;
        private TextView tvDateTime;
        private LinearLayout layoutItem;
        public DataViewHolder(View itemView) {
            super(itemView);

            tvTenMH =  itemView.findViewById(R.id.tvTenMH);
            tvSoluongMH =  itemView.findViewById(R.id.tvSoluongMH);
            imgMH   = itemView.findViewById(R.id.imgMH);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            layoutItem = itemView.findViewById(R.id.layoutMathang);
        }
    }
}