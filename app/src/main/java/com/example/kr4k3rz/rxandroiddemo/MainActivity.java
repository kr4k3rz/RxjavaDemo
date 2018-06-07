package com.example.kr4k3rz.rxandroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getName();

    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*MAP() operator transforms the items of an observable and emits the modified items*/


        getUsersObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<User, ObservableSource<User>>() {
            @Override
            public ObservableSource<User> apply(User user) {
                return getAddressObservable(user);
            }
        }).subscribe(new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.d(TAG, "onNext: " + "Name : " + user.getName() + " Gender : " + user.getGender() + " Address: " + user.getAddress().getAddress());

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "Error : " + e.getMessage());

            }

            @Override
            public void onComplete() {

                Log.d(TAG, "onComplete");
            }
        });


    }


    Observable<User> getUsersObservable() {
        String[] names = {"Aman", "Ajay", "Rahul", "Abhisekh", "Rohit", "Gayle"};
        final List<User> userList = new ArrayList<>();
        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("Male");
            userList.add(user);

        }


        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                for (User user : userList) {
                    if (!emitter.isDisposed())
                        emitter.onNext(user);
                }

                if (!emitter.isDisposed())
                    emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    Observable<User> getAddressObservable(final User user) {
        final String[] addresses = {"1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142"};

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Address address = new Address();  //maps the User item
                address.setAddress(addresses[new Random().nextInt(2) + 0]);
                if (!emitter.isDisposed())
                    user.setAddress(address);
                int sleepTime = new Random().nextInt(1000) + 500;   //generates for network latency
                Thread.sleep(sleepTime);
                emitter.onNext(user);  //set to next user reactive
                emitter.onComplete();  //signals a completion

            }
        }).subscribeOn(Schedulers.io());
    }

}
