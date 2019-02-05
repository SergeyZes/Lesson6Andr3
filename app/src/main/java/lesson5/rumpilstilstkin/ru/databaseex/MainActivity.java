package lesson5.rumpilstilstkin.ru.databaseex;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String EXT_TIME = "ext_time";
    private final static String EXT_COUNT = "ext_count";
    private WorkWithDB workWithDB;

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    Button btnLoad;
    Button btnSaveAllSugar;
    Button btnSelectAllSugar;
    Button btnDeleteAllSugar;
    Button btnSaveAllRealm;
    Button btnSelectAllRealm;
    Button btnDeleteAllRealm;
    Button btnSaveAllRoom;
    Button btnSelectAllRoom;
    Button btnDeleteAllRoom;
    Button btnSaveAllSQLite;
    Button btnSelectAllSQLite;
    Button btnDeleteAllSQLite;
    Button btnSaveAllRealmAsync;
    Button btnSelectAllRealmAsync;
    Button btnDeleteAllRealmAsync;

    List<Model> modelList = new ArrayList<>();
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        workWithDB = new WorkWithDB(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    private void initViews() {
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        btnLoad = findViewById(R.id.btnLoad);
        btnSaveAllSugar = findViewById(R.id.btnSaveAllSugar);
        btnSelectAllSugar = findViewById(R.id.btnSelectAllSugar);
        btnDeleteAllSugar = findViewById(R.id.btnDeleteAllSugar);
        btnSaveAllRealm = findViewById(R.id.btnSaveAllRealm);
        btnSelectAllRealm = findViewById(R.id.btnSelectAllRealm);
        btnDeleteAllRealm = findViewById(R.id.btnDeleteAllRealm);
        btnSaveAllRoom = findViewById(R.id.btnSaveAllRoom);
        btnSelectAllRoom = findViewById(R.id.btnSelectAllRoom);
        btnDeleteAllRoom = findViewById(R.id.btnDeleteAllRoom);
        btnSaveAllSQLite = findViewById(R.id.btnSaveAllSQLite);
        btnSelectAllSQLite = findViewById(R.id.btnSelectAllSQLite);
        btnDeleteAllSQLite = findViewById(R.id.btnDeleteAllSQLite);
        btnSaveAllRealmAsync = findViewById(R.id.btnSaveAllRealmAsync);
        btnSelectAllRealmAsync = findViewById(R.id.btnSelectAllRealmAsync);
        btnDeleteAllRealmAsync = findViewById(R.id.btnDeleteAllRealmAsync);


        btnLoad.setOnClickListener(this);
        btnSaveAllSugar.setOnClickListener(this);
        btnSelectAllSugar.setOnClickListener(this);
        btnDeleteAllSugar.setOnClickListener(this);
        btnSaveAllRealm.setOnClickListener(this);
        btnSelectAllRealm.setOnClickListener(this);
        btnDeleteAllRealm.setOnClickListener(this);
        btnSaveAllRoom.setOnClickListener(this);
        btnSelectAllRoom.setOnClickListener(this);
        btnDeleteAllRoom.setOnClickListener(this);
        btnSaveAllSQLite.setOnClickListener(this);
        btnSelectAllSQLite.setOnClickListener(this);
        btnDeleteAllSQLite.setOnClickListener(this);
        btnSaveAllRealmAsync.setOnClickListener(this);
        btnSelectAllRealmAsync.setOnClickListener(this);
        btnDeleteAllRealmAsync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                loadData();
                break;
            case R.id.btnSaveAllSugar:
                execute(this::saveShugar);
                break;
            case R.id.btnSelectAllSugar:
                execute(this::getAllShugar);
                break;
            case R.id.btnDeleteAllSugar:
                execute(this::deleteAllShugar);
                break;
            case R.id.btnSaveAllRealm:
                execute(this::saveRealm);
                break;
            case R.id.btnSelectAllRealm:
                execute(this::getAllRealm);
                break;
            case R.id.btnDeleteAllRealm:
                execute(this::deleteAllRealm);
                break;
            case R.id.btnSaveAllRoom:
                execute(this::saveRoom);
                break;
            case R.id.btnSelectAllRoom:
                execute(this::getAllRoom);
                break;
            case R.id.btnDeleteAllRoom:
                execute(this::deleteAllRoom);
                break;
            case R.id.btnSaveAllSQLite:
                execute(this::saveSQLite);
                break;
            case R.id.btnSelectAllSQLite:
                execute(this::getAllSQLite);
                break;
            case R.id.btnDeleteAllSQLite:
                execute(this::deleteAllSQLite);
                break;
            case R.id.btnSaveAllRealmAsync:
                saveRealm2();
                break;
            case R.id.btnSelectAllRealmAsync:
                getAllRealm2();
                break;
            case R.id.btnDeleteAllRealmAsync:
                deleteAllRealm2();
                break;
        }
    }

    private DisposableSingleObserver<Bundle> createObserver() {
        return new DisposableSingleObserver<Bundle>() {

            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }

            @Override
            public void onSuccess(@NonNull Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + bundle.getInt(EXT_COUNT) +
                        "\n милисекунд = " + bundle.getLong(EXT_TIME));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }

    private void loadData() {
        mInfoTextView.setText("");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

        if (networkinfo != null && networkinfo.isConnected()) {
            // запускаем
            progressBar.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<List<Model>> call = retrofit.create(Endpoints.class).loadUsers();
            downloadOneUrl(call);
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Call<List<Model>> call) {
        call.enqueue(new Callback<List<Model>>() {

            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Model curModel = null;
                        mInfoTextView.append("\n Size = " + response.body().size() +
                                "\n-----------------");
                        for (int i = 0; i < response.body().size(); i++) {
                            curModel = response.body().get(i);
                            modelList.add(curModel);
                            mInfoTextView.append(
                                    "\nLogin = " + curModel.getLogin() +
                                            "\nId = " + curModel.getUserId() +
                                            "\nURI = " + curModel.getAvatar() +
                                            "\n-----------------");
                        }
                    }
                } else {
                    System.out.println("onResponse error: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                System.out.println("onFailure " + t);
                mInfoTextView.setText("onFailure " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void execute(Callable<Bundle> call) {
        Single<Bundle> singleDeleteAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
            try {
                emitter.onSuccess(call.call());
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        singleDeleteAll.subscribeWith(createObserver());
    }

    private Bundle saveShugar() {
        Date first = new Date();
        for (Model curItem : modelList) {
            new SugarModel(
                    curItem.getLogin(),
                    curItem.getUserId(),
                    curItem.getAvatar()
            ).save();
        }
        Date second = new Date();
        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle getAllShugar() {
        Date first = new Date();
        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllShugar() {
        Date first = new Date();
        int count = SugarModel.deleteAll(SugarModel.class);
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, count);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle saveRealm() {
        realm = Realm.getDefaultInstance();
        Date first = new Date();
        realm.executeTransaction(realm1 -> {
            for (Model curItem : modelList) {
                try {
                    RealmModel realmModel = realm1.createObject(RealmModel.class);
                    realmModel.setUserID(curItem.getUserId());
                    realmModel.setLogin(curItem.getLogin());
                    realmModel.setAvatarUrl(curItem.getAvatar());
                } catch (Exception e) {
                    realm1.cancelTransaction();
                    realm1.close();
                    throw e;
                }
            }
        });
        Date second = new Date();
        long count = realm.where(RealmModel.class).count();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, (int) count);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private void saveRealm2() {
        mInfoTextView.setText("");
        realm = Realm.getDefaultInstance();
        Date first = new Date();

        realm.executeTransactionAsync(realm1 -> {
            for (Model curItem : modelList) {
                RealmModel realmModel = realm1.createObject(RealmModel.class);
                realmModel.setUserID(curItem.getUserId());
                realmModel.setLogin(curItem.getLogin());
                realmModel.setAvatarUrl(curItem.getAvatar());
            }

        }, () -> {
            Date second = new Date();
            long count = realm.where(RealmModel.class).count();
            progressBar.setVisibility(View.GONE);
            mInfoTextView.append("количество = " + count +
                    "\n милисекунд = " + (second.getTime() - first.getTime()));
            realm.close();

        }, error -> {
            System.out.println("onFailure " + error.getMessage());
            mInfoTextView.setText("onFailure " + error.getMessage());
            progressBar.setVisibility(View.GONE);
            realm.close();

        });

    }

    private void deleteAllRealm2() {
        mInfoTextView.setText("");
        realm = Realm.getDefaultInstance();

        final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAllAsync();
        tempList.addChangeListener(realmModels -> {
            int size = realmModels.size();
            Date first = new Date();
            tempList.removeAllChangeListeners();

            realm.executeTransactionAsync(realm1 -> {
                realm1.deleteAll();
                //realmModels.deleteAllFromRealm();
            }, () -> {
                Date second = new Date();
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + size +
                        "\n милисекунд = " + (second.getTime() - first.getTime()));
                realm.close();

            }, error -> {
                System.out.println("onFailure " + error.getMessage());
                mInfoTextView.setText("onFailure " + error.getMessage());
                progressBar.setVisibility(View.GONE);
                realm.close();

            });

        });
    }

    private void getAllRealm2() {
        mInfoTextView.setText("");
        realm = Realm.getDefaultInstance();
        Date first = new Date();

        final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAllAsync();
        tempList.addChangeListener(realmModels -> {
            int size = realmModels.size();
            Date second = new Date();
            progressBar.setVisibility(View.GONE);
            mInfoTextView.append("количество = " + size +
                    "\n милисекунд = " + (second.getTime() - first.getTime()));
            realm.close();
        });
    }


    private Bundle getAllRealm() {
        realm = Realm.getDefaultInstance();
        Date first = new Date();
        RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private Bundle deleteAllRealm() {
        realm = Realm.getDefaultInstance();
        final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
        int size = tempList.size();
        Date first = new Date();
        realm.executeTransaction(realm -> tempList.deleteAllFromRealm());
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, size);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        realm.close();
        return bundle;
    }

    private Bundle saveRoom() {
        UserDao userDao = MainApp.getDb().userDao();
        Date first = new Date();
        for (Model curItem : modelList) {
            userDao.insert(new Users(
                    curItem.getLogin(),
                    curItem.getUserId(),
                    curItem.getAvatar()
            ));
        }
        Date second = new Date();
        List<Users> tempList = userDao.getAll();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle saveSQLite() {
        Date first = new Date();
        SQLiteDatabase db = workWithDB.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for (Model curItem : modelList) {
            cv.put(WorkWithDB.COLUMN_USER_ID, curItem.getUserId());
            cv.put(WorkWithDB.COLUMN_USER_LOGIN, curItem.getLogin());
            cv.put(WorkWithDB.COLUMN_USER_AVATAR, curItem.getAvatar());
            db.insert(WorkWithDB.TABLE_USERS, null, cv);
        }

        Cursor c = db.query(WorkWithDB.TABLE_USERS, new String[]{WorkWithDB.COLUMN_USER_ID}, null, null, null, null, null);
        int cnt = c.getCount();
        db.close();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, cnt);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllSQLite() {
        Date first = new Date();
        SQLiteDatabase db = workWithDB.getWritableDatabase();

        db.delete(WorkWithDB.TABLE_USERS, "1", null);

        Cursor c = db.query(WorkWithDB.TABLE_USERS, new String[]{WorkWithDB.COLUMN_USER_ID}, null, null, null, null, null);
        int cnt = c.getCount();
        db.close();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, cnt);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }


    private Bundle getAllSQLite() {
        Date first = new Date();
        SQLiteDatabase db = workWithDB.getWritableDatabase();
        Cursor c = db.query(WorkWithDB.TABLE_USERS, new String[]{WorkWithDB.COLUMN_USER_ID}, null, null, null, null, null);
        int cnt = c.getCount();
        db.close();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, cnt);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }


    private Bundle getAllRoom() {
        UserDao userDao = MainApp.getDb().userDao();

        Date first = new Date();
        List<Users> tempList = userDao.getAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, tempList.size());
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }

    private Bundle deleteAllRoom() {
        UserDao userDao = MainApp.getDb().userDao();

        Date first = new Date();
        int size = userDao.deleteAll();
        Date second = new Date();
        Bundle bundle = new Bundle();
        bundle.putInt(EXT_COUNT, size);
        bundle.putLong(EXT_TIME, second.getTime() - first.getTime());
        return bundle;
    }
}
