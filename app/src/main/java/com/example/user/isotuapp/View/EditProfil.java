package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

public class EditProfil extends AppCompatActivity {

    ImageView fotoProfile;
    EditText namaProfileEditText, emailProfile, nimProfile,FakultasProfile,ProdiProfile,noHpProfile,AsalProfile;
    View mRootView;
    LinearLayout edit;
    FirebaseUser currentUser;
    String Image;
    Button saveButton;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filepath;
    private static int IMG_CAMERA = 2;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        namaProfileEditText  = (EditText) findViewById(R.id.editfullname);
        emailProfile = (EditText) findViewById(R.id.editemail);
        nimProfile= (EditText) findViewById(R.id.editnim);
        FakultasProfile = (EditText) findViewById(R.id.editfaculty);
        ProdiProfile = (EditText) findViewById(R.id.editmajor);
        noHpProfile = (EditText) findViewById(R.id.editnumberPhone);
        AsalProfile = (EditText) findViewById(R.id.editasal);
        fotoProfile = (ImageView) findViewById(R.id.editfotoprofil);
        saveButton = (Button) findViewById(R.id.saveeditprofile);

        Intent intent = getIntent();
        String foto  = intent.getStringExtra("foto");
        String fullname  = intent.getStringExtra("nama");
        String email  = intent.getStringExtra("email");
        String nim  = intent.getStringExtra("nim");
        String fakultas  = intent.getStringExtra("fakultas");
        String jurusan  = intent.getStringExtra("jurusan");
        String nohp  = intent.getStringExtra("nohp");
        String asal  = intent.getStringExtra("asal");

        Picasso.get().load(foto).fit()
                .into(fotoProfile);
        namaProfileEditText.setText(fullname);
        emailProfile.setText(email);
        nimProfile.setText(nim);
        FakultasProfile.setText(fakultas);
        ProdiProfile.setText(jurusan);
        noHpProfile.setText(nohp);
        AsalProfile.setText(asal);

        fotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(EditProfil.this);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

    }

    private void saveSettings() {
        final StorageReference ref = storageReference.child("users/"+ UUID.randomUUID().toString());
        if (filepath == null) {
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    final HashMap<String, Object> user= new HashMap<>();
                    user.put("email",emailProfile.getText().toString());
                    user.put("image", usr.getImage());
                    user.put("fullname", namaProfileEditText.getText().toString());
                    user.put("nim",nimProfile.getText().toString());
                    user.put("fakultas",FakultasProfile.getText().toString());
                    user.put("jurusan",ProdiProfile.getText().toString());
                    user.put("nohp",noHpProfile.getText().toString());
                    user.put("asal",AsalProfile.getText().toString());
                    user.put("completeProfile",usr.getCompleteProfile());
                    user.put("Uid",currentUser.getUid());
                    dbf.setValue(user);
                    Toast.makeText(EditProfil.this, "Profile tersimpan ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfil.this, Dashboard.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            UploadTask uploadTask = ref.putFile(filepath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlGambar = uri.toString();
                            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());

                            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User usr = dataSnapshot.getValue(User.class);
                                    final HashMap<String, Object> user= new HashMap<>();
                                    user.put("email",emailProfile.getText().toString());
                                    user.put("image", urlGambar);
                                    user.put("fullname", namaProfileEditText.getText().toString());
                                    user.put("nim",nimProfile.getText().toString());
                                    user.put("fakultas",FakultasProfile.getText().toString());
                                    user.put("jurusan",ProdiProfile.getText().toString());
                                    user.put("nohp",noHpProfile.getText().toString());
                                    user.put("asal",AsalProfile.getText().toString());
                                    user.put("completeProfile",usr.getCompleteProfile());
                                    user.put("Uid",currentUser.getUid());
                                    dbf.setValue(user);
                                    Toast.makeText(EditProfil.this, "Profile Tersimpan", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditProfil.this, Dashboard .class);
                                    startActivity(intent);

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
                    progressDialog.dismiss();
                    Toast.makeText(EditProfil.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    filepath = result.getUri();
                    fotoProfile.setImageURI(filepath);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }
            else if (requestCode == IMG_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                fotoProfile.setImageBitmap(thumbnail);
            }
        }catch (Exception e){
            Log.d("ClaimProduct", "onActivityResult: "+e.toString());
            Toast.makeText(EditProfil.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
        }
    }
}
