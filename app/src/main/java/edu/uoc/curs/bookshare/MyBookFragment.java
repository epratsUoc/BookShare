package edu.uoc.curs.bookshare;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.uoc.curs.bookshare.database.Book;

public class MyBookFragment extends DialogFragment {
    private Book current;

    static MyBookFragment newInstance(Book b) {
        MyBookFragment f = new MyBookFragment();

        Bundle args = new Bundle();
        args.putString("titulo", b.getTitle());
        args.putString("autor", b.getAuthor());
        args.putString("dia", b.getDate());
        args.putString("descripcion", b.getDescription());
        args.putString("imagen", b.getImageUrl());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        current = new Book();

        current.setTitle(getArguments().getString("titulo"));
        current.setAuthor(getArguments().getString("autor"));
        current.setDate(getArguments().getString("dia"));
        current.setDescription(getArguments().getString("descripcion"));
        current.setImageUrl(getArguments().getString("imagen"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_book, container, false);

        TextView titulo = v.findViewById(R.id.book_title_detail);
        TextView autor = v.findViewById(R.id.book_author_detail);
        TextView dia = v.findViewById(R.id.book_date_detail);
        TextView descripcion = v.findViewById(R.id.book_description_detail);
        ImageView portada = v.findViewById(R.id.thumb_detail);

        titulo.setText(current.getTitle());
        autor.setText(current.getAuthor());
        dia.setText(current.getDate());
        descripcion.setText(current.getDescription());

        if (!"".equals(current.getImageUrl())) {
            Picasso.get().load(current.getImageUrl()).into(portada);
        } else {
            portada.setImageResource(R.drawable.not_found);
        }
        return v;
    }
}
