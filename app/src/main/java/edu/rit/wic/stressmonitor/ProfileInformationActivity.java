package edu.rit.wic.stressmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.antlr.v4.runtime.misc.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Cara on 7/26/2016.
 */
public class ProfileInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final int REQUEST_LOGIN = 0;
    @Bind(R.id.input_first_name)EditText _firstNameText;
    @Bind(R.id.input_last_name) EditText _lastNameText;
    @Bind(R.id.input_birthday) EditText _birthdayText;
    @Bind(R.id.input_phone_number) EditText _phoneNumberText;
    @Bind(R.id.btn_submit_info) Button _submitButton;
    @Bind(R.id.link_login) TextView _loginLink;
    private Calendar myCalendar;
    private Spinner genderSpinner;
    private Spinner ethnicitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        ButterKnife.bind(this);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        _phoneNumberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        myCalendar = Calendar.getInstance();
        _birthdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileInformationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };

        // Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderAdapter.add("Female");
        genderAdapter.add("Male");
        genderAdapter.add("Gender");
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(genderAdapter.getCount());
        genderSpinner.setOnItemSelectedListener(this);

        ethnicitySpinner = (Spinner) findViewById(R.id.ethnicity_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> ethnicityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };
        // Specify the layout to use when the list of choices appears
        ethnicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicityAdapter.add("White/Caucasian");
        ethnicityAdapter.add("Black/African American");
        ethnicityAdapter.add("Hispanic or Latino");
        ethnicityAdapter.add("Asian/Asian American");
        ethnicityAdapter.add("American Indian or Alaskan Native");
        ethnicityAdapter.add("Native Hawaiian or Other Pacific Islander");
        ethnicityAdapter.add("Ethnicity");
        // Apply the adapter to the spinner
        ethnicitySpinner.setAdapter(ethnicityAdapter);
        ethnicitySpinner.setSelection(ethnicityAdapter.getCount());
        ethnicitySpinner.setOnItemSelectedListener(this);

        _submitButton.setOnClickListener((v) -> submitForm());
        _loginLink.setOnClickListener((v) ->
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN));
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        _birthdayText.setText(sdf.format(myCalendar.getTime()));
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Spinner spinner = (Spinner) parent;
        // An item was selected. You can retrieve the selected item using
        if(spinner.getId() == R.id.gender_spinner) {
            spinner.getItemAtPosition(pos);
        } else if (spinner.getId() == R.id.ethnicity_spinner) {
            spinner.getItemAtPosition(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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

    public boolean validateFields() {
        boolean valid = true;
        Date today = new Date();
        String firstName = _firstNameText.getText().toString();
        String lastName = _lastNameText.getText().toString();
//        Date birthday = (Date) _birthdayText.getText();
        String phone_number = _phoneNumberText.getText().toString();

        if (firstName.isEmpty() || firstName.length() < 3) {
            _firstNameText.setError("Must be at least 3 characters");
            valid = false;
        } else {
            _firstNameText.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() < 3) {
            _lastNameText.setError("Must be at least 3 characters");
            valid = false;
        } else {
            _lastNameText.setError(null);
        }

//        if (birthday == null) {
//            _birthdayText.setError("Required!");
//            valid = false;
//        } else if (birthday.after(today)) {
//            _birthdayText.setError("Invalid Date");
//            valid = false;
//        } else {
//            _birthdayText.setError(null);
//        }

        if (phone_number.isEmpty()) {
            _phoneNumberText.setError("Must be 10 digits");
            valid = false;
        } else {
            _phoneNumberText.setError(null);
        }

        if (genderSpinner == null || genderSpinner.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(),"Must select one", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
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

    public void submitForm() {
        if (!validateFields()) {
            onSubmitFailed();
            return;
        }
        _submitButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting...");
        progressDialog.show();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onRegisterSuccess or onRegisterFailed
                        // depending on success
                        onSubmitSuccess();
                        // onRegisterFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSubmitSuccess() {
        _submitButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onSubmitFailed() {
        Toast.makeText(getBaseContext(), "Failed to Submit", Toast.LENGTH_LONG).show();
        _submitButton.setEnabled(true);
    }
}


