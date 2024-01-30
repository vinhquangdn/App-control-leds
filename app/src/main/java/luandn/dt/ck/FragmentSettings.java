package luandn.dt.ck;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import luandn.dt.ck.Interfaces.UpdateNameRoom;

public class FragmentSettings extends Fragment {
    ActionBar actionBar;
    FloatingActionButton fab;
    String email;
    UpdateNameRoom updateNameRoom;

    FirebaseAuth mAuth;
    TextView textViewName;
    String name;

    public FragmentSettings(ActionBar actionBar, FloatingActionButton fab, String email, String name, UpdateNameRoom updateNameRoom) {
        this.actionBar = actionBar;
        this.fab = fab;
        this.email = email;
        this.name = name;
        this.updateNameRoom = updateNameRoom;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_settings, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        actionBar.setTitle("Settings"); // dat tieu de cho actionBar
        fab.hide();

        textViewName = view.findViewById(R.id.textViewNameSetting);
        textViewName.setText(name);
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeName();
            }
        });

        TextView textViewEmail = view.findViewById(R.id.textViewEmailSetting);
        textViewEmail.setText(email);

        TextView textViewChangePassword = view.findViewById(R.id.textViewChangePasswordSetting);
        textViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Reset password link has been sent to your registered email", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    void ChangeName() {
        Dialog dialog = new Dialog(getContext());   //tao dialog
        dialog.getWindow().setBackgroundDrawable(actionBar.getThemedContext().getDrawable(R.drawable.custom_dialog));
        dialog.setContentView(R.layout.dialog_infor);    //truyen content cho dialog
        dialog.setCanceledOnTouchOutside(false);    //khong tat dialog khi nhan ra ngoai

        TextView textViewTitle = dialog.findViewById(R.id.textViewDialogInfo);
        EditText editTextName = dialog.findViewById(R.id.editTextDialogInfo);
        Button buttonYes = dialog.findViewById(R.id.buttonYesDialogInfo);
        Button buttonNo = dialog.findViewById(R.id.buttonNoDialogInfo);

        textViewTitle.setText("Change name");
        editTextName.setText(name);

        dialog.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                textViewName.setText(name);
                updateNameRoom.UpdateNameRoom(name);
                dialog.cancel();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
}
