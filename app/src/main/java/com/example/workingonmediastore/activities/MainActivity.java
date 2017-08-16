package com.example.workingonmediastore.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.workingonmediastore.R;
import com.example.workingonmediastore.adapters.MediaAdapter;

import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,MediaAdapter.OnClickThumbnailListener
{
    private static final int LOADER_ID = 0;

    private Context context = null;
    private Activity activity = null;
    private RecyclerView recyclerView = null;
    private GridLayoutManager gridLayoutManager = null;
    private MediaAdapter mediaAdapter = null;

    private Bundle loaderBundleArgs = null;
    private LoaderManager.LoaderCallbacks loaderCallbacks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {
        context = MainActivity.this;
        activity = MainActivity.this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if(context!=null)
            gridLayoutManager = new GridLayoutManager(context, 3);

        if(recyclerView!=null)
            recyclerView.setLayoutManager(gridLayoutManager);

        if(activity!=null)
            mediaAdapter = new MediaAdapter(activity);

        if(mediaAdapter!=null)
            recyclerView.setAdapter(mediaAdapter);

        loaderBundleArgs = null;
        loaderCallbacks = this;
        getSupportLoaderManager().initLoader(LOADER_ID,loaderBundleArgs,loaderCallbacks);

        getUserInstalledApplications();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader cursorLoader = null;
        Context context = null;
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            context = MainActivity.this;
            uri = MediaStore.Files.getContentUri("external");
            projection = new String[]
                    {
                            MediaStore.Files.FileColumns._ID,
                            MediaStore.Files.FileColumns.DATE_ADDED,
                            MediaStore.Files.FileColumns.DATA,
                            MediaStore.Files.FileColumns.MEDIA_TYPE
                    };
            selection =     MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                            + " OR "
                            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ;
            selectionArgs = null;
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
            cursorLoader = new CursorLoader(context,uri,projection,selection,selectionArgs,sortOrder);

            if(cursorLoader!=null)
                return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(mediaAdapter!=null)
            mediaAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(mediaAdapter!=null)
            mediaAdapter.changeCursor(null);
    }

    @Override
    public void onClickImage(Uri uri)
    {
        Toast.makeText(context,"Image Uri "+uri,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickVideo(Uri uri)
    {
        Toast.makeText(context,"Video Uri "+uri,Toast.LENGTH_LONG).show();
    }

    private void getUserInstalledApplications()
    {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : applicationInfoList)
        {
//            if(!isSystemPackage(applicationInfo))
//            {
                try
                {
                    Intent launchIntent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
                    if (launchIntent!=null)
                        Log.i("Pkg", "" + applicationInfo.loadLabel(packageManager));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

//            }
        }

        Log.i("Pkg", "" + applicationInfoList.size());
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo)
    {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
