package com.example.jimenez.appmunitacna;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimenez.appmunitacna.Clases.Usuario;
import com.example.jimenez.appmunitacna.objects.FirebaseReferences;
import com.example.jimenez.appmunitacna.objects.Global;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements onItemClickListener {

    //Para Login

    private static final int RC_SIGN_IN = 123;
    private static final String PROVEEDOR_DESCONOCIDO = "Proveedor Desconocido";
    private static final String TAG = "FirebaseDataSnapshot";
    public static Usuario currentUser=new Usuario();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static Boolean flagConection=false;


    @BindView(R.id.imgPhotoProfile)
    ImageView imgPhotoProfile;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvProvider)
    TextView tvProvider;
    //----------------------------------------------------------------------------------------------


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.containerMain)
    CoordinatorLayout containerMain;
    //App brus


    private categoriaAdapter adapter;
    List<Categoria> categorias = new ArrayList<>();

    public static final Categoria sCategoria=new Categoria();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configToolbar();
        configAdapter();
        configRecyclerView();
        generateCategoria();
        cargarDatosUsuario();

    }

    private void cargarDatosUsuario() {
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        Global.setGlobalDataUser(user);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(FirebaseReferences.USERS_REFERENCE);

        Query query=reference.orderByChild("correo").equalTo(mAuth.getCurrentUser().getEmail()); // de la bd selecciono nodo que contiene al usuario actual
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String currentUserKey = childSnapshot.getKey();
                    Global.setUserKey(currentUserKey);
                    Global.setCurrentDataUser(dataSnapshot.child(currentUserKey).getValue(Usuario.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Usuario currentUserData= GlobalCurrentUser.getCompleteUserData();
    }

    /*************************************************************************************************************************
    ****************************************METODOS LOGIN*********************************************************************
     *************************************************************************************************************************/

    private void configLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    cargarDatosUsuario();
                    Global.setGlobalDataUser(user);
                    onSetDataUser(user.getDisplayName(), user.getEmail(), user.getProviders() != null ?
                            user.getProviders().get(0) : PROVEEDOR_DESCONOCIDO);

                } else {
                    onSignedOutCleaned();
                    AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                            .build();
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false).setTosAndPrivacyPolicyUrls("https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/AuthUI.java", "https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/AuthUI.java")
                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                                                    googleIdp))
                            .setTheme(R.style.GreenTheme)
                            .setLogo(R.drawable.ic_pista)
                            .build(), RC_SIGN_IN);

                }
            }
        };
    }

    private void onSignedOutCleaned() {
        onSetDataUser("","","");
    }

    private void onSetDataUser(String userName, String email, String provider) {
        tvUserName.setText(userName);
        tvEmail.setText(email);
        tvProvider.setText(provider);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                cargarDatosUsuario();
                flagConection=false;


            } else {
                if(flagConection==false){
                    new AlertDialog.Builder(this)
                            .setTitle("Error de conexión")
                            .setMessage("Ups, parece que no tienes conexión a internet, será necesario para registrarte la primera vez," +
                                    "después podrás mandar reportes incluso sin conexión!")
                            .setCancelable(false)
                            .setPositiveButton("OK, Ya lo arreglé", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    flagConection=false;
                                }
                            })
                            .show();
                    flagConection=true;
                }

            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    /*************************************************************************************************************************
     ****************************************FIN METODOS LOGIN*********************************************************************
     *************************************************************************************************************************/


    private void generateCategoria() {
        Categoria arrayCategoria[]=new Categoria[5];
        arrayCategoria[0]=new Categoria("Parques y Jardines","Pistas 1",R.drawable.ic_arboles);
        arrayCategoria[1]=new Categoria("Pistas y Veredas","Pistas 1",R.drawable.ic_pista);
        arrayCategoria[2]=new Categoria("Seguridad","Pistas 1",R.drawable.ic_seguridad);
        arrayCategoria[3]=new Categoria("Transporte Público","Pistas 1",R.drawable.ic_autobu);
        arrayCategoria[4]=new Categoria("Limpieza Pública","Pistas 1",R.drawable.ic_limpieza);

        for (int i=0;i<arrayCategoria.length;i++) {
            adapter.add(arrayCategoria[i]);
        }


    }

    private void configRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
    }

    private void configAdapter() {
        adapter = new categoriaAdapter(categorias, this);
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;

            case R.id.action_editar_perfil:
                Intent intent=new Intent(MainActivity.this,PerfilUsuarioActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_record:

                Intent intentl=new Intent(MainActivity.this,RecordActivity.class);
                startActivity(intentl);
                return true;

            default:
                    return super.onOptionsItemSelected(item);
        }
    }


    /************
     * Metodos implementados por la interfaz OnItemClickListener*************/

    @Override
    public void onItemClick(Categoria categoria) {
        sCategoria.setNombre(categoria.getNombre());
        Intent intent=new Intent(MainActivity.this,MandarReporteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(Categoria categoria) {

    }


}

/***************************************************************************/
