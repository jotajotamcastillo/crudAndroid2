package com.example.crudandroid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crudandroid2.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    EditText nombre, apellido, correo, contrasena;
    ListView lvpersonas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre = findViewById(R.id.editTextNombre);
        apellido = findViewById(R.id.editTextApellido);
        correo= findViewById(R.id.editTextCorreo);
        contrasena= findViewById(R.id.editTextPassword);
        lvpersonas = findViewById(R.id.lvPersonas);
        IniciarFirebase();
        ListarDatos();
        lvpersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada=(Persona) parent.getItemAtPosition(position);
                nombre.setText(personaSeleccionada.getNombre());
                apellido.setText(personaSeleccionada.getApellidos());
                correo.setText(personaSeleccionada.getCorreo());
                contrasena.setText(personaSeleccionada.getContrasena());
            }
        });
    }

    private void ListarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for(DataSnapshot objSnaptshot: dataSnapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPerson.add(p);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1,listPerson);
                    lvpersonas.setAdapter(arrayAdapterPersona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void IniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String nombr = nombre.getText().toString();
        String apell = apellido.getText().toString();
        String corre = correo.getText().toString();
        String contr = contrasena.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if(nombr.equals("")||apell.equals("")||corre.equals("")||contr.equals("")){
                    Validacion();
                }
                else{
                    Persona p = new Persona();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombr);
                    p.setApellidos(apell);
                    p.setCorreo(corre);
                    p.setContrasena(contr);
                    databaseReference.child("Persona").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Agregado", Toast.LENGTH_LONG).show();
                    Limpiar();
                }
                break;
            }
            case R.id.icon_save:{
                Persona p = new Persona();
                p.setId(personaSeleccionada.getId());
                p.setNombre(nombre.getText().toString().trim());
                p.setApellidos(apellido.getText().toString().trim());
                p.setCorreo(correo.getText().toString().trim());
                p.setContrasena(contrasena.getText().toString().trim());
                databaseReference.child("Persona").child(p.getId()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                Limpiar();
                break;
            }
            case R.id.icon_delete:{
                Persona p = new Persona();
                p.setId(personaSeleccionada.getId());
                databaseReference.child("Persona").child(p.getId()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                Limpiar();
                break;
            }
            default:break;
        }
        return true;
    }

    private void Limpiar() {
        nombre.setText("");
        apellido.setText("");
        correo.setText("");
        contrasena.setText("");
    }

    private void Validacion(){
        String nombr = nombre.getText().toString();
        String apell = apellido.getText().toString();
        String corre = correo.getText().toString();
        String contr = contrasena.getText().toString();

        if(nombr.equals("")){
            nombre.setError("Campo requerido.");
        }else if(apell.equals("")){
            apellido.setError("Campo requerido.");
        }else if(corre.equals("")){
            correo.setError("Campo requerido.");
        }else if(contr.equals("")){
            contrasena.setError("Campo requerido.");
        }
    }
}