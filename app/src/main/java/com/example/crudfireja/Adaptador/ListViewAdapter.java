package com.example.crudfireja.Adaptador;

import android.content.Context;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.crudfireja.Modelo.Usuario;
import com.example.crudfireja.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<Usuario> usuarioData;
    LayoutInflater layoutInflater;
    Usuario usuarioModel;

    public ListViewAdapter(Context context, ArrayList<Usuario> usuarioData) {
        this.context = context;
        this.usuarioData = usuarioData;
        layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public int getCount() {
        return usuarioData.size();
    }

    @Override
    public Object getItem(int position) {
        return usuarioData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView==null){
            rowView = layoutInflater.inflate(R.layout.listausuario, null, true);
        }
        TextView nombres = rowView.findViewById(R.id.nombres);
        TextView apellidos = rowView.findViewById(R.id.apellidos);
        TextView correos = rowView.findViewById(R.id.correos);
        TextView passwords = rowView.findViewById(R.id.passwords);
        usuarioModel = usuarioData.get(position);
        nombres.setText(usuarioModel.getNombre());
        apellidos.setText(usuarioModel.getApellido());
        correos.setText(usuarioModel.getCorreo());
        passwords.setText(usuarioModel.getPassword());
        return rowView;
    }

}
