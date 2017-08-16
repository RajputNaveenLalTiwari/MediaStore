package com.example.workingonmediastore.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.workingonmediastore.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by 2114 on 09-06-2017.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder>
{
    private final Activity activity;
    private Cursor mediaAdapterCursor = null;
    private OnClickThumbnailListener onClickThumbnailListener;

    public interface OnClickThumbnailListener
    {
        void onClickImage(Uri uri);
        void onClickVideo(Uri uri);
    }

    public MediaAdapter(Activity activity)
    {
        this.activity = activity;
        onClickThumbnailListener = (OnClickThumbnailListener) activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        /*Bitmap bitmap = getBitmapFromMediaStore(position);

        if (bitmap!=null)
        {
            holder.getImageThumb().setImageBitmap(bitmap);
        }*/

        /*Glide.with(activity)
                .load(getUriFromMediaStore(position).toString())
                .into(holder.getImageThumb());*/

        Picasso.with(activity)
                .load(getUriFromMediaStore(position))
                .centerCrop()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .resize(96,96)
                .into(holder.getImageThumb());
    }

    @Override
    public int getItemCount()
    {
        return (mediaAdapterCursor == null) ? 0 : mediaAdapterCursor.getCount();
    }

    public void changeCursor(Cursor cursor)
    {
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor!=null)
            oldCursor.close();
    }

    private Cursor swapCursor(Cursor cursor)
    {
        if(mediaAdapterCursor==cursor)
            return null;

        Cursor oldCursor = mediaAdapterCursor;
        this.mediaAdapterCursor = cursor;

        if (cursor!=null)
            this.notifyDataSetChanged();

        return oldCursor;
    }

    private Bitmap getBitmapFromMediaStore(int position)
    {
        int id_index = mediaAdapterCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int media_type_index = mediaAdapterCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        mediaAdapterCursor.moveToPosition(position);

        Bitmap bitmap = null;
        ContentResolver content_resolver = null;
        long original_image_id = 0L;
        int type_of_thumbnail = 0;
        BitmapFactory.Options options = null;

        switch (mediaAdapterCursor.getInt(media_type_index))
        {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:

                content_resolver = activity.getContentResolver();
                original_image_id = mediaAdapterCursor.getLong(id_index);
                type_of_thumbnail = MediaStore.Images.Thumbnails.MICRO_KIND;
                options = null;
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(content_resolver,original_image_id,type_of_thumbnail,options);

                return bitmap;

            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:

                content_resolver = activity.getContentResolver();
                original_image_id = mediaAdapterCursor.getLong(id_index);
                type_of_thumbnail = MediaStore.Video.Thumbnails.MICRO_KIND;
                options = null;
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(content_resolver,original_image_id,type_of_thumbnail,options);

                return bitmap;
        }
        return null;
    }

    private Uri getUriFromMediaStore(int position)
    {
        int data_index = mediaAdapterCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        mediaAdapterCursor.moveToPosition(position);

        String data_string = mediaAdapterCursor.getString(data_index);
        Uri uri = Uri.parse("file://" + data_string);
        return uri;
    }

    private void getOnClickUri(int position)
    {
        int media_type_index = mediaAdapterCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int data_index = mediaAdapterCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        mediaAdapterCursor.moveToPosition(position);

        String data_string = mediaAdapterCursor.getString(data_index);
        Uri uri = Uri.parse("file://" + data_string);

//        String authorities = activity.getPackageName() + ".fileprovider";
//        Uri uri = FileProvider.getUriForFile(activity,authorities,new File(data_string));

        switch (mediaAdapterCursor.getInt(media_type_index))
        {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                onClickThumbnailListener.onClickImage(uri);
            break;
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                onClickThumbnailListener.onClickVideo(uri);
            break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView image_thumb;
        public ViewHolder(View itemView)
        {
            super(itemView);

            image_thumb = (ImageView) itemView.findViewById(R.id.image_thumb);
            image_thumb.setOnClickListener(this);
        }

        public ImageView getImageThumb()
        {
            return image_thumb;
        }

        @Override
        public void onClick(View v)
        {
            getOnClickUri(getAdapterPosition());
        }
    }
}
