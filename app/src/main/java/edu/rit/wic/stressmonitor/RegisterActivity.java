package edu.rit.wic.stressmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.rit.wic.stressmonitor.requery.CreateUser;
import edu.rit.wic.stressmonitor.requery.PeopleApplication;
import edu.rit.wic.stressmonitor.requery.model.Person;
import edu.rit.wic.stressmonitor.requery.model.PersonEntity;
import edu.rit.wic.stressmonitor.databinding.ActivityRegisterBinding;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RegisterActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 0;
    private static final String TAG = "RegisterActivity";
    static final String EXTRA_PERSON_ID = "personId";
    private SingleEntityStore<Persistable> data;
//    private ExecutorService executor;
//    private PersonAdapter adapter;
    private PersonEntity person;
    private ActivityRegisterBinding binding;


//    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_confirm_password) EditText _confirmPasswordText;
    @Bind(R.id.btn_register) Button _registerButton;
    @Bind(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        data = ((PeopleApplication) getApplication()).getData();

//        int personId = getIntent().getIntExtra(EXTRA_PERSON_ID, -1);
//        if (personId == -1) {
//            person = new PersonEntity(); // creating a new person
//            binding.setPerson(person);
//        } else {
//            data.findByKey(PersonEntity.class, personId)
//            .subscribeOn(AndroidSchedulers.mainThread())
//            .subscribe(new Action1<PersonEntity>() {
//                @Override
//                public void call(PersonEntity person) {
//                    RegisterActivity.this.person = person;
//                    binding.setPerson(person);
//                }
//            });
//        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        _registerButton.setOnClickListener((v) -> register());
        _loginLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN));

//        data = ((PeopleApplication) getApplication()).getData();
//        executor = Executors.newSingleThreadExecutor();
//        adapter = new PersonAdapter();
//        adapter.setExecutor(executor);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public void onRegisterSuccess() {
//        savePerson();
        _registerButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, ProfileInformationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();
        _registerButton.setEnabled(true);
    }

    public boolean validateFields() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();

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

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            _confirmPasswordText.setError("Password does not match");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void savePerson() {
        // TODO make binding 2 way
        person.setEmail(binding.inputEmail.getText().toString());
        person.setPassword(binding.inputPassword.getText().toString());
        // save the person
        if (person.getId() == 0) {
            data.insert(person).subscribe(new Action1<Person>() {
                @Override
                public void call(Person person) {
                    finish();
                }
            });
        } else {
            onRegisterFailed();
        }
    }

//    @Override
//    protected void onResume() {
//        adapter.queryAsync();
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        executor.shutdown();
//        adapter.close();
//        super.onDestroy();
//    }
}