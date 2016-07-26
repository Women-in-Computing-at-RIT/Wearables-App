package edu.rit.wic.stressmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by Cara on 7/26/2016.
 */
public class ProfileInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
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

        Spinner genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
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

        Spinner ethnicitySpinner = (Spinner) findViewById(R.id.ethnicity_spinner);
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
}


