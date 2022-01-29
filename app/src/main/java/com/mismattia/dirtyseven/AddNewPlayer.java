package com.mismattia.dirtyseven;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;

public class AddNewPlayer extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewPlayer";

    private EditText mEditText;
    private DatabaseHelper myDB;
    ArrayList<Player> players;

    public static AddNewPlayer newInstance() {
        return new AddNewPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_new_player, container, false);

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.editText);
        myDB = new DatabaseHelper(getActivity());
        players = new ArrayList<>();

        boolean isUpdate = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            if(bundle.getBoolean("is_update")) {
                isUpdate = true;
                String name = bundle.getString("name");
                mEditText.setText(name);
            }

            players = bundle.getParcelableArrayList("players");
        }

        final boolean finalIsUpdate = isUpdate;
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String text = mEditText.getText().toString();

                    if(text.equals("")) {
                        Toast.makeText(getActivity().getApplicationContext(), "لطفا نام بازیکن را وارد کنید", Toast.LENGTH_SHORT).show();
                    } else if(! playerAlreadyAdded(players, text)) {
                        if (finalIsUpdate) {
                                myDB.updatePlayer(bundle.getInt("id"), text);

                        } else {
                                Player item = new Player();
                                item.setName(text);
                                myDB.insertPlayer(item);
                        }
                    }

                    dismiss();
                    handled = true;
                }
                return handled;
            }
        });


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();

        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }

    private boolean playerAlreadyAdded(ArrayList<Player> players, String name) {
        boolean alreadyAdded = false;

        for(Player player: players) {
            if (player.getName().equals(name)) {
                alreadyAdded = true;
                Toast.makeText(getActivity(), "این بازیکن قبلا اضافه شده است", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        return alreadyAdded;
    }
}
