package com.adroitdevs.adroitapps.adroitiotservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitdevs.adroitapps.adroitiotservice.model.TokenPrefrences;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 777;
    EditText name, email, password, passwordKonf;
    TextView conti, create, terms, buttonGoogleText;
    SignInButton signInButton;
    ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "font/Lato-Light.ttf");
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        passwordKonf = (EditText) findViewById(R.id.passconf);
        conti = (TextView) findViewById(R.id.conti);
        create = (TextView) findViewById(R.id.create);
        terms = (TextView) findViewById(R.id.terms);
        signInButton = (SignInButton) findViewById(R.id.signInButton);
        buttonGoogleText = (TextView) signInButton.getChildAt(0);

        buttonGoogleText.setText("Masuk dengan Google");
        buttonGoogleText.setTypeface(customFont);
        name.setTypeface(customFont);
        email.setTypeface(customFont);
        password.setTypeface(customFont);
        passwordKonf.setTypeface(customFont);
        conti.setTypeface(customFont);
        create.setTypeface(customFont);
        terms.setTypeface(customFont);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        passwordKonf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getText() != passwordKonf.getText()) {
                    passwordKonf.setError("Password salah");
                } else {
                    passwordKonf.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || (passwordKonf.getText().toString().trim().isEmpty() && !passwordKonf.getText().toString().equals(password.getText().toString()))) {
                    Toast.makeText(getBaseContext(), "Harus diisi semua!", Toast.LENGTH_LONG).show();
                } else {
                    signUp();
                }
            }
        });
    }

    private void signUp() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = "http://angkatin.arkademy.com/LoginAwal/SignUp";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("status").equals("success")) {
                        Intent intent = new Intent(getBaseContext(), SigninActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (res.getString("status").equals("Already Exist")) {
                        Toast.makeText(getBaseContext(), "Already Exist!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Connection Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String emailText = String.valueOf(email.getText());
                String namaText = String.valueOf(name.getText());
                String passText = String.valueOf(password.getText());
                byte[] namaByte = new byte[0];
                byte[] emailByte = new byte[0];
                byte[] passByte = new byte[0];
                try {
                    namaByte = namaText.getBytes("UTF-8");
                    emailByte = emailText.getBytes("UTF-8");
                    passByte = passText.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String base64Nama = Base64.encodeToString(namaByte, Base64.DEFAULT);
                String base64Email = Base64.encodeToString(emailByte, Base64.DEFAULT);
                String base64Pass = Base64.encodeToString(passByte, Base64.DEFAULT);
                Map<String, String> body = new HashMap<>();
                body.put("email", base64Email);
                body.put("name", base64Nama);
                body.put("password", base64Pass);
                return body;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                handleSignInResult(result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) throws UnsupportedEncodingException {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String email = account.getEmail();
                String nama = account.getDisplayName();
                byte[] namaByte = nama.getBytes("UTF-8");
                byte[] emailByte = email.getBytes("UTF-8");
                final String base64Nama = Base64.encodeToString(namaByte, Base64.DEFAULT);
                final String base64Email = Base64.encodeToString(emailByte, Base64.DEFAULT);
                String url = "http://angkatin.arkademy.com/LoginAwal/withGmail";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("status").equals("success")) {
                                TokenPrefrences.setToken(getBaseContext(), res.getString("token"));
                                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> body = new HashMap<>();
                        body.put("name", base64Nama);
                        body.put("email", base64Email);
                        return body;
                    }
                };
                VolleySingleton.getInstance(this).addToRequestQueue(request);
            }
        } else {
            Toast.makeText(this, "Login Fail", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
        Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }
}
