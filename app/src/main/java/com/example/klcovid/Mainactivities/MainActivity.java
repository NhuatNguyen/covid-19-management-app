package com.example.klcovid.Mainactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.klcovid.Service.SharedPreferenceClass;
import com.example.klcovid.databinding.ActivityMainBinding;
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

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String mPhoneNumber, mVerification;
    FirebaseAuth auth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken3;
    SharedPreferenceClass sharedPreferenceClass;
    static final  String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        mPhoneNumber = getIntent().getStringExtra("phone_number");
        mVerification = getIntent().getStringExtra("verification");
        getDataIntent();
        sharedPreferenceClass = new SharedPreferenceClass(MainActivity.this);

        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.edtOtp.getText().toString().trim();
                if(otp.isEmpty() || otp.length() < 5 || otp.length() > 6){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ OTP", Toast.LENGTH_SHORT).show();
                }else{
                    ocClickOtpcode(otp);
                }
            }
        });
//        binding.imgbackinfor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        binding.txtrequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOtpAgain();
            }
        });
    }

    private void getDataIntent() {
        binding.txtinfoverifynumber.setText("Nhấn để gửi mã OTP vào số " + mPhoneNumber.replace("+84","0"));
    }

    private void onClickOtpAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(mPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(forceResendingToken3)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(MainActivity.this, "Xác nhận lỗi" + e.toString(), Toast.LENGTH_SHORT).show();
                            }

                            //onCodeSent gui ma code
                            @Override
                            public void onCodeSent(@NonNull String verification, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verification, forceResendingToken);
                                mVerification = verification;
                                forceResendingToken3 = forceResendingToken;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void ocClickOtpcode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToMainActivity(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(MainActivity.this, "Mã xác nhập không hợp lệ" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goToMainActivity(String phoneNumber) {
        sharedPreferenceClass.setValue_String("phone_number", phoneNumber.replace("+84", "0"));
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
