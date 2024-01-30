package luandn.dt.ck;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    TextView forgotPassword, createNewAccount;

    ActionBar actionBar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;

    FragmentRoom fragmentRoom;
    FragmentCreate fragmentCreate;
    FragmentForgotPassword fragmentForgotPassword;
    private FirebaseAuth mAuth;

    public FragmentLogin(ActionBar actionBar, ActionBarDrawerToggle actionBarDrawerToggle, FloatingActionButton fab, DrawerLayout drawerLayout) {
        this.actionBar = actionBar;
        this.actionBarDrawerToggle = actionBarDrawerToggle;
        this.fab = fab;
        this.drawerLayout = drawerLayout;

        // tao fragmentRoom
        fragmentCreate = new FragmentCreate();
        fragmentForgotPassword = new FragmentForgotPassword();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        forgotPassword = view.findViewById(R.id.textViewForgotPassword);
        createNewAccount = view.findViewById(R.id.textViewCreate);
        btnLogin = view.findViewById(R.id.buttonLogin);

        fab.hide();
        actionBar.hide();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragmentForgotPassword)
                        .addToBackStack("")
                        .commit();
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragmentCreate)  //goi fragment
                        .addToBackStack("") // them vao stack
                        .commit();  //hien thi fragment duoc goi
            };

        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                if(TextUtils.isEmpty(password) && TextUtils.isEmpty(email)){
                    Toast.makeText(getContext(), "Please fill full information!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            fragmentRoom = new FragmentRoom(actionBar, actionBarDrawerToggle, fab, email);
                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameLayout, fragmentRoom)    // thay the tren layout hien tai bang fragmentRoom
                                    .commit();   // bat dau thay the
                        }
                        else{
                            Toast.makeText(getContext(), "Login unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    public FragmentRoom getFragmentRoom() {
        return fragmentRoom;
    }
}
