
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


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    private static final int RC_SIGN_IN = 0;
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
    private boolean googleAttempt = false;

    //google auth
    private GoogleApiClient mGoogleApiClient;
    //private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = new InfoSesion(this);
        setContentView(R.layout.activity_login);
        final Button logout = (Button) findViewById(R.id.logout_button);
        logout.setVisibility(View.GONE);
        final Button button_continue = (Button) findViewById(R.id.continue_button);
        button_continue.setVisibility(View.GONE);

        api = new API(this);
        facade = new Facade(api);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) updateUI(2);

        i1 = new Intent(this, Perfil_Profesor.class);
        i0 = new Intent(this, Busqueda_Profesores.class);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        final Button mEmailSignInButtonProf = (Button) findViewById(R.id.log_in_button);
        mEmailSignInButtonProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                signIn();
            }
        });


        final Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_continue();
            }
        });


        //Code for google auth system
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final com.google.android.gms.common.SignInButton googleSignInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.google_sign_in_button);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn();
            }
        });

        //end of code for google auth

    }
    //method for google auth
    private void GoogleSignIn() {
        updateUI(0);
        googleAttempt = true;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //method for google auth
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(1);
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method for google auth
    private void authWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (task.isSuccessful() && user != null) {
                            googleSuccessful(user);
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(1);
                        }
                    }
                });
    }

    private void googleSuccessful(FirebaseUser user) {
        String password = "ZwgMAEioujBT4fZPAizP"; //PASSWORD IN OLD CLOUD FOR ALL GOOGLE-USERS
        String email = user.getEmail();
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
            }catch (APIexception e1) { //si falla... significa que el usuario es nuevo: registro
                signUp();
            }

        }

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
        updateUI(0);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
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
                                    updateUI(1);
                                }

                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(1);
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

    private void updateUI(int code) {
        if (code == 0 ) {
            Toast.makeText(LoginActivity.this, "Loading...",
                    Toast.LENGTH_SHORT).show();
            findViewById(R.id.email).setVisibility(View.GONE);
            findViewById(R.id.password).setVisibility(View.GONE);
            findViewById(R.id.log_in_button).setVisibility(View.GONE);
            findViewById(R.id.register_button).setVisibility(View.GONE);
            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
        } else if (code == 1){
            findViewById(R.id.email).setVisibility(View.VISIBLE);
            findViewById(R.id.password).setVisibility(View.VISIBLE);
            findViewById(R.id.log_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.register_button).setVisibility(View.VISIBLE);
            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logout_button).setVisibility(View.GONE);
            findViewById(R.id.continue_button).setVisibility(View.GONE);
        } else {//onResume
            findViewById(R.id.email).setVisibility(View.GONE);
            findViewById(R.id.password).setVisibility(View.GONE);
            findViewById(R.id.log_in_button).setVisibility(View.GONE);
            findViewById(R.id.register_button).setVisibility(View.GONE);
            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
            findViewById(R.id.continue_button).setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!googleAttempt) {
            if (mAuth.getCurrentUser() != null) updateUI(2);
            else updateUI(1);
        } else {
            Toast.makeText(LoginActivity.this, "Loading...",
                Toast.LENGTH_SHORT).show();
            googleAttempt = false;}
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(LoginActivity.this, "Session closed.",
                Toast.LENGTH_SHORT).show();
        updateUI(1);
    }

    private void button_continue(){
        info = new InfoSesion(this);
        int prof = info.getTipo();
        if(prof == 0) startActivity(i0);
        else startActivity(i1);
    }
}