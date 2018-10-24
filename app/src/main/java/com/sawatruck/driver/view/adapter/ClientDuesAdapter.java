package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.ClientDues;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class ClientDuesAdapter extends RecyclerView.Adapter<ClientDuesAdapter.MyViewHolder> {
    private ArrayList<ClientDues> clientDues = new ArrayList<>();
    private Context context;

    @Override
    public ClientDuesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.client_dues_item, parent, false);
        return new ClientDuesAdapter.MyViewHolder(v);
    }

    public ClientDuesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ClientDuesAdapter.MyViewHolder holder, final int position) {
        ClientDues clientDues = getClientDues().get(position);

        holder.txtAmount.setText(context.getString(R.string.amount).concat(" ").concat(clientDues.getAmount()));
        holder.txtLoadDetails.setText(clientDues.getLoadDetails());
        holder.txtOfferDetails.setText(clientDues.getOfferDetails());
        holder.txtDueDate.setText(clientDues.getDueDate());
        holder.txtFromUserFullName.setText(clientDues.getFromUserFirstName());
        holder.txtToUserFullName.setText(clientDues.getToUserFirstName());
        holder.txtTransactionDate.setText(clientDues.getTransactionDate());
    }

    @Override
    public int getItemCount() {
        return clientDues.size();
    }


    public void initializeAdapter() {
        this.clientDues = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<ClientDues> getClientDues() {
        return clientDues;
    }

    public void setClientDues(ArrayList<ClientDues> clientDues) {
        this.clientDues = clientDues;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_amount) CustomTextView txtAmount;
        @Bind(R.id.txt_load_details) CustomTextView txtLoadDetails;
        @Bind(R.id.txt_offer_details) CustomTextView txtOfferDetails;
        @Bind(R.id.txt_from_user_fullname) CustomTextView txtFromUserFullName;
        @Bind(R.id.txt_to_user_fullname) CustomTextView txtToUserFullName;
        @Bind(R.id.txt_transaction_date) CustomTextView txtTransactionDate;
        @Bind(R.id.txt_due_date) CustomTextView txtDueDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}