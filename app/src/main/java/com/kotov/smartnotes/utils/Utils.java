package com.kotov.smartnotes.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Window;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.model.Item;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utils {
    public static final Integer PRIORITY_RED = 1;
    public static final Integer PRIORITY_YELLOW = 2;
    public static final Integer PRIORITY_GREEN = 3;
    public static final Integer PRIORITY_DEFAULT = 4;
    public static final String[] PRIORITY = {"None", "низкая", "средняя", "важная"};
    public static final int[] PR = {4, 3, 2, 1};
    public static final String CATEGORY_DEFAULT = "default";
    public static final String CATEGORY_BOOKMAR$KS = "bookmark";

    public static void setSystemBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void setSystemBarColor(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(activity.getResources().getColor(i));
        }
    }


    public static Intent shareNote(String title, String description, String date, List<Item> rst, Context context) {
        ArrayList<Uri> imageUri = new ArrayList<>();
        for (int i = 0; i < rst.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rst.get(i).getImage(), 0, rst.get(i).getImage().length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
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
        Objects.requireNonNull(intent).setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, String.format("Title:\n%s\nDescription:\n%s\n%s", title, description, date) + "\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());

        return Intent.createChooser(intent, "Share with");
    }
}
