package com.example.lab12_ieti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private  String correo;
    private  String password;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo = ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString();
        password = ((EditText) findViewById(R.id.editTextTextPersonName2)).getText().toString();
    }
    public void loginUser(View view){
        if(correo.isEmpty()){
            ((EditText) findViewById(R.id.editTextTextPersonName)).setError("Ingrese email");
        }
        else{
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http:/10.0.2.2:8080") //localhost for emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthService autheticationService = retrofit.create(AuthService.class);
        executorService.execute(authenticate(autheticationService,correo,password));
    }
    private Runnable authenticate(AuthService authService, String correo, String password) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Response<Token> response =
                            authService.login(new LoginWrapper(correo, password)).execute();
                    Token token = response.body();

                    SharedPreferences sharedPref =
                            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TOKEN_KEY", token.getToken());
                    editor.commit();
                    if (token != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        finish();
                    } else {
                        ((EditText) findViewById(R.id.editTextTextPersonName)).setError("Verify Your email");
                        ((EditText) findViewById(R.id.editTextTextPersonName2)).setError("Verify Your Password");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
