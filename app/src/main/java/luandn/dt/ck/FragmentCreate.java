package luandn.dt.ck;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreate extends Fragment {

    EditText editTextName, editTextEmail, editTextPassword;
    Button btnCreate;
    TextView textViewLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    FragmentCreate() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        mAuth = FirebaseAuth.getInstance();

        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        btnCreate = view.findViewById(R.id.buttonCreate);
        textViewLogin = view.findViewById(R.id.textViewLogin);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail.getText().clear();
                editTextPassword.getText().clear();
                editTextName.getText().clear();
                getParentFragmentManager().popBackStack();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                name = editTextName.getText().toString();

                if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                    Toast.makeText(getContext(), "Please fill in the blank fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(password.toCharArray().length >= 6) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Name", name);
                                    db.collection(email).document("userInfo").set(user);

                                    Toast.makeText(getContext(), "Create account successfully", Toast.LENGTH_LONG).show();
                                    editTextEmail.getText().clear();
                                    editTextPassword.getText().clear();
                                    editTextName.getText().clear();
                                }
                                else {
                                    if (task.getException().getMessage() == "The email address is badly formatted.") {
                                        Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (task.getException().getMessage() == "The email address is already in use by another account.") {
                                        Toast.makeText(getContext(), "The email address is already in use by another account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(getContext(), "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
        }

        return true;
    }
}
