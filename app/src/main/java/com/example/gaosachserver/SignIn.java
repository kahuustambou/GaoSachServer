package com.example.gaosachserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaosachserver.Common.Common;
import com.example.gaosachserver.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;

    Button btnSignIn;

    //declare an instance of firebase

   FirebaseDatabase db;
    ProgressDialog mDialog;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);

      //init firebase
        db = FirebaseDatabase.getInstance();
        users= db.getReference("User");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
//                String Email= edtEmail.getText().toString().trim();
//                String Password=edtPassword.getText().toString().trim();
//                if (Email.isEmpty()) {
//                    edtEmail.setError("(*) Vui lòng nhập email");
//                    edtEmail.requestFocus();
//                    return;
//                }
//                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
//                    edtEmail.setError("Địa chỉ email không hợp lệ");
//                    edtEmail.requestFocus();
//                    return;
//                }
//
//                if (Password.isEmpty()) {
//                    edtPassword.setError("Vui lòng nhập mật khẩu");
//                    edtPassword.requestFocus();
//                    return;
//                }
//
//                if (Password.length() < 6) {
//                    edtPassword.setError("Độ dài bắt buộc 6 kí tự");
//                    edtPassword.requestFocus();
//                    return;
//                }
//                else{
//                    signInUser(Email,Password);
//                }
            }
        });


//        //init prodialog
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Đăng Nhập ...");


    }
    private void signInUser(final String phone, final String password) {
        final ProgressDialog mDialog= new ProgressDialog(SignIn.this);
        mDialog.setMessage("Vui lòng đợi...");
        mDialog.show();

        final String localPhone= phone;
        final String localPassword= password;


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(localPhone).exists())
                {
                    mDialog.dismiss();
                    User user= dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff())) //if isStaff==true
                    {

                        if(user.getPassword().equals(localPassword))
                        {

                            Intent signInIntent= new Intent(SignIn.this,Home.class);
                            Common.currentUser= user;
                            startActivity(signInIntent);
                            finish();

                        }
                        else
                            Toast.makeText(SignIn.this,"Sai mật khẩu",Toast.LENGTH_SHORT).show();

                    }
                    else
                        Toast.makeText(SignIn.this,"Vui lòng đăng nhập bằng tài khoản Manager",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this,"Người dùng không tồn tại ",Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //show pd
//        mDialog.show();

//               users.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(localPhone).exists()) {
//                    mDialog.dismiss();
//                    User user = dataSnapshot.child(localPhone).getValue(User.class);
//                    user.setEmail(localPhone);
//                    if (Boolean.parseBoolean(user.getIsStaff())) {
//                        if (user.getEmail().equals(localPhone)) {
//                            Intent login= new Intent(SignIn.this,Home.class);
//                            Common.currentUser= user;
//                            startActivity(login);
//                            finish();
//
//
//                        } else
//                            Toast.makeText(SignIn.this, "Sai địa chỉ email", Toast.LENGTH_SHORT).show();
//                    } else
//                        Toast.makeText(SignIn.this, "Vui lòng đăng nhập bằng tài khoản người quản lý", Toast.LENGTH_SHORT).show();
//
//                }
//                else {
//                    mDialog.dismiss();
//                    Toast.makeText(SignIn.this,"Người dùng không tồn tại",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//
//              mAuth.signInWithEmailAndPassword(Email, Password)
//                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            User user= new User(
//                                    Email,
//                                    Password
//
//                            );
//                            if (!Boolean.parseBoolean(user.getIsStaff())){
//                                startActivity(new Intent(SignIn.this, Home.class));
//                                finish();
//
//                            }
//                            else
//
//                                Toast.makeText(SignIn.this, "Vui lòng đăng nhập tài khoản nhân viên", Toast.LENGTH_SHORT).show();
//
//
////                                mDialog.dismiss();
//
////                                startActivity(new Intent(SignIn.this, Home.class));
////                                finish();
//
//
//
//                        } else {
//                            mDialog.dismiss();
//                            Toast.makeText(SignIn.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                mDialog.dismiss();
//                //error get and show error meesage
//                Toast.makeText(SignIn.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        //show pd
//        mDialog.show();

//        mAuth.signInWithEmailAndPassword(Email, Password)
//                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            User user= new User(
//                                    Email,
//                                    Password
//                            );
//
//                            mDialog.dismiss();
//                            Toast.makeText(SignIn.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignIn.this,Home.class));
//                            finish();
//                        } else {
//                            mDialog.dismiss();
//                            Toast.makeText(SignIn.this, "Đăng  nhập thất bại", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                mDialog.dismiss();
//                //error get and show error meesage
//                Toast.makeText(SignIn.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }
}
