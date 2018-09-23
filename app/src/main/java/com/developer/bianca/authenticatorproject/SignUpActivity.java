package com.developer.bianca.authenticatorproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.bianca.authenticatorproject.Utils.Constants;
import com.developer.bianca.authenticatorproject.domain.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    FileOutputStream fileOutputStream;

    EditText nameField, emailField, passwordField, passwordConfirmField, cpfField;
    boolean isNameValid, isEmailValid, isPasswordValid, isPasswordConfirmedValid, isCpfValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = findViewById(R.id.name_edit_text);
        emailField = findViewById(R.id.email_to_login_et);
        passwordField = findViewById(R.id.password_edit_text);
        passwordConfirmField = findViewById(R.id.confirm_password_et);
        cpfField = findViewById(R.id.cpf_edit_text);

        nameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && (!nameField.getText().toString().matches("[a-zA-Z ]+") || nameField.getText().toString().trim().equals(""))) {
                    nameField.setError("Somente letras podem compor o nome. Campo obrigatório.");
                    isNameValid = false;
                } else {
                    isNameValid = true;
                }
            }
        });

        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && ((isEmailValid(emailField.getText().toString())) == false)) {
                    emailField.setError("Campo obrigatório. Digite um e-mail válido");
                    isEmailValid = false;
                } else {
                    isEmailValid = true;
                }
            }
        });

        final String password = passwordField.getText().toString();
        final String passwordConfirm = passwordConfirmField.getText().toString();
        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && ((!password.equals(passwordConfirm)) || (passwordField.getText().toString().trim().equals("")))) {
                    passwordField.setError("Campo obrigatório. As senhas devem ser iguais.");
                    isPasswordValid = false;
                } else {
                    isPasswordValid = true;
                }
            }
        });

        passwordConfirmField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && ((!password.equals(passwordConfirm)) || (passwordField.getText().toString().trim().equals("")))) {
                    passwordConfirmField.setError("Campo obrigatório. As senhas devem ser iguais.");
                    isPasswordConfirmedValid = false;
                } else {
                    isPasswordConfirmedValid = true;
                }
            }
        });

        //TODO: validar campo CPF.
        cpfField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && cpfField.getText().toString().trim().equals("")) {
                    cpfField.setError("Campo obrigatório");
                    isCpfValid = false;
                } else {
                    isCpfValid = true;
                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public void newUser(View view) {

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordConfirm = passwordConfirmField.getText().toString();
        String cpf = cpfField.getText().toString();

        //FIXME: implement the function to save the data on txt for the local storage of the user.
        if (isNameValid && isEmailValid && isPasswordValid && isPasswordConfirmedValid && isCpfValid) {
            try {
                //SALVAR NA MEMÓRIA INTERNA
                fileOutputStream = openFileOutput(String.valueOf(Constants.USERS_FILENAME), Context.MODE_APPEND | Context.MODE_PRIVATE);

                final User user = new User(name, email, password, passwordConfirm, cpf);

                user.setName(name);
                user.setEmail(email);
                user.setPassword(password);
                user.setPasswordConfirm(passwordConfirm);
                user.setCpf(cpf);

                fileOutputStream.write("#\n".getBytes());
                fileOutputStream.write(user.getName().getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(user.getEmail().getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(user.getPassword().getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(user.getPasswordConfirm().getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(user.getCpf().getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.close();

                Toast.makeText(getApplicationContext(), "Usuário salvo com sucesso!", Toast.LENGTH_LONG).show();

                //APÓS SALVAR O ARQUIVO, EXIBIR UM ANÚNCIO DO TIPO INTERSTICIAL,
                //QUE DEVE SER IMPLEMENTADO ATRAVÉS DO ADMOB
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Dados não foram salvos. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        readFromFile();
    }

    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(Constants.USERS_FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                Log.d("Dados salvos:" ,ret);
            }

        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void clearFormSignUp(View view) {
        nameField.getText().clear();
        emailField.getText().clear();
        passwordField.getText().clear();
        passwordConfirmField.getText().clear();
        cpfField.getText().clear();
        isNameValid = false;
        isEmailValid = false;
        isPasswordValid = false;
        isPasswordConfirmedValid = false;
        isCpfValid = false;
    }
}