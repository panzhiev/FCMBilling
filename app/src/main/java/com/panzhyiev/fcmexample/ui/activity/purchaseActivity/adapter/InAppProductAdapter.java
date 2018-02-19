package com.panzhyiev.fcmexample.ui.activity.purchaseActivity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.db.room.entity.CoinBuyPojo;
import com.panzhyiev.fcmexample.iab.sample2.InAppProduct;
import com.panzhyiev.fcmexample.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InAppProductAdapter extends RecyclerView.Adapter<InAppProductAdapter.ViewHolder> {

    private List<InAppProduct> mCoinBuyPojoList;
    OnBuyClickListener mOnBuyClickListener;

    public interface OnBuyClickListener {
        void onBuyClicked(InAppProduct inAppProduct);
    }

    public InAppProductAdapter(List<InAppProduct> coinBuyPojoList, OnBuyClickListener listener) {
        mCoinBuyPojoList = coinBuyPojoList;
        mOnBuyClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin_buy, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        InAppProduct inAppProduct = mCoinBuyPojoList.get(position);

        holder.mTvSymbol.setText(inAppProduct.productId.toUpperCase());
        holder.mTvName.setText(inAppProduct.storeDescription);
        holder.mTvAmount.setText(inAppProduct.price);
        holder.mBtnBuy.setOnClickListener(view -> mOnBuyClickListener.onBuyClicked(inAppProduct));
        holder.mIvIcon.setImageBitmap(Utils.getBitmapFromCryptoIconsAssets(holder.mContext, inAppProduct.productId));
    }

    @Override
    public int getItemCount() {
        return mCoinBuyPojoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void reloadList(ArrayList<InAppProduct> list) {
        mCoinBuyPojoList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //bind views here
        @BindView(R.id.tv_symbol_currency)
        TextView mTvSymbol;

        @BindView(R.id.tv_name_currency)
        TextView mTvName;

        @BindView(R.id.tv_amount_for_sale)
        TextView mTvAmount;

        @BindView(R.id.iv_crypto_icon)
        ImageView mIvIcon;

        @BindView(R.id.btn_buy_coins_item)
        Button mBtnBuy;

        Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }
    }
}
