package com.kotdev.smartnotes.helpers.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.kotdev.smartnotes.room.image.Image;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static final String[] PRIORITY = {"None", "низкая", "средняя", "важная"};
    public static final int[] PR = {4, 3, 2, 1};
    public static final Integer PRIORITY_RED = 1;
    public static final Integer PRIORITY_YELLOW = 2;
    public static final Integer PRIORITY_GREEN = 3;
    public static final Integer PRIORITY_DEFAULT = 4;

    public static Intent shareNote(String title, String description, String date, List<Image> rst, Context context) throws UnsupportedEncodingException {
        ArrayList<Uri> imageUri = new ArrayList<>();
        for (int i = 0; i < rst.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rst.get(i).image, 0, rst.get(i).image.length);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
            imageUri.add(Uri.parse(path));
        }
        Intent intent = null;
        if (rst.size() > 1) {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUri);
        } else if (rst.size() == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri.get(0));
        }
        Objects.requireNonNull(intent).setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, String.format("Title:\n%s\nDescription:\n%s\n%s", title, description, date) + "\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());

        return Intent.createChooser(intent, "Share with");
    }
}
