package com.example.jimenez.appmunitacna;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimenez.appmunitacna.Clases.Reporte;
import com.example.jimenez.appmunitacna.objects.FirebaseReferences;
import com.example.jimenez.appmunitacna.objects.Global;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class MandarReporteActivity extends AppCompatActivity {

    private static final int RC_GALLERY = 21;
    private static final int RC_CAMERA = 22;

    private static final int RP_CAMERA=121;
    private static final int RP_STORAGE=122;

    private static final String TAG = "FirebaseData";
    private static final String MY_PHOTO = "my_photo";
    private Uri mPhotoSelectedUri;
    private String mPhotoSelectedCameraUri;
    private String photoName="reporte";
    private String mCurrentPhotoPath;


    Categoria mCategoria;
    @BindView(R.id.imgFoto)
    AppCompatImageView imgFoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.tvTitulo)
    TextView tvTitulo;
    @BindView(R.id.etTituloReporte)
    TextInputEditText etTituloReporte;
    @BindView(R.id.etUbicacion)
    TextInputEditText etUbicacion;
    @BindView(R.id.etDescripcion)
    TextInputEditText etDescripcion;
    @BindView(R.id.btnEnviar)
    Button btnEnviar;
    @BindView(R.id.btnUbicacion)
    Button btnUbicacion;
    @BindView(R.id.tvFecha)
    TextView tvFecha;
    @BindView(R.id.imgGaleria)
    AppCompatImageView imgGaleria;
    @BindView(R.id.imgCamara)
    AppCompatImageView imgCamara;
    @BindView(R.id.linearParent)
    LinearLayout linearParent;
    @BindView(R.id.linearGaleria)
    LinearLayout linearGaleria;
    @BindView(R.id.linearFoto)
    LinearLayout linearFoto;
    @BindView(R.id.linearFotoTotal)
    LinearLayout linearFotoTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandar_reporte);
        ButterKnife.bind(this);
        configCategoria();
        configActionBar();
        configImageView();
        takePhoto();
        getUbicacion();


    }

    private void getUbicacion() {
        int permissionsCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionsCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show an explanation to the user

            } else {
                //No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

        }


    }

    private void takePhoto() {
        imgCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openCamera();
                dispatchTakePictureIntent();
            }
        });
        imgGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile;
            photoFile=createImageFile();
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.jimenez.appmunitacna", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, RC_CAMERA);
            }
        }
    }

    private File createImageFile() {
        final String timeStamp=new SimpleDateFormat("dd-MM-yyyy_HHmmss",Locale.ROOT)
                .format(new Date());
        final String imageFileName=MY_PHOTO+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;


    }

    private void openGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RC_GALLERY);
    }

    private void configImageView() {

    }

    private void configActionBar() {
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configCategoria() {
        mCategoria = MainActivity.sCategoria;
        tvTitulo.setText(mCategoria.getNombre());
        long timeInMillis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(timeInMillis));
        photoName= photoName+String.valueOf(timeInMillis);
        tvFecha.setText("" + dateString);

    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, RC_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case RC_GALLERY:
                    if(data!=null){
                        mPhotoSelectedUri=data.getData();
                        try {
                            Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver()
                                    ,mPhotoSelectedUri);
                            showPhoto();
                            imgFoto.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case RC_CAMERA:
                    mPhotoSelectedUri=addPicGallery();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                mPhotoSelectedUri);
                        showPhoto();
                        imgFoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private Uri addPicGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        mCurrentPhotoPath = null;
        return contentUri;
    }

    private void showPhoto() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT);
        param.weight = 1f;
        param2.weight = 0f;
        linearFotoTotal.setLayoutParams(param);
        linearFoto.setLayoutParams(param2);
        linearGaleria.setLayoutParams(param2);
    }

    @OnClick(R.id.btnUbicacion)
    public void onUbicacionClicked() {
        Intent intent=new Intent(MandarReporteActivity.this,MapsActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.btnEnviar)


    public void onClickEnviarReporte() {
        if (validateFields()) {
                //--------------------
                //--------------------
            ACProgressFlower dialog=new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("enviando")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();

                StorageReference reporteImageReference = FirebaseStorage.getInstance().getReference().child(FirebaseReferences.IMAGENES_REPORTES_REFERENCE);
                final StorageReference photoReference = reporteImageReference.child(photoName);
                if(mPhotoSelectedUri==null){
                    mPhotoSelectedUri=Uri.parse("android.resource://" + getPackageName() + "/" +
                            R.drawable.ic_airport_shuttle);
                }
                final UploadTask uploadTask=photoReference.putFile(mPhotoSelectedUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            savePhotoUrl(downloadUri.toString());
                            Log.d(TAG,""+downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (etDescripcion.getText().toString().trim().isEmpty()){
            etDescripcion.setError("Completa la descripción");
            etDescripcion.requestFocus();
            isValid = false;
        }
        if (etUbicacion.getText().toString().trim().isEmpty()){
            etUbicacion.setError("Completa la ubicación");
            etUbicacion.requestFocus();
            isValid = false;
        }

        if (etTituloReporte.getText().toString().trim().isEmpty()){
            etTituloReporte.setError("Completa el título del reporte");
            etTituloReporte.requestFocus();
            isValid = false;
        }



        return isValid;
    }

    private void savePhotoUrl(final String downloadUri) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(FirebaseReferences.REPORTES_REFERENCE);

        //Saving user into firebase db
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // User Exists
                        String userId= Global.currentDataUser.getUserId();
                        String reporteId=reference.push().getKey();
                        String categoria = tvTitulo.getText().toString();
                        String titulo = etTituloReporte.getText().toString();
                        String ubicacion = etUbicacion.getText().toString();
                        String descripcion = etDescripcion.getText().toString();
                        String imgURL = downloadUri;
                        long fecha = System.currentTimeMillis();
                        boolean estado = false;
                        Reporte reporte = new Reporte(reporteId,userId, categoria,titulo, ubicacion, descripcion, imgURL, fecha, estado);

                        reference.child(reporteId).setValue(reporte);
                        setAlertDialog("Reporte enviado con éxito!","Aceptar");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("ErrorFirebase", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    private void setAlertDialog(String message, String messageButton) {
        new AlertDialog.Builder(MandarReporteActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(messageButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MandarReporteActivity.this.finish();
                    }
                })
                .show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG,"titulo: "+etTituloReporte.getText().toString());
                Log.d(TAG,"hola!");
                onBackPressed();
                return true;
        }
        return false;
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    public void onBackPressed() {
        if (isEmpty(etTituloReporte) && isEmpty(etUbicacion) && isEmpty(etDescripcion)) {
            MandarReporteActivity.this.finish();

        } else {
            new AlertDialog.Builder(this)
                    .setMessage("¿Deseas dejar escribir tu solicitud?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MandarReporteActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }



}
