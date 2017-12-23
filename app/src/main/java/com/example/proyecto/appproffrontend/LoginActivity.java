
package com.example.proyecto.appproffrontend;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
 import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // Nuevo codigo de Adrian
    //FirebaseDatabase database= FirebaseDatabase.getInstance("Users");
    //DatabaseReference myRef = database.getReference();



    // Acaba nuevo codigo de Adrian
    private static final String TAG = null;
    // Instancia la api una vez en la clase
    API api;
    private Facade facade;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    /*private View mProgressView;
    private View mLoginFormView;*/
    private InfoSesion info;
    private FirebaseAuth mAuth;
    private Intent i1, i0;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = new InfoSesion(this);
        api = new API(this);
        facade = new Facade(api);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        i1 = new Intent(this, Perfil_Profesor.class);
        i0 = new Intent(this, Busqueda_Profesores.class);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButtonProf = (Button) findViewById(R.id.log_in_button);
        mEmailSignInButtonProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                signIn();
            }
        });


        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signUp(mEmailView.getText().toString(), mPasswordView.getText().toString());
                signUp();
            }
        });
    }
    private void signUp(){
           Intent i = new Intent(this, Registro1.class);
           startActivity(i);

    }


    private void signIn() {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            try {//intententamos login como profesor
                                facade.login(new PersonaVO(email,password),1);
                                info.set(email,1);
                                info.setSession(user);
                                startActivity(i1);
                            } catch (APIexception e) { //si falla...
                                try {//intentamos login como alumno
                                    facade.login(new PersonaVO(email,password),0);
                                    info.set(email,0);
                                    info.setSession(user);
                                    startActivity(i0);
                                }catch (APIexception e1) {
                                    mEmailView.setError("Something went wrong.");
                                    mAuth.signOut();
                                }

                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            try {
                                throw e;
                            } catch(FirebaseAuthInvalidCredentialsException  e1) {
                                mEmailView.setError("Invalid email/pass combination.");
                                mPasswordView.setError("Invalid email/pass combination.");
                            } catch(Exception e1) {
                                mEmailView.setError("Something went wrong.");
                                mPasswordView.setError("Something went wrong.");
                            }
                        }

                    }
                });
        // [END sign_in_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Required.");
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Required.");
            valid = false;
        } else if (password.length() < 4) {
            mPasswordView.setError("Too short.");
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
           // mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,user.getEmail(), user.isEmailVerified()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            //findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email).setVisibility(View.GONE);
            findViewById(R.id.password).setVisibility(View.GONE);
            findViewById(R.id.log_in_button).setVisibility(View.GONE);

           // findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
           // mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            //findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email).setVisibility(View.VISIBLE);
            findViewById(R.id.password).setVisibility(View.VISIBLE);
            findViewById(R.id.log_in_button).setVisibility(View.VISIBLE);
        }
    }

}