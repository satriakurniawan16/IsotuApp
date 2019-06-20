package com.example.user.isotuapp.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Event;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    ImageView takePhoto,picture;
    EditText titelField,scheduleField,timeStartField,timeEndField,locationField,descriptionField;
    Calendar myCalendar;
    Button btnPostEvent;

    String key;
    private ProgressDialog pDialog;

    private static int IMG_CAMERA = 2;
    private Uri filepath;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference dbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        /* takePhoto.setVisibility(View.GONE);*/
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dbs = FirebaseDatabase.getInstance().getReference("events");

        titelField = (EditText) findViewById(R.id.event_name);
        scheduleField = (EditText) findViewById(R.id.schedule);
        timeStartField = (EditText) findViewById(R.id.timestart);
        timeEndField = (EditText) findViewById(R.id.timeend);
        locationField = (EditText) findViewById(R.id.location);
        descriptionField = (EditText) findViewById(R.id.description );
        btnPostEvent = (Button) findViewById(R.id.btn_post_event);
        picture = (ImageView) findViewById(R.id.img_event);

        takePhoto = (ImageView) findViewById(R.id.img);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(EventActivity.this);
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(EventActivity.this);
            }
        });
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        scheduleField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeStartField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        /*timeStartField.setText(selectedHour + ":" + selectedMinute);*/
                        String am_pm;
                        if (selectedHour<12){
                            am_pm = "AM";
                            timeStartField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }else if (selectedHour==12){
                            am_pm = "PM";
                            timeStartField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }else {
                            am_pm = "PM";
                            timeStartField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        timeEndField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String am_pm;
                        if (selectedHour<12){
                            am_pm = "AM";
                            timeEndField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }else if (selectedHour==12){
                            am_pm = "PM";
                            timeEndField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }else {
                            am_pm = "PM";
                            timeEndField.setText(selectedHour + ":" + selectedMinute +" "+am_pm);
                        }
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(titelField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(scheduleField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Tanggal tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(timeStartField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Jam mulai tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(timeEndField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Jam berakhir tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(locationField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Lokasi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(descriptionField.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }if(filepath == null ){
                    Toast.makeText(getApplicationContext(), "gambar tidak boleh kosong !", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    sendPost();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    filepath = result.getUri();
                    takePhoto.setVisibility(View.GONE);
                    picture.setImageURI(filepath);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            else if (requestCode == IMG_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                takePhoto.setVisibility(View.GONE);
                picture.setImageBitmap(thumbnail);
            }
        }catch (Exception e){
            Log.d("ClaimProduct", "onActivityResult: "+e.toString());
            Toast.makeText(EventActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPost() {
        displayLoader();
        final StorageReference ref = storageReference.child("event/"+ UUID.randomUUID().toString());
        if(filepath != null) {
            UploadTask uploadTask = ref.putFile(filepath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlGambar = uri.toString();
                            key = dbs.push().getKey();
                            final String judulEvent = titelField.getText().toString();
                            final String tanggalEvent = scheduleField.getText().toString();
                            final String jamMulai = timeStartField.getText().toString();
                            final String jamAkhir = timeEndField.getText().toString();
                            final String lokasi   = locationField.getText().toString();
                            final String deskripsi = descriptionField.getText().toString();
                            final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User usr = dataSnapshot.getValue(User.class);
                                    submitPosting(new Event(key, urlGambar, judulEvent, tanggalEvent,jamMulai,jamAkhir,lokasi, deskripsi,usr.getFullname(),usr.getUid()));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pDialog.dismiss();
                    Toast.makeText(EventActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    pDialog.setMessage("Uploading "+(int)progress+"%");
                }
            });
        }else{

        }
    }


    private void submitPosting(Event event) {
        Log.d("lol", "submitBarang: disini");
        dbs.child(key).setValue(event).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("lo", "berhasil");
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        Intent intent = new Intent(EventActivity.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        scheduleField.setText(sdf.format(myCalendar.getTime()));
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending post...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        int d = R.drawable.custom_progress_dialog;
        Drawable drawable = getResources().getDrawable(d);
        pDialog.setIndeterminateDrawable(drawable);
        pDialog.show();
    }
    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    private void status(final String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

}
