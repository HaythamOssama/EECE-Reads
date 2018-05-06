package com.example.haytham.eecereads.List_All_Books;

/**
 * Created by haytham on 22/04/18.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.haytham.EECE_Reads.R;
import java.util.ArrayList;
import java.util.List;

class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private List<Books> booksList;                  // Holds the list of books to be displayed
    private FoundFragment foundFragment;            // Instance of FoundFragment fragment
    private BorrowedFragment borrowedFragment;      // Instance of BorrowedFragment fragment
    private RequestedFragment requestedFragment;    // Instance of RequestedFragment fragment
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public CardView container;

        /**
         * @brief defines the views of the adapters
         * @param view single item in recyclerview
         */
        public MyViewHolder(View view) {
            super(view);
            title     = (TextView) view.findViewById(R.id.title);
            genre     = (TextView) view.findViewById(R.id.name);
            year      = (TextView) view.findViewById(R.id.type);
            container = (CardView) view.findViewById(R.id.container);
        }
    }

    public BooksAdapter(List<Books> booksList, FoundFragment foundFragment) {
        this.booksList = booksList;
        this.foundFragment = foundFragment;
    }
    public BooksAdapter(List<Books> booksList, RequestedFragment requestedFragment) {
        this.booksList = booksList;
        this.requestedFragment = requestedFragment;
    }
    public BooksAdapter(List<Books> booksList, BorrowedFragment borrowedFragment) {
        this.booksList = booksList;
        this.borrowedFragment = borrowedFragment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_book_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * @brief Opens the dialog associated with every fragment
     * @param holder the holder of the item itself
     * @param position which item is clicked
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Books movie = booksList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getType());
        holder.year.setText(movie.getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundFragment != null){
                    foundFragment.OpenDialog(movie.getTitle(),movie.getName());
                }
                else if(borrowedFragment != null){
                    borrowedFragment.OpenDialog(movie.getTitle(),movie.getName());
                }
                else if(requestedFragment != null){
                    requestedFragment.OpenDialog(movie.getTitle(),movie.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    /**
     * @brief Used in searching bar
     * @param books books that need to be filtered
     */
    public void setFilter(List<Books> books) {
        booksList = new ArrayList<>();
        booksList.addAll(books);
        notifyDataSetChanged();
    }

}