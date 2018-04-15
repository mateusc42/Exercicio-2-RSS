package br.ufpe.cin.if1001.rss.db;

import android.provider.BaseColumns;

//_ID e _COUNT adicionandos automaticamente por BaseColumns
public class RssProviderContract implements BaseColumns {
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String UNREAD = "unread";
}