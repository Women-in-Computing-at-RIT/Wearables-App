package edu.rit.wic.stressmonitor;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

//    @Bind(R.id.input_first_name) EditText _firstNameText;
//    @Bind(R.id.input_last_name) EditText _lastNameText;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_register) Button _registerButton;
    @Bind(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        _registerButton.setOnClickListener((v) -> register());

        // Finish the registration screen and return to the Login activity
        _loginLink.setOnClickListener((v) -> finish());

    }

    public void register() {
        Log.d(TAG, "Register");

        if (!validateFields()) {
            onRegisterFailed();
            return;
        }

        _registerButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onRegisterSuccess or onRegisterFailed
                        // depending on success
                        onRegisterSuccess();
                        // onRegisterFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onRegisterSuccess() {
        _registerButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();
        _registerButton.setEnabled(true);
    }

    public boolean validateFields() {
        boolean valid = true;

//        String firstName = _firstNameText.getText().toString();
//        String lastName = _lastNameText.getText().toString();
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Must be at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

//        if (firstName.isEmpty() || firstName.length() < 3) {
//            _firstNameText.setError("Must be at least 3 characters");
//            valid = false;
//        } else {
//            _firstNameText.setError(null);
//        }
//
//        if (lastName.isEmpty() || lastName.length() < 3) {
//            _lastNameText.setError("Must be at least 3 characters");
//            valid = false;
//        } else {
//            _lastNameText.setError(null);
//        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}