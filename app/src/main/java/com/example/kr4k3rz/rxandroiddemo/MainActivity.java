package com.example.kr4k3rz.rxandroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

        getObservableUsers().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Function<User, User>() {
            @Override
            public User apply(User user) throws Exception {
                user.setEmail(String.format("%s@rxjava.com", user.name));
                user.setName(user.name.toUpperCase());
                return user;
            }
        }).subscribe(new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.d(TAG, "onNext : Name " + user.getName() + " Email : " + user.getEmail() + " Gender : " + user.getGender());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");

            }
        });


    }


    Observable<User> getObservableUsers() {
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
}
