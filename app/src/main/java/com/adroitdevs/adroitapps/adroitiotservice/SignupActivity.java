package com.adroitdevs.adroitapps.adroitiotservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.etNama)
    EditText nameText;
    @InjectView(R.id.etEmail)
    EditText emailText;
    @InjectView(R.id.etPassword)
    EditText passText;
    @InjectView(R.id.bSignUp)
    Button signupButton;
    @InjectView(R.id.link_login)
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    private void signup() {
        if (!validate()) {
            onSignUpFailed();
            return;
        }

        signupButton.setEnabled(false);
        final ProgressDialog pd = new ProgressDialog(SignupActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        pd.setIndeterminate(true);
        pd.setMessage("Authenticating...");
        pd.show();

        String nama = nameText.getText().toString();
        String email = emailText.getText().toString();
        String pass = passText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        onSignUpSuccess();
                        pd.dismiss();
                    }
                }, 3000
        );
    }

    private void onSignUpSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String pass = passText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 charactes");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 25) {
            passText.setError("between 4 and 25 alphanumeric characters");
            valid = false;
        } else {
            passText.setError(null);
        }
        return valid;
    }
}
