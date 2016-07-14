package edu.rit.wic.stressmonitor;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private static final int REQUEST_REGISTER = 0;
    private static final int REQUEST_LOGIN = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.btn_forgot_pass) Button _sendButton;
    @Bind(R.id.forgot_register) TextView _registerLink;
    @Bind(R.id.forgot_login) TextView _loginLink;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        _sendButton.setOnClickListener((v) -> sendResetLink());

        _registerLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), REQUEST_REGISTER));

        _loginLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN));
    }

    public void sendResetLink() {
        Log.d(TAG, "ForgotPassword");

        if (!validateFields()) {
            onSendResetLinkFailed();
            return;
        }

        _sendButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onSendResetLinkSuccess();
                        // onSendResetLinkFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public void onSendResetLinkSuccess() {
        _sendButton.setEnabled(true);
        finish();
    }

    public void onSendResetLinkFailed() {
        Toast.makeText(getBaseContext(), "Password reset link failed to send", Toast.LENGTH_LONG).show();

        _sendButton.setEnabled(true);
    }

    public boolean validateFields() {
        boolean valid = true;
        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        return valid;
    }
}
