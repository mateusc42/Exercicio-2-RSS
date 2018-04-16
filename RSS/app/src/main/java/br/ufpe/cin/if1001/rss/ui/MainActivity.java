package br.ufpe.cin.if1001.rss.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.services.DownloadXmlService;

public class MainActivity extends Activity {

  private RecyclerView conteudoRSS;
  private final String RSS_FEED = "http://rss.cnn.com/rss/edition.rss";
  private SQLiteRSSHelper db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    db = SQLiteRSSHelper.getInstance(this);

    conteudoRSS = findViewById(R.id.conteudoRSS);

    conteudoRSS.setLayoutManager(new LinearLayoutManager(this));
    conteudoRSS.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    conteudoRSS.setItemAnimator(new DefaultItemAnimator());
  }

  @Override
  protected void onStart() {
    super.onStart();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    String linkfeed = preferences.getString("rssfeedlink", getResources().getString(R.string.rssfeed));
    Intent downloadService = new Intent(getApplicationContext(), DownloadXmlService.class);
    downloadService.putExtra("url", linkfeed);
    startService(downloadService);
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(DownloadXmlService.DOWNLOAD_COMPLETE);
    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadCompleteEvent, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onDownloadCompleteEvent);
  }

  private BroadcastReceiver onDownloadCompleteEvent = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Toast.makeText(getApplicationContext(), "News loaded, displaying feed.", Toast.LENGTH_LONG).show();
      new ExibirFeed().execute();
    }
  };

  @Override
  protected void onDestroy() {
    db.close();
    super.onDestroy();
  }

  class ExibirFeed extends AsyncTask<Void, Void, Cursor> {

    @Override
    protected Cursor doInBackground(Void... voids) {
      Cursor c = db.getItems();
      c.getCount();
      return c;
    }

    @Override
    protected void onPostExecute(Cursor c) {
      if (c != null) {
        conteudoRSS.setAdapter(new RecyclerAdapter(getApplicationContext(), c));
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.mainmenu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.btn_Config:
      startActivity(new Intent(this, ConfigActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}