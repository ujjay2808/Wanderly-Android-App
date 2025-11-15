package com.ujjay.wanderly.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.models.Trip;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;
    private OnTripClickListener onTripClickListener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
        void onTripLongClick(Trip trip);
    }

    public TripAdapter(List<Trip> tripList, OnTripClickListener onTripClickListener) {
        this.tripList = tripList;
        this.onTripClickListener = onTripClickListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.bind(trip);

        // Set different emojis based on destination
        String destination = trip.getDestination().toLowerCase();
        if (destination.contains("beach") || destination.contains("bali") || destination.contains("mumbai")) {
            holder.icon.setText("🏖️");
        } else if (destination.contains("paris") || destination.contains("france")) {
            holder.icon.setText("🗼");
        } else if (destination.contains("london") || destination.contains("uk")) {
            holder.icon.setText("🇬🇧");
        } else if (destination.contains("mountain") || destination.contains("hike")) {
            holder.icon.setText("⛰️");
        } else if (destination.contains("new york") || destination.contains("nyc")) {
            holder.icon.setText("🗽");
        } else if (destination.contains("tokyo") || destination.contains("japan")) {
            holder.icon.setText("🗾");
        } else {
            holder.icon.setText("✈️");
        }
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void setTrips(List<Trip> trips) {
        this.tripList = trips;
        notifyDataSetChanged();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView destination;
        private TextView itinerary;
        private TextView icon;
        private CardView tripCard;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            destination = itemView.findViewById(R.id.trip_destination);
            itinerary = itemView.findViewById(R.id.trip_itinerary);
            icon = itemView.findViewById(R.id.trip_icon);
            tripCard = itemView.findViewById(R.id.trip_card);

            tripCard.setOnClickListener(v -> {
                if (onTripClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onTripClickListener.onTripClick(tripList.get(getAdapterPosition()));
                }
            });

            tripCard.setOnLongClickListener(v -> {
                if (onTripClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onTripClickListener.onTripLongClick(tripList.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }

        public void bind(Trip trip) {
            destination.setText(trip.getDestination());

            // Show first 100 characters of itinerary as preview
            String itineraryText = trip.getItinerary();
            if (itineraryText.length() > 100) {
                itineraryText = itineraryText.substring(0, 100) + "...";
            }
            itinerary.setText(itineraryText);
        }
    }
}