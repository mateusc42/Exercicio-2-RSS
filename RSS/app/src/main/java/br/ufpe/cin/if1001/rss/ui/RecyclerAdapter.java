package br.ufpe.cin.if1001.rss.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.db.RssProviderContract;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemRSSHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteRSSHelper db;

    public RecyclerAdapter(Context context, Cursor cursor) {
        db = SQLiteRSSHelper.getInstance(context);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public ItemRSSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ItemRSSHolder holder = new ItemRSSHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemRSSHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.holderTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.TITLE)));
        holder.holderPubDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.DATE)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class ItemRSSHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView holderTitle;
        TextView holderPubDate;

        public ItemRSSHolder(View itemView) {
            super(itemView);
            this.holderTitle = itemView.findViewById(R.id.itemTitulo);
            this.holderPubDate = itemView.findViewById(R.id.itemData);
            itemView.setOnClickListener(this);
        }

        //Abrir a noticia do browser vai ser uma funcionalidade do viewHolder visto que RecyclerView nao possui setOnItemClickListener
        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            db.markAsRead(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.LINK)));
            Intent seeOnBrowser = new Intent(Intent.ACTION_VIEW);
            seeOnBrowser.setFlags(FLAG_ACTIVITY_NEW_TASK);          //Flag necessaria para executar um intent nao estando numa activity
            Uri itemUrl = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.LINK)));
            seeOnBrowser.setData(itemUrl);
            if (seeOnBrowser.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(seeOnBrowser);
            }
        }
    }
}