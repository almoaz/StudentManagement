package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import Model.RecordData;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    EditText password, name, email, phone, roll, address, date_of_birth, searchBox;
    Button passwordSubmitBtn, recordAddBtn, recordUpdateBtn, recordDeleteBtn, searchBtn, clearBtn ;
    ConstraintLayout securityConstraintLayout, resultConstraintLayout;
    NestedScrollView recordConstraintLayout;
    CircleImageView imageView;
    CircleImageView addImageView;
    private DatabaseReference userReference;
    Bitmap thumb_bitmaps = null;
    public static final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    private StorageReference ImageReff;
    String ImageUrl;
    RecyclerView recordData;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userReference = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(MainActivity.this);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        password = findViewById(R.id.security_password_text);
        passwordSubmitBtn = findViewById(R.id.security_password_submit_btn);
        securityConstraintLayout = findViewById(R.id.security_constraint_layout);
        recordConstraintLayout = findViewById(R.id.record_constraint_layout);
        resultConstraintLayout = findViewById(R.id.result_constraint_layout);

        imageView = findViewById(R.id.record_image);
        addImageView = findViewById(R.id.add_image_icon);
        name = findViewById(R.id.record_box_name);
        email = findViewById(R.id.record_box_email);
        roll = findViewById(R.id.record_box_roll_no);
        phone = findViewById(R.id.record_box_phone_no);
        address = findViewById(R.id.record_box_address);
        date_of_birth = findViewById(R.id.record_box_date_of_birth);
        recordAddBtn = findViewById(R.id.record_box_add_btn);
        recordUpdateBtn = findViewById(R.id.record_box_update_btn);
        recordDeleteBtn = findViewById(R.id.record_box_delete_btn);

        recordData = findViewById(R.id.record_recyclearView);
        recordData.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recordData.setLayoutManager(linearLayoutManager);

        DisplayAllRecord("");

        searchBox = findViewById(R.id.searchBox);
        searchBtn = findViewById(R.id.searchBtn);
        clearBtn = findViewById(R.id.clearBtn);

        searchBtn.setEnabled(false);
        searchBtn.setTextColor(Color.argb(50, 255, 255, 255));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(searchBox.getText().toString()))
                {
                    searchBtn.setEnabled(false);
                    searchBtn.setTextColor(Color.argb(50, 255, 255, 255));

                }
                else {
                    searchBtn.setEnabled(true);
                    searchBtn.setTextColor(Color.rgb( 255, 255, 255));
                    searchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {

                            String searchBoxInputs = searchBox.getText().toString();
                            DisplayAllRecord(searchBoxInputs);


                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayAllRecord("");
                searchBox.setText("");
                searchBtn.setEnabled(false);
                searchBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        });


        passwordSubmitBtn.setEnabled(false);
        passwordSubmitBtn.setTextColor(Color.argb(50, 255, 255, 255));
        recordAddBtn.setEnabled(false);
        recordAddBtn.setTextColor(Color.argb(50, 255, 255, 255));


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        roll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        date_of_birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        recordUpdateBtn.setEnabled(false);
        recordUpdateBtn.setTextColor(Color.argb(50, 255, 255, 255));
        recordDeleteBtn.setEnabled(false);
        recordDeleteBtn.setTextColor(Color.argb(50, 255, 255, 255));

        passwordSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("1234567890")) {
                    securityConstraintLayout.setVisibility(View.GONE);
                    recordConstraintLayout.setVisibility(View.VISIBLE);
                    resultConstraintLayout.setVisibility(View.VISIBLE);
                } else {
                    securityConstraintLayout.setVisibility(View.VISIBLE);
                    recordConstraintLayout.setVisibility(View.INVISIBLE);
                    resultConstraintLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recordAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitDataToServer();

            }
        });

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void DisplayAllRecord(String searchBoxInputs) {
        Query query = userReference.child("All Student").orderByChild("roll")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<RecordData> options = new FirebaseRecyclerOptions.Builder<RecordData>().setQuery(query, RecordData.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<RecordData, MainActivity.RecordViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MainActivity.RecordViewHolder recordViewHolder, final int position, @NonNull final RecordData recordData) {

                final String usersIDs = getRef(position).getKey();

                recordViewHolder.name.setText(recordData.getName());
                recordViewHolder.roll.setText(recordData.getRoll());
                recordViewHolder.address.setText(recordData.getAddress());
                recordViewHolder.dateOfBirth.setText(recordData.getDateOfBirth());
                recordViewHolder.email.setText(recordData.getEmail());
                recordViewHolder.number.setText(recordData.getNumber());

                recordViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recordUpdateBtn.setEnabled(true);
                        recordUpdateBtn.setTextColor(Color.rgb( 255, 255, 255));

                        recordDeleteBtn.setEnabled(true);
                        recordDeleteBtn.setTextColor(Color.rgb( 255, 255, 255));

                        addImageView.setVisibility(View.INVISIBLE);


                        name.setText(recordViewHolder.name.getText().toString());
                        email.setText(recordViewHolder.email.getText().toString());
                        date_of_birth.setText(recordViewHolder.dateOfBirth.getText().toString());
                        address.setText(recordViewHolder.address.getText().toString());
                        phone.setText(recordViewHolder.number.getText().toString());
                        roll.setText(recordViewHolder.roll.getText().toString());

                        userReference.child("All Student").child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if (snapshot.exists())
                                {
                                    String image = Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString();
                                    Picasso.get().load(image).into(imageView);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        recordUpdateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HashMap<String, Object> userMapMap = new HashMap<>();

                                userMapMap.put("name",name.getText().toString());
                                userMapMap.put("email",email.getText().toString());
                                userMapMap.put("roll",roll.getText().toString());
                                userMapMap.put("number",phone.getText().toString());
                                userMapMap.put("dateOfBirth",date_of_birth.getText().toString());
                                userMapMap.put("address",address.getText().toString());

                                userReference.child("All Student").child(recordViewHolder.roll.getText().toString()).updateChildren(userMapMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            name.setText("");
                                            email.setText("");
                                            phone.setText("");
                                            date_of_birth.setText("");
                                            roll.setText("");
                                            address.setText("");
                                            recordAddBtn.setEnabled(false);
                                            recordAddBtn.setTextColor(Color.argb(50,255,255,255));
                                            imageView.setImageDrawable(getDrawable(R.drawable.profile_icon));

                                            Toast.makeText(MainActivity.this,"User data updated",Toast.LENGTH_SHORT).show();

                                            recordUpdateBtn.setEnabled(false);
                                            recordUpdateBtn.setTextColor(Color.argb(50, 255, 255, 255));
                                            recordDeleteBtn.setEnabled(false);
                                            recordDeleteBtn.setTextColor(Color.argb(50, 255, 255, 255));

                                            addImageView.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {

                                        }
                                    }
                                });
                            }
                        });

                        recordDeleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                userReference.child("All Student").child(recordViewHolder.roll.getText().toString()).removeValue();
                                name.setText("");
                                email.setText("");
                                phone.setText("");
                                date_of_birth.setText("");
                                roll.setText("");
                                address.setText("");
                                recordAddBtn.setEnabled(false);
                                recordAddBtn.setTextColor(Color.argb(50,255,255,255));
                                imageView.setImageDrawable(getDrawable(R.drawable.profile_icon));
                                Toast.makeText(MainActivity.this,"User data deleted",Toast.LENGTH_SHORT).show();
                                recordUpdateBtn.setEnabled(false);
                                recordUpdateBtn.setTextColor(Color.argb(50, 255, 255, 255));
                                recordDeleteBtn.setEnabled(false);
                                recordDeleteBtn.setTextColor(Color.argb(50, 255, 255, 255));
                                addImageView.setVisibility(View.VISIBLE);
                            }
                        });




                        recordAddBtn.setEnabled(false);
                        recordAddBtn.setTextColor(Color.argb(50, 255, 255, 255));


                    }
                });






            }

            public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent ,false);
                return new RecordViewHolder(view);
            }
        };
        adapter.startListening();
        recordData.setAdapter(adapter);
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        View mView;
        View recordView;
        TextView name , email, number, address, dateOfBirth, roll;


        RecordViewHolder(View itemView) {
            super(itemView);

            recordView = itemView;

            name = itemView.findViewById(R.id.item_name);
            email = itemView.findViewById(R.id.item_email);
            number = itemView.findViewById(R.id.item_phone_no);
            address = itemView.findViewById(R.id.item_address);
            dateOfBirth = itemView.findViewById(R.id.item_date_of_birth);
            roll = itemView.findViewById(R.id.item_roll);




            mView = itemView;

        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data!=null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(MainActivity.this);


        }

        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final  String postRandomName = saveCurrentDate + saveCurrentTime;


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Student Image");
                loadingBar.setMessage("Your Student image uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());

                try {
                    thumb_bitmaps = new Compressor(MainActivity.this)
                            .setMaxWidth(400)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePathUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmaps.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                StorageReference filePath = ImageReff.child(resultUri.getLastPathSegment() + postRandomName + ".jpg");                     //loadingBar.setTitle("Profile Image");

                filePath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            //Toast.makeText(AddPostActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getMetadata()).getReference()).getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    ImageUrl = downloadUrl;
                                    Picasso.get().load(ImageUrl).into(imageView);
                                    loadingBar.dismiss();

                                }
                            });
                        }
                    }
                });
            } else {
                loadingBar.dismiss();
            }
        } super.onActivityResult(requestCode, resultCode, data);

    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_pick);
    }

    private void submitDataToServer() {

        HashMap<String, Object> userMapMap = new HashMap<>();

        userMapMap.put("name",name.getText().toString());
        userMapMap.put("email",email.getText().toString());
        userMapMap.put("roll",roll.getText().toString());
        userMapMap.put("number",phone.getText().toString());
        userMapMap.put("dateOfBirth",date_of_birth.getText().toString());
        userMapMap.put("address",address.getText().toString());
        userMapMap.put("profileImage",ImageUrl);

        userReference.child("All Student").child(roll.getText().toString()).updateChildren(userMapMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    name.setText("");
                    email.setText("");
                    phone.setText("");
                    date_of_birth.setText("");
                    roll.setText("");
                    address.setText("");
                    recordAddBtn.setEnabled(false);
                    recordAddBtn.setTextColor(Color.argb(50,255,255,255));
                    imageView.setImageDrawable(getDrawable(R.drawable.profile_icon));

                    Toast.makeText(MainActivity.this,"User data recorded",Toast.LENGTH_SHORT).show();
                }
                else
                {

                }
            }
        });
    }

    private void CheckInputs()
    {
        if (!TextUtils.isEmpty(name.getText().toString()))
        {
            if (!TextUtils.isEmpty(email.getText().toString()))
            {
                if (!TextUtils.isEmpty(address.getText().toString()))
                {
                    if (!TextUtils.isEmpty(date_of_birth.getText().toString()))
                    {
                        if (!TextUtils.isEmpty(phone.getText().toString()))
                        {
                            if (!TextUtils.isEmpty(roll.getText().toString()))
                            {
                                recordAddBtn.setEnabled(true);
                                recordAddBtn.setTextColor(Color.rgb(255,255,255));
                            }
                            else
                            {
                                recordAddBtn.setEnabled(false);
                                recordAddBtn.setTextColor(Color.argb(50,255,255,255));

                            }

                        }
                        else
                        {
                            recordAddBtn.setEnabled(false);
                            recordAddBtn.setTextColor(Color.argb(50,255,255,255));

                        }

                    }
                    else
                    {
                        recordAddBtn.setEnabled(false);
                        recordAddBtn.setTextColor(Color.argb(50,255,255,255));

                    }

                }
                else
                {
                    recordAddBtn.setEnabled(false);
                    recordAddBtn.setTextColor(Color.argb(50,255,255,255));

                }

            }
            else
            {
                recordAddBtn.setEnabled(false);
                recordAddBtn.setTextColor(Color.argb(50,255,255,255));

            }

        }
        else
        {
            recordAddBtn.setEnabled(false);
            recordAddBtn.setTextColor(Color.argb(50,255,255,255));

        }
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(password.getText().toString()))
        {
            passwordSubmitBtn.setEnabled(true);
            passwordSubmitBtn.setTextColor(Color.rgb(255,255,255));
        }
        else
        {
            passwordSubmitBtn.setEnabled(false);
            passwordSubmitBtn.setTextColor(Color.argb(50,255,255,255));

        }
    }
}