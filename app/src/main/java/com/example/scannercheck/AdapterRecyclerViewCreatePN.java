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

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerViewCreatePN extends RecyclerView.Adapter<AdapterRecyclerViewCreatePN.DataViewHolder> {

    private List<PhieuNhap> phieuNhaps;
    private Context context;

    public AdapterRecyclerViewCreatePN(Context context, List<PhieuNhap> phieuNhaps) {
        this.context = context;
        this.phieuNhaps = phieuNhaps;
    }

    @Override
    public int getItemCount() {
        return phieuNhaps == null ? 0 : phieuNhaps.size();
    }

    @Override
    public AdapterRecyclerViewCreatePN.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_dsnhaphang, parent, false);


        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        PhieuNhap phieuNhap = phieuNhaps.get(position);
        String id = phieuNhap.getMaphieu();
//        holder.tvTenMH.setText(id);
        String tenmh = phieuNhap.getTenmh();
        holder.tvTenMH.setText(tenmh);

        // Tạo đối tượng Calendar và thiết lập thời gian
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(phieuNhap.getNgaynhap());

        // Tạo định dạng ngày tháng năm
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Chuyển đổi đối tượng Calendar sang chuỗi định dạng ngày tháng năm
                String date = sdf.format(calendar.getTime());


        holder.tvDvtMH.setText("Ngày nhập: " + date +"  Nhà cung cấp: " + phieuNhap.getTenncc());
        holder.tvGiaMH.setText("Đvt: "+ phieuNhap.getTendvt() + "  Giá nhập: " + String.format("%.0f", phieuNhap.getDongia())+"  Số lượng: "+ phieuNhap.getSoluong() + "  Thành tiền: "+String.format("%.0f", phieuNhap.getThanhtien()));

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click đến trang chi tiết
            }
        });
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