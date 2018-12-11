package edu.uoc.curs.bookshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.uoc.curs.bookshare.database.Book;

public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.MyBookViewHolder> {

    class MyBookViewHolder extends RecyclerView.ViewHolder {
        ImageView portadaView;
        TextView tituloView;
        TextView autorView;

        private MyBookViewHolder(View itemView) {
            super(itemView);
            portadaView = itemView.findViewById(R.id.portada_imageView);
            tituloView = itemView.findViewById(R.id.titulo_textView);
            autorView = itemView.findViewById(R.id.autor_textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Book> mBooks;
    private MyBookListActivity mParentActivity;

    MyBookAdapter(Context context, MyBookListActivity parent) { mParentActivity = parent;
        mInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public MyBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.my_book_item, parent, false);
        return new MyBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBookViewHolder holder, int position) {
        if (mBooks != null) {
            final Book current = mBooks.get(position);
            holder.tituloView.setText(current.getTitle());
            holder.autorView.setText(current.getAuthor());
            if (!"".equals(current.getImageUrl())) {
                Picasso.get().load(current.getImageUrl()).into(holder.portadaView);
            } else {
                holder.portadaView.setImageResource(R.drawable.not_found);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = mParentActivity.getSupportFragmentManager().beginTransaction();
                    Fragment prev = mParentActivity.getSupportFragmentManager().findFragmentByTag("detail");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment newFragment = MyBookFragment.newInstance(current);
                    newFragment.show(ft, "detail");
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            // TODO: do something
            holder.portadaView.setImageResource(R.drawable.not_found);
        }
    }

    void setBooks(List<Book> books){
        mBooks = books;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mBooks has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mBooks != null)
            return mBooks.size();
        else return 0;
    }
}