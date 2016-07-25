//package edu.rit.wic.stressmonitor.requery;
//
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import io.requery.Persistable;
//import edu.rit.wic.stressmonitor.R;
//import edu.rit.wic.stressmonitor.requery.model.databinding.ActivityEditPersonBinding;
//import edu.rit.wic.stressmonitor.requery.model.Person;
//import edu.rit.wic.stressmonitor.requery.model.PersonEntity;
//import edu.rit.wic.stressmonitor.requery.model.Phone;
//import edu.rit.wic.stressmonitor.requery.model.PhoneEntity;
//import io.requery.rx.SingleEntityStore;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//
//public class ProfileEditActivity extends AppCompatActivity {
//
//    static final String EXTRA_PERSON_ID = "personId";
//
//    private SingleEntityStore<Persistable> data;
//    private PersonEntity person;
//    private ActivityEditPersonBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
//        toolbar.setTitle("Edit Profile");
//
//        data = ((UserApplication) getApplication()).getData();
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
//                    PersonEditActivity.this.person = person;
//                    binding.setPerson(person);
//                }
//            });
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_edit, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_save:
//                savePerson();
//                return true;
//        }
//        return false;
//    }
//
//    private void savePerson() {
//        // TODO make binding 2 way
//        person.setName(binding.name.getText().toString());
//        person.setEmail(binding.email.getText().toString());
//        Phone phone;
//        if (person.getPhoneNumberList().isEmpty()) {
//            phone = new PhoneEntity();
//            phone.setOwner(person);
//            person.getPhoneNumberList().add(phone);
//        } else {
//            phone = person.getPhoneNumberList().get(0);
//        }
//        phone.setPhoneNumber(binding.phone.getText().toString());
//
//        // save the person
//        if (person.getId() == 0) {
//            data.insert(person).subscribe(new Action1<Person>() {
//                @Override
//                public void call(Person person) {
//                    finish();
//                }
//            });
//        } else {
//            data.update(person).subscribe(new Action1<Person>() {
//                @Override
//                public void call(Person person) {
//                    finish();
//                }
//            });
//        }
//    }
//}
