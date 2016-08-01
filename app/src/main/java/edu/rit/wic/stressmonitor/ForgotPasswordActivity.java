/**
 * File: ForgotPasswordActivity.java
 * @author Cara Steinberg
 */
package edu.rit.wic.stressmonitor;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Activity sends email to reset password if user has forgotten their password.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private static final int REQUEST_REGISTER = 0;
    private static final int REQUEST_LOGIN = 0;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.btn_forgot_pass) Button _sendButton;
    @BindView(R.id.forgot_register) TextView _registerLink;
    @BindView(R.id.forgot_login) TextView _loginLink;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        _sendButton.setOnClickListener((v) -> sendResetLink());

        _registerLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), REQUEST_REGISTER));

        _loginLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN));
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Hide settings menu item
        menu.findItem(R.id.action_settings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
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
