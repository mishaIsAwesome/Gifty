package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardForm extends Fragment {

    DatabaseReference dbUsers;

    EditText nameTextField, cardNumberTextField, expiryTextField, cvvTextField;
    Button submitButton;
    ImageView cardProviderImage;
    ImageButton backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_form, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("cards");

        nameTextField = view.findViewById(R.id.card_name_edit_text);
        cardNumberTextField = view.findViewById(R.id.card_number_edit_text);
        expiryTextField = view.findViewById(R.id.card_expiry_edit_text);
        cvvTextField = view.findViewById(R.id.card_cvv_edit_text);
        submitButton = view.findViewById(R.id.submit_button);
        cardProviderImage = view.findViewById(R.id.card_service_image);
        backButton = view.findViewById(R.id.back_button);

        cardNumberTextField.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_CHARS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_CHARS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                    if (Character.getNumericValue(s.charAt(0)) == 5){
                        if (getContext() != null)
                            cardProviderImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_mastercard));
                    } else if (Character.getNumericValue(s.charAt(0)) == 4){
                        if (getContext() != null)
                            cardProviderImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_visa));
                    }
                }
            }
        });

        expiryTextField.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_CHARS = 5; // 00/00
            private static final int TOTAL_DIGITS = 4; // max numbers of digits in pattern: 00 x 2
            private static final int DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
            private static final char DIVIDER = '/';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_CHARS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }
        });

        cvvTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        return view;
    }

    private void addCard() {
        String name = nameTextField.getText().toString().trim();
        String number = cardNumberTextField.getText().toString().trim();
        String service = "Card";
        String expiryDate = expiryTextField.getText().toString().trim();
        String cvv = cvvTextField.getText().toString().trim();
        if (Character.getNumericValue(number.charAt(0)) == 4)
            service = "Visa";
        else if (Character.getNumericValue(number.charAt(0)) == 5)
            service = "Mastercard";

        if (checkFields(name, number, expiryDate, cvv)){
            String id = number.substring(15);
            Card newCard = new Card(id, name, number, service, expiryDate, cvv);
            dbUsers.child(id).setValue(newCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getActivity(), "Card added!", Toast.LENGTH_SHORT).show();
                        goBack();
                    } else {
                        Toast.makeText(getActivity(), "Card couldn't be added at this moment, try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean checkFields(String name, String number, String expiryDate, String cvv){
        if (TextUtils.isEmpty(name)){
            nameTextField.setError("Name is required.");
            return false;
        }

        if (TextUtils.isEmpty(number)){
            cardNumberTextField.setError("Card number is required.");
            return false;
        }

        if (TextUtils.isEmpty(expiryDate)){
            expiryTextField.setError("Expiry date is required.");
            return false;
        }

        if (TextUtils.isEmpty(cvv)){
            cvvTextField.setError("CVV is required.");
            return false;
        }

        return true;
    }

    private boolean isInputCorrect(Editable s, int totalChars, int dividerModulo, char divider) {
        boolean isCorrect = s.length() <= totalChars; // total length less/equals to TOTAL_CHARS
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect &= divider == s.charAt(i); // isCorrect true if isCorrect is true AND i is DIVIDER
            } else {
                isCorrect &= Character.isDigit(s.charAt(i)); // isCorrect true if isCorrect is true AND i is a digit
            }
        }
        return isCorrect;
    }

    private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }
        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    public void goBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }
}