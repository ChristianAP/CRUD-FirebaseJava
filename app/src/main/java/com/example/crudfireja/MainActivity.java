package com.example.crudfireja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crudfireja.Adaptador.ListViewAdapter;
import com.example.crudfireja.Modelo.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Usuario> listUsuario = new ArrayList<Usuario>();
    ArrayAdapter<Usuario> arrayAdapterUsuario;
    ListViewAdapter listViewAdapter;
    LinearLayout lineaEditar;
    EditText nombre, apellido, correo, contraseña;
    Button cancelar;
    ListView lista;
    Usuario usuarioSeleccionado;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        correo = findViewById(R.id.correo);
        contraseña = findViewById(R.id.contraseña);
        cancelar = findViewById(R.id.cancelar);
        lista = findViewById(R.id.lista);
        lineaEditar = findViewById(R.id.lineaEditar);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                nombre.setText(usuarioSeleccionado.getNombre());
                apellido.setText(usuarioSeleccionado.getApellido());
                correo.setText(usuarioSeleccionado.getCorreo());
                contraseña.setText(usuarioSeleccionado.getPassword());
                /// hacer visible el linear layaut
                lineaEditar.setVisibility(View.VISIBLE);
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineaEditar.setVisibility(View.GONE);
                usuarioSeleccionado = null;
            }
        });
        inicializarFirebase();
        listarusuario();

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarusuario() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsuario.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Usuario u = objSnaptshot.getValue(Usuario.class);
                    listUsuario.add(u);
                }
                /// iniciar propio adaptador
                listViewAdapter = new ListViewAdapter(MainActivity.this, listUsuario);
                //arrayAdapterUsuario = new ArrayAdapter<Usuario>(
                //     MainActivity.this, android.R.layout.simple_list_item_1, listUsuario
                // );
                lista.setAdapter(listViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crudmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombres = nombre.getText().toString();
        String apellidos = apellido.getText().toString();
        String correos = correo.getText().toString();
        String password = contraseña.getText().toString();

        switch (item.getItemId()) {
            case R.id.agregar:
                insertar();
                break;
            case R.id.guardar:
                if (usuarioSeleccionado != null) {
                    if (validarinput() == false) {
                        Usuario u = new Usuario();
                        u.setIdusuario(usuarioSeleccionado.getIdusuario());
                        u.setNombre(nombres);
                        u.setApellido(apellidos);
                        u.setCorreo(correos);
                        u.setPassword(password);
                        databaseReference.child("Usuario").child(u.getIdusuario()).setValue(u);
                        Toast.makeText(this, "ACTUALIZADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        lineaEditar.setVisibility(View.GONE);
                        usuarioSeleccionado = null;
                    }
                }else{
                    Toast.makeText(this, "SELECCIONE UNA PERSONA!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.eliminar:
                if (usuarioSeleccionado!=null){
                    Usuario u2 = new Usuario();
                    u2.setIdusuario(usuarioSeleccionado.getIdusuario());
                    databaseReference.child("Usuario").child(u2.getIdusuario()).removeValue();
                    lineaEditar.setVisibility(View.GONE);
                    usuarioSeleccionado = null;
                    Toast.makeText(this, "USUARIO ELIMINADO", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Selecciones a quien Eliminar", Toast.LENGTH_SHORT).show();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validarinput() {
        String nombret = nombre.getText().toString();
        String apellidot = apellido.getText().toString();
        String correot = correo.getText().toString();
        String passt = contraseña.getText().toString();
        if (nombret.isEmpty() || nombret.length() < 3) {
            showError(nombre, "Nombre Invalido");
            return true;
        } else if (apellidot.isEmpty() || apellidot.length() < 3) {
            showError(apellido, "Apellido Invalido");
            return true;
        } else if (correot.isEmpty() || correot.length() < 3) {
            showError(correo, "Correo Invalido");
            return true;
        } else if (passt.isEmpty() || passt.length() < 3) {
            showError(contraseña, "FALTA CONTRASEÑA");
            return true;
        } else {
            return false;
        }
    }




    private void insertar() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                MainActivity.this
        );
        View mView = getLayoutInflater().inflate(R.layout.insertar, null);
        Button insert = (Button) mView.findViewById(R.id.insertar);
        final EditText mNombre = (EditText) mView.findViewById(R.id.inputName);
        final EditText mApellido = (EditText) mView.findViewById(R.id.inputLast);
        final  EditText mCorreo = (EditText) mView.findViewById(R.id.inputCorreo);
        final EditText mPassword = (EditText) mView.findViewById(R.id.inputPass);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombres = mNombre.getText().toString();
                String apellidos = mApellido.getText().toString();
                String correos = mCorreo.getText().toString();
                String passwords = mPassword.getText().toString();
                if (nombres.isEmpty() || nombres.length()< 3){
                    showError(mNombre, "Nombre Inválido¡¡¡");
                }else if(apellidos.isEmpty() || apellidos.length() <4){
                    showError(mApellido, "Apellido Inválido¡¡¡");
                }else if(correos.isEmpty() || correos.length() <4){
                    showError(mCorreo, "Correo Inválido¡¡¡");
                }else if(passwords.isEmpty() || passwords.length() <4){
                    showError(mPassword, "Contraseña Invalida¡¡¡");
                }else{
                    Usuario u = new Usuario();
                    u.setIdusuario(UUID.randomUUID().toString());
                    u.setNombre(nombres);
                    u.setApellido(apellidos);
                    u.setCorreo(correos);
                    u.setPassword(passwords);
                    databaseReference.child("Usuario").child(u.getIdusuario()).setValue(u);
                    Toast.makeText(MainActivity.this, "REGISTRADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
    public void showError(EditText input, String s){
        input.requestFocus();
        input.setError(s);
    }

}