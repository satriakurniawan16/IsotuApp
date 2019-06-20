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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    EditText namaProfileEditText, emailProfile, nimProfile,noHpProfile,AsalProfile;
    Spinner FakultasProfile,ProdiProfile,ProvinceProfile;
    int positionfakultas,positionjurusan,positionprovinsi;
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
        setContentView(R.layout.activity_edit_profil);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        namaProfileEditText  = (EditText) findViewById(R.id.editfullname);
        emailProfile = (EditText) findViewById(R.id.editemail);
        nimProfile= (EditText) findViewById(R.id.editnim);
        FakultasProfile = (Spinner) findViewById(R.id.editfaculty);
        ProdiProfile = (Spinner) findViewById(R.id.editmajor);
        ProvinceProfile = (Spinner) findViewById(R.id.editprovinsi);
        noHpProfile = (EditText) findViewById(R.id.editnumberPhone);
        AsalProfile = (EditText) findViewById(R.id.editasal);
        fotoProfile = (ImageView) findViewById(R.id.editfotoprofil);
        saveButton = (Button) findViewById(R.id.saveeditprofile);



        Intent intent = getIntent();
        String foto  = intent.getStringExtra("foto");
        String fullname  = intent.getStringExtra("nama");
        String email  = intent.getStringExtra("email");
        String nim  = intent.getStringExtra("nim");
        positionfakultas = intent.getIntExtra("posfakultas",0);
        positionjurusan = intent.getIntExtra("posjurusan",0);
        positionprovinsi = intent.getIntExtra("posprovinsi",0);
        String nohp  = intent.getStringExtra("nohp");
        String asal  = intent.getStringExtra("asal");

        initdropdown();

        Picasso.get().load(foto).fit()
                .into(fotoProfile);
        namaProfileEditText.setText(fullname);
        emailProfile.setText(email);
        nimProfile.setText(nim);
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
                    user.put("fakultas",FakultasProfile.getSelectedItem().toString());
                    user.put("jurusan",ProdiProfile.getSelectedItem().toString());
                    user.put("nohp",noHpProfile.getText().toString());
                    user.put("asal",ProvinceProfile.getSelectedItem().toString()+", "+AsalProfile.getText().toString());
                    user.put("completeProfile",usr.getCompleteProfile());
                    user.put("uid",currentUser.getUid());
                    user.put("search",namaProfileEditText.getText().toString().toLowerCase());
                    user.put("searchbyprovince",ProvinceProfile.getSelectedItem().toString().toLowerCase());
                    user.put("positionfakultas",positionfakultas);
                    user.put("positionjurusan",positionjurusan);
                    user.put("positionprovinsi",positionprovinsi);
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
                                    user.put("fakultas",FakultasProfile.getSelectedItem().toString());
                                    user.put("jurusan",ProdiProfile.getSelectedItem().toString());
                                    user.put("nohp",noHpProfile.getText().toString());
                                    user.put("asal",ProvinceProfile.getSelectedItem().toString()+", "+AsalProfile.getText().toString());
                                    user.put("completeProfile",usr.getCompleteProfile());
                                    user.put("search",namaProfileEditText.toString());
                                    user.put("searchbyprovince",ProvinceProfile.getSelectedItem().toString().toLowerCase());
                                    user.put("positionfakultas",positionfakultas);
                                    user.put("positionjurusan",positionjurusan);
                                    user.put("positionprovinsi",positionprovinsi);
                                    user.put("uid",currentUser.getUid());
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

        FakultasProfile.setAdapter(adapter);
        FakultasProfile.setSelection(positionfakultas);
        FakultasProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
//                Toast.makeText(EditProfil.this, "Selected "+ adapter.getItem(i), Toast.LENGTH_SHORT).show();
                positionfakultas = i ;
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

        ProvinceProfile.setAdapter(adapterprovinsi);
        ProvinceProfile.setSelection(positionprovinsi);
        ProvinceProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                positionprovinsi = i;
//                Toast.makeText(EditProfil.this, "Selected "+ adapterprovinsi.getItem(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
}
    public void jurusan (final ArrayAdapter<String> adapterjurusan ){
        ProdiProfile.setAdapter(adapterjurusan);
        ProdiProfile.setSelection(positionjurusan);
        ProdiProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
//                Toast.makeText(EditProfil.this, "Selected "+ adapterjurusan.getItem(i), Toast.LENGTH_SHORT).show();
                positionjurusan = i ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
