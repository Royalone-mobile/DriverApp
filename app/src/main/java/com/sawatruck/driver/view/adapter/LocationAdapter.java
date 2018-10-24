package com.sawatruck.driver.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.City;
import com.sawatruck.driver.entities.Country;
import com.sawatruck.driver.view.activity.ActivityCities;
import com.sawatruck.driver.view.design.CustomTextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 10/5/2017.
 */

public class LocationAdapter extends SectioningAdapter {
    private Context context;

    public LocationAdapter(){

    }

    public LocationAdapter(Context context){
        this.context = context;
    }


    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public void initialize() {
        setSections(new ArrayList<Section>());
        notifyAllSectionsDataSetChanged();
    }


    public static class Section {
        private int index;
        private Country country = new Country();
        private ArrayList<City> cities = new ArrayList<>();
        public Section(){

        }

        public ArrayList<City> getCities() {
            return cities;
        }

        public void setCities(ArrayList<City> cities) {
            this.cities = cities;
        }

        public Country getCountry() {
            return country;
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public void appendSection(Section section){
        this.sections.add(section);
        notifyAllSectionsDataSetChanged();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    private ArrayList<Section> sections = new ArrayList<>();


    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.city_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.search_location_header, parent, false);
        return new HeaderViewHolder(v);
    }


    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.get(sectionIndex).getCities().size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        Section section = sections.get(sectionIndex);
        final City city = section.getCities().get(itemIndex);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.txtCityName.setText(city.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCities activityCities = (ActivityCities)context;
                activityCities.setCurrentCityName(city.getName());
                activityCities.setCurrentLatLng(new LatLng(city.getLatitude(),city.getLongitude()));
                activityCities.toggleShowCityLocation();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section section = sections.get(sectionIndex);
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        holder.txtCountryName.setText(section.getCountry().getCountryName());
        try {
            BaseApplication.getPicasso().load(section.getCities().get(0).getCountryFlagUrl()).into(holder.imgCountryFlag);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
        @Bind(R.id.txt_city_name) CustomTextView txtCityName;
        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @Bind(R.id.img_photo) ImageView imgCountryFlag;
        @Bind(R.id.txt_name) CustomTextView txtCountryName;
        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

}
