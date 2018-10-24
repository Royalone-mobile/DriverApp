package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sawatruck.driver.R;
import com.sawatruck.driver.repository.DBController;
import com.sawatruck.driver.repository.LoadSearchDAO;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */

public class SavedSearchAdapter extends RecyclerView.Adapter<SavedSearchAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LoadSearchDAO> loadSearchs = new ArrayList<>();

    public SavedSearchClickListener getSavedSearchClickListener() {
        return savedSearchClickListener;
    }

    public void setSavedSearchClickListener(SavedSearchClickListener savedSearchClickListener) {
        this.savedSearchClickListener = savedSearchClickListener;
    }

    public interface SavedSearchClickListener{
        void onSavedSearchClick(LoadSearchDAO searchItem);
    }

    private SavedSearchClickListener savedSearchClickListener;
    @Override
    public SavedSearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.save_search_item, parent, false);
        return new SavedSearchAdapter.MyViewHolder(v);
    }

    public SavedSearchAdapter() {

    }

    public SavedSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(SavedSearchAdapter.MyViewHolder holder, final int position) {

        LoadSearchDAO loadSearchDAO = loadSearchs.get(position);
        String loadLocation = loadSearchDAO.getLoadLocation();
        String deliveryLocation = loadSearchDAO.getDeliveryLocation();
        String strLoadCity = loadLocation.substring(loadLocation.lastIndexOf(",")+1, loadLocation.length()-1);
        String strDeliveryCity = deliveryLocation.substring(deliveryLocation.lastIndexOf(",") + 1, deliveryLocation.length()-1);

        holder.txtSearchName.setText(loadSearchDAO.getSearchName());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadSearchDAO loadSearchDAO = loadSearchs.get(position);
                DBController dbController = DBController.getInstance().open(context);
                dbController.deleteSearch(loadSearchDAO);
                loadSearchs.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadSearchDAO loadSearchDAO = loadSearchs.get(position);
                savedSearchClickListener.onSavedSearchClick(loadSearchDAO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loadSearchs.size();
    }

    public void setLoadSearchs(ArrayList<LoadSearchDAO> loadSearchs) {
        this.loadSearchs = loadSearchs;
    }

    public ArrayList<LoadSearchDAO> getLoadSearchs() {
        return loadSearchs;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btn_remove) View btnRemove;
        @Bind(R.id.txt_search_name) TextView txtSearchName;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}