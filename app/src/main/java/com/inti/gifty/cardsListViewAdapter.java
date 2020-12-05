package com.inti.gifty;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class cardsListViewAdapter extends ArrayAdapter<Card> {

    private Context context;

    public cardsListViewAdapter(Context context, ArrayList<Card> cards){
        super(context, 0, cards);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Card card = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cards_list_item, parent, false);
        }

        TextView cardService = convertView.findViewById(R.id.card_service);
        TextView cardNumber = convertView.findViewById(R.id.card_number);
        TextView cardExpiryDate = convertView.findViewById(R.id.card_expiry_date);
        ImageView cardServiceImage = convertView.findViewById(R.id.card_service_image);

        cardService.setText(card.getService());
        cardNumber.setText("●●●● ●●●● ●●●● " + card.getId());
        cardExpiryDate.setText(card.getExpiryDate());
        if (card.getService().equals("Visa"))
            cardServiceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_visa));
        else if (card.getService().equals("Mastercard"))
            cardServiceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mastercard));
        else
            cardServiceImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_card));

        return convertView;
    }
}
