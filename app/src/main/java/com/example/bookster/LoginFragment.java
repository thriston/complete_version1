package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {
    EditText emailInput,passwordInput;
    TextView textView;
    ViewPager viewPager;
    private Button loginBtn;
    NavigationView navigationView;
    ProductPictureSlider adapter;
    private DrawerLayout drawer;
    Toolbar toolbar;
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**Inflate the layout for this fragment   **/
        View v =inflater.inflate(R.layout.fragment_login, container, false);
        textView=v.findViewById(R.id.signUpLinkTextView);
        loginBtn= v.findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        emailInput=v.findViewById(R.id.emailAddressEditText2);
        passwordInput=v.findViewById(R.id.passwordEditText2);
        textView.setTextColor(Color.parseColor("#411FC7"));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        /**Organizes the toolbar  **/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        /**  Signup link on login page **/
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getContext(),SignUp.class);
                startActivity(i);
            }
        });


        return v;
    }

    public void signIn(final View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(getContext(), "Email Address field is Empty", Toast.LENGTH_SHORT).show();
        }

        if(password.isEmpty()){
            Toast.makeText(getContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /**Sign in success, update UI with the signed-in user's information   **/
                            Toast.makeText(getContext(), "Logged In", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            getActivity().setResult(getActivity().RESULT_OK, null);

                            Fragment fragment = new HomeFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.commit();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            Intent intent2 = getActivity().getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);
                            startActivity(intent2);

                        } else {
                            /** If sign in fails, display a message to the user.  **/
                            Toast.makeText(getContext(), "Sign in Failed - Username or Password Incorrect", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    public void sendPasswordReset(View view) {
        // [START send_password_reset]
        final String emailAddress = emailInput.getText().toString();
        if(emailAddress.isEmpty()){
            Toast.makeText(getContext(), "Please enter your email address to being password reset", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "An email was sent to " + emailAddress + " to reset your password", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "This email is not registered to an existing account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END send_password_reset]
    }

}
