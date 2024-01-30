package luandn.dt.ck;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentForgotPassword extends Fragment {
    EditText edtEmail;
    Button btnResetPassword, btnBack;
    FirebaseAuth mAuth;
    String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mAuth = FirebaseAuth.getInstance();

        btnResetPassword = view.findViewById(R.id.buttonResetPassword);
        btnBack = view.findViewById(R.id.buttonBack);
        edtEmail = view.findViewById(R.id.editTextEmail);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEmail.getText().clear();
                getParentFragmentManager().popBackStack();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                if(!email.isEmpty()){
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
                else{
                    Toast.makeText(getContext(), "Please fill your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
