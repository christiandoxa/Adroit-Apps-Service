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
    EditText _nameText;
    @InjectView(R.id.etEmail)
    EditText _emailText;
    @InjectView(R.id.etPassword)
    EditText _passText;
    @InjectView(R.id.bSignUp)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
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

        _signupButton.setEnabled(false);
        final ProgressDialog pd = new ProgressDialog(SignupActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        pd.setIndeterminate(true);
        pd.setMessage("Authenticating...");
        pd.show();

        String nama = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String pass = _passText.getText().toString();

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
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG);
        _signupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String pass = _passText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 charactes");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 25) {
            _passText.setError("between 4 and 25 alphanumeric characters");
            valid = false;
        } else {
            _passText.setError(null);
        }
        return valid;
    }
}
