package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Transaction;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 9/14/2017.
 */


public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {
    private ArrayList<Transaction> transactions = new ArrayList<>();

    private Context context;

    @Override
    public TransactionHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.transaction_history_item, parent, false);
        return new TransactionHistoryAdapter.MyViewHolder(v);
    }

    public TransactionHistoryAdapter() {

    }

    public TransactionHistoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(TransactionHistoryAdapter.MyViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);


        holder.txtTransactionDate.setText(transaction.getDate());
        holder.txtTransactionType.setText(transaction.getTransactionTypeText());



        try {

            double Value = Double.valueOf(transaction.getTransactionDetail().get(0).getValue());
            if (Value >= 0)
                holder.imgTransactionType.setImageResource(R.drawable.ico_up_money);
            else
                holder.imgTransactionType.setImageResource(R.drawable.ico_down_money);
            Value = Math.abs(Value);
            holder.txtBalance.setText(String.valueOf(Value));

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public ArrayList<Transaction> getTransactionList() {
        return transactions;
    }

    public void setTransactionList(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void initializeAdapter() {
        this.transactions = new ArrayList<>();
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_transaction_type) ImageView imgTransactionType;
        @Bind(R.id.txt_balance) CustomTextView txtBalance;
        @Bind(R.id.txt_transaction_type) CustomTextView txtTransactionType;
        @Bind(R.id.txt_transaction_date) CustomTextView txtTransactionDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
