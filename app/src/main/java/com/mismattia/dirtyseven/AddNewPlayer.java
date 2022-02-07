package com.mismattia.dirtyseven;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;

public class AddNewPlayer extends BottomSheetDialogFragment {
    private EditText mEditText;
    private DatabaseHelper myDB;
    private ArrayList<Player> players;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_player, container, false);
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
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
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
                Toast.makeText(getActivity(), "بازیکنی با این نام قبلا اضافه شده است", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        return alreadyAdded;
    }
}
