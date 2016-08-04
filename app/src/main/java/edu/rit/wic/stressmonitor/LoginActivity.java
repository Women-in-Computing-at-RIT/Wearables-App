/**
 * File: LoginActivity.java
 * @author Cara Steinberg
 */
package edu.rit.wic.stressmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Optional;

import butterknife.ButterKnife;
import butterknife.BindView;
import edu.rit.wic.stressmonitor.requery.PeopleApplication;
import edu.rit.wic.stressmonitor.requery.model.PersonEntity;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.query.Tuple;
import io.requery.rx.SingleEntityStore;


/**
 * Activity logs the user in and redirects to the main activity.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_REGISTER = 0;
    private static final int REQUEST_FORGOT = 0;

    private SingleEntityStore<Persistable> data;
    String emailInput;
    String password;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_register) TextView _registerLink;
    @BindView(R.id.link_forgot_password) TextView _forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        data = ((PeopleApplication) getApplication()).getData();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        _loginButton.setOnClickListener((v) -> login());

        _registerLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), REQUEST_REGISTER));

        _forgotPasswordLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), ForgotPasswordActivity.class), REQUEST_FORGOT));
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

    public void login() {
        Log.d(TAG, "Login");

        if (!validateFields() || !validateUser()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess();
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

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validateFields() {
        boolean valid = true;
        emailInput = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        if (emailInput.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
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

    public boolean validateUser() {
        boolean valid = true;
        emailInput = _emailText.getText().toString();
        password = _passwordText.getText().toString();

//        Result<Tuple> emailResult = data.select(PersonEntity.EMAIL)
//                .where(PersonEntity.EMAIL.eq(emailInput)).get();
//        Result<Tuple> passwordResult = data.select(PersonEntity.PASSWORD)
//                .where(PersonEntity.PASSWORD.eq(password)).get();

        Result<PersonEntity> person = data.select(PersonEntity.class)
                                    .where(PersonEntity.EMAIL.eq(emailInput)
                                    .and(PersonEntity.PASSWORD.eq(password)).limit(1).get());

        Optional<PersonEntity> userOption = Optional.of(person.firstOrNull());

//        if (emailResult == null || !emailInput.equals(emailResult.toString())) {
//            valid = false;
//        }
//
//        if (passwordResult == null || !password.equals(passwordResult.toString())) {
//            valid = false;
//        }
        return valid;
    }
}