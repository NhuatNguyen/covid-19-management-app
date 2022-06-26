package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.klcovid.Service.SharedPreferenceClass;
import com.example.klcovid.databinding.ActivityVerifyNumberPhoneBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyNumberPhone extends AppCompatActivity {
    ActivityVerifyNumberPhoneBinding binding;
    FirebaseAuth auth;
    String strPhoneNumber, StrPhoneNumberFormat;
    static final String TAG = VerifyNumberPhone.class.getName();
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyNumberPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferenceClass = new SharedPreferenceClass(VerifyNumberPhone.this);
        auth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                strPhoneNumber = binding.edtInputNumber.getText().toString().replaceFirst("0", "").trim();
                if (strPhoneNumber.isEmpty() || strPhoneNumber.length() < 9 || strPhoneNumber.length() > 13) {
                    Toast.makeText(VerifyNumberPhone.this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
                } else {
                    StrPhoneNumberFormat = "+84" + strPhoneNumber;
                    onClickVerifyPhonenumber(StrPhoneNumberFormat);
                }
            }
        });

    }

    private void onClickVerifyPhonenumber(String strNumberphone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(strNumberphone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifyNumberPhone.this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyNumberPhone.this, "Xác nhận lỗi" + e.toString(), Toast.LENGTH_LONG).show();
                                Log.d("AAA", e.toString());
                            }

                            //onCodeSent gui ma code
                            @Override
                            public void onCodeSent(@NonNull String verification, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verification, forceResendingToken);
                                goToEnterOTP(strNumberphone, verification);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyNumberPhone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToMainActivity(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyNumberPhone.this, "Mã xác minh đã nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goToMainActivity(String phoneNumber) {
        SharedPreferences admin_Nhuat = getSharedPreferences("datalogin", MODE_PRIVATE);
        if (admin_Nhuat.contains("phone_number")) {
            startActivity(new Intent(VerifyNumberPhone.this, Check_NumberPhone.class));
            finish();
        }
    }

    private void goToEnterOTP(String strPhoneNumber, String verification) {
        Intent intent = new Intent(VerifyNumberPhone.this, MainActivity.class);
        intent.putExtra("phone_number", strPhoneNumber);
        intent.putExtra("verification", verification);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences admin_Nhuat = getSharedPreferences("datalogin", MODE_PRIVATE);
        if (admin_Nhuat.contains("phone_number")) {
            startActivity(new Intent(VerifyNumberPhone.this, Check_NumberPhone.class));
            finish();
        }
    }
}