package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

public class CompleteProfile extends AppCompatActivity {

    private static int IMG_CAMERA = 2;
    EditText fullnameField,nimField,numberField,asalField;
    ImageView profilePict;
    Button saveProfile;
    private Uri filepath;
    FirebaseUser currentUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference dbs;
    Spinner spinnerTextFakultas,spinnerTextJurusan,spinnerTextProvinsi;
    int positionFakultas,positionJurusan,positonProvinsi;

    private String[] fakultas = {
            "Fakultas Teknik Elektro",
            "Fakultas Informatika",
            "Fakultas Rekayasa Industri",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Industri Kreatif",
            "Fakultas Ilmu Terapan",
            "Fakultas Komunikasi dan Bisnis"
    };

    private String[] jurusanfif = {
            "S1 Teknik Informatika",
            "S1 Teknik Informatika (International)",
            "S2 Teknik Informatika",
            "S1 Teknologi Informasi"
    };

    private String[] jurusanfte = {
            "S1 Teknik Telekomunikasi",
            "S1 Teknik Fisika",
            "S2 Teknik Telekomunikasi (International)",
            "S1 Sistem Komputer",
            "S1 Teknik Elektro",
            "S1 Teknik Elektro (International)",
            "S2 Teknik Telekomunikasi"
    };

    private String[] jurusanfeb = {
            "S1 International ICT Business",
            "S1 Akuntansi",
            "S1 MBTI",
            "S1 Manajemen"
    };

    private String[] jurusanfri = {
            "S1 Teknik Industri",
            "S1 Sistem Informasi",
            "S1 Teknik Industri (International)",
            "S2 Teknik Industri)"
    };

    private String[] jurusanfit = {
            "D3 Rekayasa Perangkat Lunak Aplikasi",
            "D3 Sistem Informasi",
            "D3 Sistem Informasi Akuntansi",
            "D3 Teknologi Telekomunikasi",
            "D3 Manajemen Pemasaran",
            "D3 Perhotelan",
            "D3 Teknik Komputer",
            "D4 Sistem Multimedia"
    };

    private String[] jurusanfik = {
            "S1 Desain Komunikasi Visual",
            "S1 Kriya dan Tekstil",
            "S1 Desain Komunikasi Visual (International)",
            "S1 Desain Interior",
            "S1 Creative Arts",

    };

    private String[] jurusanfkb = {
            "S1 Administrasi Bisnis",
            "S1 Ilmu Komunikasi",
            "S1 Administrasi Bisnis (International)",
            "S1 Digital Public Relations",
            "S1 Ilmu Komunikasi (International)",

    };


    private String[] Provinsi = {
            "Aceh",
            "Bali",
            "Banten",
            "Bengkulu",
            "Gorontalo",
            "Jakarta",
            "Jawa Tengah",
            "Jawa Timur",
            "Kalimantan Barat",
            "Kalimantan Timur",
            "Kepulauan Riau",
            "Lampung",
            "Maluku",
            "Sulawesi Tengah",
            "Sulawesi Tenggara",
            "Sulawesi Utara",
            "Sumatera Barat",
            "Sumatera Selatan",
            "Riau",
            "Yogyakarta"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mFirebaseAuth=FirebaseAuth.getInstance();

        fullnameField = (EditText) findViewById(R.id.fullname);
        nimField = (EditText) findViewById(R.id.nim);
        spinnerTextFakultas =(Spinner) findViewById(R.id.faculty);
        spinnerTextJurusan = (Spinner) findViewById(R.id.major);
        spinnerTextProvinsi = (Spinner) findViewById(R.id.provinsi);

        initdropdown();






        saveProfile = (Button) findViewById(R.id.save);
        numberField = (EditText)findViewById(R.id.numberPhone);
        asalField = (EditText) findViewById(R.id.asal);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
         dbs = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            saveSetting();
            }
        });

        profilePict = (ImageView) findViewById(R.id.pict);
        profilePict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(CompleteProfile.this);
            }
        });
    }

    private void initdropdown() {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, fakultas);

        final ArrayAdapter<String> adapterfit = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfit);

        final ArrayAdapter<String> adapterfik = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfik);

        final ArrayAdapter<String> adapterfkb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfkb);

        final ArrayAdapter<String> adapterfeb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfeb);

        final ArrayAdapter<String> adapterfri = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfri);

        final ArrayAdapter<String> adapterfte = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfte);

        final ArrayAdapter<String> adapterfif = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfif);

        final ArrayAdapter<String> adapterprovinsi = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Provinsi);

        spinnerTextFakultas.setAdapter(adapter);
        spinnerTextFakultas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                Toast.makeText(CompleteProfile.this, "Selected "+ adapter.getItem(i), Toast.LENGTH_SHORT).show();
                positionFakultas = i ;
                switch (i){
                    case 0 :
                        jurusan(adapterfte);
                        break;
                    case 1 :
                        jurusan(adapterfif);
                        break;
                    case 2 :
                        jurusan(adapterfri);
                        break;
                    case 3 :
                        jurusan(adapterfeb);
                        break;
                    case 4 :
                        jurusan(adapterfik);
                        break;
                    case 5 :
                        jurusan(adapterfit);
                        break;
                    case 6 :
                        jurusan(adapterfkb);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerTextProvinsi.setAdapter(adapterprovinsi);
        spinnerTextProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                positonProvinsi = i;
                Toast.makeText(CompleteProfile.this, "Selected "+ adapterprovinsi.getItem(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                    profilePict.setImageURI(filepath);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }
            else if (requestCode == IMG_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                profilePict.setImageBitmap(thumbnail);
            }
        }catch (Exception e){
            Log.d("ClaimProduct", "onActivityResult: "+e.toString());
            Toast.makeText(CompleteProfile.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
        }
    }

    private void saveSetting(){

        final String fullnameString = fullnameField.getText().toString().trim();
        final String nimString = nimField.getText().toString().trim();
        final String facultyString = spinnerTextFakultas.getSelectedItem().toString().trim();
        final String majorString = spinnerTextJurusan.getSelectedItem().toString().trim();
        final String numberString = numberField.getText().toString().trim();
        final String asalString= asalField.getText().toString().trim();
        final String provinsiString= spinnerTextProvinsi.getSelectedItem().toString().trim();
        dbs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (TextUtils.isEmpty(fullnameString)){
                    Toast.makeText(getApplicationContext(), "Enter fullname!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(nimString)){
                    Toast.makeText(getApplicationContext(), "Enter nim!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(facultyString)){
                    Toast.makeText(getApplicationContext(), "Enter faculty!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(majorString)){
                    Toast.makeText(getApplicationContext(), "Enter major!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(numberString)){
                    Toast.makeText(getApplicationContext(), "Enter Number Phone!", Toast.LENGTH_SHORT).show();
                    return;
                }if(filepath == null){
                    Toast.makeText(CompleteProfile.this, "Take Your Profile!", Toast.LENGTH_SHORT).show();
                }
                else{
                    final StorageReference ref = storageReference.child("users/"+ UUID.randomUUID().toString());
                    final ProgressDialog progressDialog = new ProgressDialog(CompleteProfile.this);
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
                                    final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                    final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                                    dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User usr = dataSnapshot.getValue(User.class);
                                            final HashMap<String, Object> user= new HashMap<>();
                                            user.put("email",usr.getEmail());
                                            user.put("image", urlGambar);
                                            user.put("fullname", fullnameString);
                                            user.put("nim",nimString);
                                            user.put("fakultas",facultyString);
                                            user.put("jurusan",majorString);
                                            user.put("nohp",numberString);
                                            user.put("asal",provinsiString+", "+asalString);
                                            user.put("completeProfile",1);
                                            user.put("uid",currentUser.getUid());
                                            user.put("status","offline");
                                            user.put("search",fullnameString.toLowerCase());
                                            user.put("searchbyprovince",provinsiString.toLowerCase());
                                            user.put("positionfakultas",positionFakultas);
                                            user.put("positionjurusan",positionJurusan);
                                            user.put("positionprovinsi",positonProvinsi);
                                            dbs.setValue(user);
                                            Toast.makeText(CompleteProfile.this, "Profile tersimpan", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CompleteProfile.this, Dashboard.class);
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
                            Toast.makeText(CompleteProfile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void jurusan (final ArrayAdapter<String> adapterjurusan ){
        spinnerTextJurusan.setAdapter(adapterjurusan);
        spinnerTextJurusan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                Toast.makeText(CompleteProfile.this, "Selected "+ adapterjurusan.getItem(i), Toast.LENGTH_SHORT).show();
                positionJurusan = i ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
