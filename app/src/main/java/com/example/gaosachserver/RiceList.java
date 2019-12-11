package com.example.gaosachserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gaosachserver.Common.Common;
import com.example.gaosachserver.Interface.ItemClickListener;
import com.example.gaosachserver.Model.Category;
import com.example.gaosachserver.Model.Rice;
import com.example.gaosachserver.ViewHolder.RiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class RiceList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;
    FloatingActionButton fab;
    FirebaseDatabase db;
    DatabaseReference riceList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";
    FirebaseRecyclerAdapter<Rice, RiceViewHolder> adapter;

    //add new rice

    MaterialEditText edtName,edtDescreption, edtPrice,edtDiscount;
    Button btnSelect,btnUpload;
    Rice newRice;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_list);

        db= FirebaseDatabase.getInstance();
        riceList= db.getReference("Rices");
        storage= FirebaseStorage.getInstance();
        storageReference= storage.getReference();

        //init

        recyclerView= (RecyclerView) findViewById(R.id.recycle_rice);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout =(RelativeLayout)findViewById(R.id.rootLayout);

        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRiceDialog();
                
            }
        });
        if(getIntent()!= null)
            categoryId= getIntent().getStringExtra("CategoryId");

        if(!categoryId.isEmpty())
        {
            loadListRice(categoryId);
        }
    }

    private void showAddRiceDialog() {

        AlertDialog.Builder alerDialog = new AlertDialog.Builder(RiceList.this);
        alerDialog.setTitle("Thêm các loại gạo");
        alerDialog.setMessage("Vui lòng điền đầy đủ thông tin");

        LayoutInflater inflater= this.getLayoutInflater();
        View add_rice_layout= inflater.inflate(R.layout.add_new_rice,null);

        edtName= add_rice_layout.findViewById(R.id.edtName);
        edtDescreption= add_rice_layout.findViewById(R.id.edtDescription);
        edtPrice= add_rice_layout.findViewById(R.id.edtPrice);
        edtDiscount= add_rice_layout.findViewById(R.id.edtDiscount);

        btnSelect= add_rice_layout.findViewById(R.id.btnSelect);
        btnUpload= add_rice_layout.findViewById(R.id.btnUpload);

        //set event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();// set user chon image from thu vien for

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alerDialog.setView(add_rice_layout);
        alerDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //set Button

        alerDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //tao new category
                if(newRice!= null)
                {
                    riceList.push().setValue(newRice);
                    Snackbar.make(rootLayout,"Loại gạo mới"+newRice.getName()+ " Đã được thêm vào",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alerDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alerDialog.show();
    }
    private void chooseImage() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn ảnh"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang tải lên");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            String imagePrice= UUID.randomUUID().toString();
            final StorageReference imageFolder= storageReference.child("image/"+imageName);
            final StorageReference priceFolder= storageReference.child("image/"+imagePrice);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(RiceList.this,"Tải lên thành công", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set vaue cho category
                                    newRice= new Rice();
                                    newRice.setName(edtName.getText().toString());
                                    newRice.setDescription(edtDescreption.getText().toString());
                                    newRice.setPrice(edtPrice.getText().toString());
                                    newRice.setDiscount(edtDiscount.getText().toString());
                                    newRice.setMenuId(categoryId);
                                    newRice.setImage(uri.toString());


                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(RiceList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress= (500.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Tải lên"+ progress+"%");
                }
            });

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Common.PICK_IMAGE_REQUEST && resultCode== RESULT_OK
                && data != null && data.getData()!= null){
            saveUri= data.getData();
            btnSelect.setText("Chọn hình ảnh");
        }
    }

    private void loadListRice(final String categoryId) {

        adapter= new FirebaseRecyclerAdapter<Rice, RiceViewHolder>(
                Rice.class,
                R.layout.rice_item,
                RiceViewHolder.class,
                riceList.orderByChild("menuId").equalTo(categoryId)
        )
        {
            @Override
            protected void populateViewHolder(RiceViewHolder viewHolder, Rice model, int position) {

                viewHolder.rice_name.setText(model.getName());
                viewHolder.rice_price.setText(model.getPrice());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.rice_image);
                final Rice local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                        //code lsate
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateRiceDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        if(item.getTitle().equals(Common.DELETE))
        {
            deleteRice(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deleteRice(String key) {
        riceList.child(key).removeValue();
    }

    private void showUpdateRiceDialog(final String key, final Rice item) {

        AlertDialog.Builder alerDialog = new AlertDialog.Builder(RiceList.this);
        alerDialog.setTitle("Chỉnh sửa loại gạo");
        alerDialog.setMessage("Vui lòng điền đầy đủ thông tin");

        LayoutInflater inflater= this.getLayoutInflater();
        View add_rice_layout= inflater.inflate(R.layout.add_new_rice,null);

        edtName= add_rice_layout.findViewById(R.id.edtName);
        edtDescreption= add_rice_layout.findViewById(R.id.edtDescription);
        edtPrice= add_rice_layout.findViewById(R.id.edtPrice);
        edtDiscount= add_rice_layout.findViewById(R.id.edtDiscount);

        //set defaut value for view
        edtName.setText(item.getName());
        edtDiscount.setText(item.getDiscount());
        edtPrice.setText(item.getPrice());
        edtDescreption.setText(item.getDescription());


        btnSelect= add_rice_layout.findViewById(R.id.btnSelect);
        btnUpload= add_rice_layout.findViewById(R.id.btnUpload);

        //set event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();// set user chon image from thu vien for

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alerDialog.setView(add_rice_layout);
        alerDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //set Button

        alerDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //tao new category
//                if(newRice!= null)
//                {
                    //update information
                    item.setName(edtName.getText().toString());
                    item.setDiscount(edtDiscount.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDescription(edtDescreption.getText().toString());


                    riceList.child(key).setValue(item);
                    Snackbar.make(rootLayout,"Gạo "+item.getName()+ " Đã được chỉnh sửa",Snackbar.LENGTH_SHORT).show();
//                }
            }
        });
        alerDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alerDialog.show();
    }

    private void changeImage(final Rice item) {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang tải lên");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder= storageReference.child("image/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(RiceList.this,"Tải lên", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    item.setImage(uri.toString());


                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(RiceList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress= (100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Tải lên"+ progress+"%");
                }
            });

        }
    }
}
