package com.sp.mad_project.placeholder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sp.mad_project.Add_food;
import com.sp.mad_project.Profile;
import com.sp.mad_project.R;
import com.sp.mad_project.Voucher;
import com.sp.mad_project.leadershipboard;


public class MoreFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        // Find the CardView by ID
        View cardLeaderboard = rootView.findViewById(R.id.card_leaderboard);
        View cardGym = rootView.findViewById(R.id.gym);
        View cardVoucher = rootView.findViewById(R.id.voucher);
        View cardUser = rootView.findViewById(R.id.profile);

        // Set onClickListeners for each card
        cardLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), leadershipboard.class);
            startActivity(intent);
        });

        cardGym.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Map.class);
            startActivity(intent);

        });

        cardVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Voucher.class);
            startActivity(intent);
        });

        cardUser.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Profile.class);
            startActivity(intent);
        });

        return rootView;
    }

}