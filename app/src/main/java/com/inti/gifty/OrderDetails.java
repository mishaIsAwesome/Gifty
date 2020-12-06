package com.inti.gifty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OrderDetails extends Fragment {

    private TextView orderReceivedDateText, orderETAText, orderDeliveringText, orderDeliveringToText, giftReceivedText;
    private ImageButton backButton;
    private ImageView iconTrolley, iconTruck, iconGift, dotsTrolley, dotsTruck, dotsGift;
    private LinearLayout etaLayout, deliveringLayout, giftReceivedLayout;

    Order order;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        if (getArguments() != null)
            order = (Order) getArguments().getSerializable("order");

        orderReceivedDateText = view.findViewById(R.id.order_received_date);
        orderETAText = view.findViewById(R.id.order_eta_date);
        orderDeliveringText = view.findViewById(R.id.order_delivering_date);
        orderDeliveringToText = view.findViewById(R.id.order_delivering_to);
        giftReceivedText = view.findViewById(R.id.gift_received_date);
        backButton = view.findViewById(R.id.back_button);

        iconTrolley = view.findViewById(R.id.icon_trolley);
        iconTruck = view.findViewById(R.id.icon_truck);
        iconGift = view.findViewById(R.id.icon_gift);
        dotsTrolley = view.findViewById(R.id.dots_trolley);
        dotsTruck  = view.findViewById(R.id.dots_truck);
        dotsGift = view.findViewById(R.id.dots_gift);
        etaLayout = view.findViewById(R.id.order_preparing_layout);
        deliveringLayout = view.findViewById(R.id.order_delivering_layout);
        giftReceivedLayout = view.findViewById(R.id.gift_received_layout);

        fillDeliveryStatus();

        RecyclerView orderItemsRecyclerView = view.findViewById(R.id.order_items_recycler_view);
        orderItemsRecyclerAdapter adapter = new orderItemsRecyclerAdapter(getActivity(), order.getFriend().getWishlist());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        orderItemsRecyclerView.setLayoutManager(mLayoutManager);
        orderItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        orderItemsRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        return view;
    }

    public void fillDeliveryStatus() {
        orderReceivedDateText.setText(order.getDateOrdered());
        orderETAText.setText(order.getDateETA());
        orderDeliveringText.setText(order.getDateSent());
        orderDeliveringToText.setText("To " + order.getFriend().getName());
        giftReceivedText.setText(order.getDateReceived());

        if (!order.getDateETA().equals("TBA")){
            etaLayout.setVisibility(View.VISIBLE);
            iconTrolley.setVisibility(View.VISIBLE);
            dotsTrolley.setVisibility(View.VISIBLE);
        }
        if (!order.getDateSent().equals("TBA")){
            deliveringLayout.setVisibility(View.VISIBLE);
            iconTruck.setVisibility(View.VISIBLE);
            dotsTruck.setVisibility(View.VISIBLE);
        }
        if (!order.getDateReceived().equals("TBA")){
            giftReceivedLayout.setVisibility(View.VISIBLE);
            iconGift.setVisibility(View.VISIBLE);
            dotsGift.setVisibility(View.VISIBLE);
        }

    }
}