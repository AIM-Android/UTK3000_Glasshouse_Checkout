package com.advantech.utk3000_glasshouse_checkout.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.advantech.utk3000_glasshouse_checkout.R;
import com.advantech.utk3000_glasshouse_checkout.entity.Product;
import com.advantech.utk3000_glasshouse_checkout.ui.ImageUtil;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static final String TAG = ProductAdapter.class.getSimpleName();

    private List<Product> dataList;
    private final Context context;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(List<Product> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_checkout_result, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product bean = dataList.get(position);
        if (bean != null) {
            Log.d(TAG, "bean : " + bean);
            holder.iconImageView.setImageBitmap(ImageUtil.loadImageFromAssets(context, bean.getImgPath()));
            holder.productTextView.setText(bean.getName());
            holder.priceTextView.setText(String.valueOf(bean.getPrice()));
            holder.countTextView.setText(String.valueOf(bean.getCount()));
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView productTextView;
        TextView priceTextView;
        TextView countTextView;
        ImageButton deleteImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iv_image);
            productTextView = itemView.findViewById(R.id.tv_product);
            priceTextView = itemView.findViewById(R.id.tv_price);
            countTextView = itemView.findViewById(R.id.tv_count);
            deleteImageButton = itemView.findViewById(R.id.ib_delete);
        }
    }
}