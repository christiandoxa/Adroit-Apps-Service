package com.adroitdevs.adroitapps.adroitiotservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ButterKnife.inject(this);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                login();
//            }
//        });
//
//        signupLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(i, REQUEST_SIGNUP);
//            }
//        });
    }

//    private void login() {
//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }
//        loginButton.setEnabled(false);
//        final ProgressDialog pd = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
//        pd.setIndeterminate(true);
//        pd.setMessage("Authenticating...");
//        pd.show();
//
//        String emailUser = emailText.getText().toString();
//        String pass = passText.getText().toString();
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoginSuccess();
//                        pd.dismiss();
//                    }
//                }, 3000
//        );
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

//    private void onLoginSuccess() {
//        loginButton.setEnabled(true);
//        finish();
//    }

//    private void onLoginFailed() {
//        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
//        loginButton.setEnabled(true);
//    }

//    private boolean validate() {
//        boolean valid = true;
//        String emailUser = emailText.getText().toString();
//        String pass = passText.getText().toString();
//
//        if (emailUser.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
//            emailText.setError(("Enter a valid emailUser address"));
//            valid = false;
//        } else {
//            emailText.setError(null);
//        }
//
//        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 15) {
//            passText.setError(("between 4 and 15 alphanumeric characters"));
//            valid = false;
//        } else {
//            passText.setError(null);
//        }
//
//        return valid;
//    }
}
