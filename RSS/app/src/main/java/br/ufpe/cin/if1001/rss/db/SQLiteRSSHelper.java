package br.ufpe.cin.if1001.rss.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import br.ufpe.cin.if1001.rss.domain.ItemRSS;


public class SQLiteRSSHelper extends SQLiteOpenHelper {
    //Nome do Banco de Dados
    private static final String DATABASE_NAME = "rss";
    //Nome da tabela do Banco a ser usada
    public static final String DATABASE_TABLE = "items";
    //Versão atual do banco
    private static final int DB_VERSION = 1;

    //alternativa
    Context c;

    private SQLiteRSSHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        c = context;
    }

    private static SQLiteRSSHelper db;

    //Definindo Singleton
    public static SQLiteRSSHelper getInstance(Context c) {
        if (db==null) {
            db = new SQLiteRSSHelper(c.getApplicationContext());
        }
        return db;
    }

    //Definindo constantes que representam os campos do banco de dados
    public static final String ITEM_ROWID = RssProviderContract._ID;
    public static final String ITEM_TITLE = RssProviderContract.TITLE;
    public static final String ITEM_DATE = RssProviderContract.DATE;
    public static final String ITEM_DESC = RssProviderContract.DESCRIPTION;
    public static final String ITEM_LINK = RssProviderContract.LINK;
    public static final String ITEM_UNREAD = RssProviderContract.UNREAD;

    //Definindo constante que representa um array com todos os campos
    public final static String[] columns = { ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD};

    //Definindo constante que representa o comando de criação da tabela no banco de dados
    private static final String CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
            ITEM_ROWID +" integer primary key autoincrement, "+
            ITEM_TITLE + " text not null, " +
            ITEM_DATE + " text not null, " +
            ITEM_DESC + " text not null, " +
            ITEM_LINK + " text not null, " +
            ITEM_UNREAD + " boolean not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //estamos ignorando esta possibilidade no momento
        throw new RuntimeException("nao se aplica");
    }

	//IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente

    public long insertItem(ItemRSS item) {
        return insertItem(item.getTitle(),item.getPubDate(),item.getDescription(),item.getLink());
    }

    public long insertItem(String title, String pubDate, String description, String link) {

        ContentValues itemRssCV = new ContentValues();
        itemRssCV.put(ITEM_TITLE, title);
        itemRssCV.put(ITEM_DESC, description);
        itemRssCV.put(ITEM_LINK, link);
        itemRssCV.put(ITEM_DATE, pubDate);
        itemRssCV.put(ITEM_UNREAD, 1);

        SQLiteDatabase writableDB = db.getWritableDatabase();

        return writableDB.insert(DATABASE_TABLE, null, itemRssCV);
    }

    public Cursor getItems() throws SQLException {
        SQLiteDatabase readableDB = db.getReadableDatabase();
        Cursor query = readableDB.query(DATABASE_TABLE, columns, ITEM_UNREAD + " = 1", null, null, null, ITEM_DATE);
        return query;
    }

    public ItemRSS getItemRSS(String link) throws SQLException {
        SQLiteDatabase readableDB = db.getReadableDatabase();
        Cursor query = readableDB.query(DATABASE_TABLE, columns, ITEM_LINK + "=?", new String[]{link}, null, null, ITEM_DATE);
        if (query.getCount() > 0) {
            query.moveToFirst();
            ItemRSS i = new ItemRSS(query.getString(query.getColumnIndexOrThrow(ITEM_TITLE)),
                    query.getString(query.getColumnIndexOrThrow(ITEM_LINK)),
                    query.getString(query.getColumnIndexOrThrow(ITEM_DATE)),
                    query.getString(query.getColumnIndexOrThrow(ITEM_DESC)));

            return i;
        }
        return null;
    }

    public boolean markAsUnread(String link) {
        return false;
    }

    public boolean markAsRead(String link) {
        SQLiteDatabase writableDB = db.getWritableDatabase();
        ContentValues itemRssCV = new ContentValues();
        itemRssCV.put(ITEM_UNREAD, 0);
        int hasUpdated = writableDB.update(DATABASE_TABLE, itemRssCV, ITEM_LINK + " = ?", new String[]{link});
        if (hasUpdated == 1) { return true; } 
        else { return false; }
    }

}