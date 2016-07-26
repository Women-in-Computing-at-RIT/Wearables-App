package edu.rit.wic.stressmonitor.requery;

import android.widget.EditText;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.rit.wic.stressmonitor.R;
import edu.rit.wic.stressmonitor.requery.model.Person;
import edu.rit.wic.stressmonitor.requery.model.PersonEntity;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import rx.Observable;


public class CreateUser implements Callable<Observable<Iterable<Person>>> {
//    @Bind(R.id.input_name)
//    EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;

    private final SingleEntityStore<Persistable> data;

    public CreateUser(SingleEntityStore<Persistable> data) {
        this.data = data;
    }

    @Override
    public Observable<Iterable<Person>> call() {
//        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        final Set<Person> people = new TreeSet<>(new Comparator<Person>() {
            @Override
            public int compare(Person lhs, Person rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        PersonEntity person = new PersonEntity();
//        person.setName(name);
        person.setUUID(UUID.randomUUID());
        person.setEmail(email);
        person.setPassword(password);
        people.add(person);
        return data.insert(people).toObservable();
    }
}
