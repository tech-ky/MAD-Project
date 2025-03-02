package com.sp.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.myViewHolder> {

    private List<UserInfo1> userList; // Updated to UserInfo1

    public MainAdapter(List<UserInfo1> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leadershipboardcell, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        UserInfo1 user = userList.get(position); // Updated to UserInfo1
        holder.username.setText(user.getUsername());
        holder.points.setText(String.valueOf(user.getPoints()));

        // Load profile picture (default profile image in this case)
        Glide.with(holder.profile.getContext())
                .load(R.drawable.profile)  // Use default profile picture
                .into(holder.profile);

        // Set rank based on position (after sorting)
        holder.rank.setText("Rank: #" + (position + 1));
    }

    @Override
    public int getItemCount() {
        return userList.size();  // Return the size of the user list
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView username, points, rank;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.imageView2);
            username = itemView.findViewById(R.id.Username);
            points = itemView.findViewById(R.id.CurrentPoints);
            rank = itemView.findViewById(R.id.Rank);
        }
    }
}
