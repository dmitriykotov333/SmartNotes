package com.kotov.smartnotes.activity.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.utils.Utils;
import com.kotov.smartnotes.utils.alarm.NotificationUtils;
import com.kotov.smartnotes.utils.drawingview.DrawingViewActivity;
import com.kotov.smartnotes.utils.drawingview.DrawingViewImage;
import com.kotov.smartnotes.utils.drawingview.DrawingViewImageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import static androidx.constraintlayout.widget.ConstraintSet.VISIBLE;
import static com.kotov.smartnotes.file.Save.saveFile;

public class DetailActivity extends AppCompatActivity implements View {


    private SliderAdapter mAdapter;
    private Presenter presenter;
    private ViewPager2 mViewPager;
    private String id;
    private int index;
    private List<Images> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        viewPager();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private List<Images> viewPager() {
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("update_date");
            if (id != null) {
                list = presenter.getAllImages(id);
                list.addAll(presenter.getAllImagesISNull());
            } else {
                list = presenter.getAllImagesISNull();
            }
        }
        mAdapter = new SliderAdapter(getApplicationContext(), list);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(intent.getIntExtra("position", 0));
        index = mViewPager.getCurrentItem();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getStrings(mViewPager.getCurrentItem() + 1, list.size()));
        List<Images> finalList = list;
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                index = position;
                Objects.requireNonNull(getSupportActionBar()).setTitle(getStrings(position + 1, finalList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });
        return list;
    }

    private void init() {
        list = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Utils.setSystemBarColor(this, R.color.colorPrimaryDark);
        mViewPager = findViewById(R.id.view_pager);
        presenter = new Presenter(this, getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String getStrings(int pos, int size) {
        return String.format("%s из %s", pos, size);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    Uri pickedImage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 111) {
                String time = getDate();
                presenter.deleteImage(list.get(index).getCreate_date());
                presenter.saveOneImage(new Images(data.getByteArrayExtra("image"), time, time), id);
                /*Realm.getDefaultInstance().executeTransaction(realm -> {
                    list.remove(index);
                    list.add(new Item(data.getByteArrayExtra("image")));
                    realm.insertOrUpdate(list);
                });*/
                finish();
            } else {
                pickedImage = data.getData();
            }
        }
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_redactor) {
            Intent intent = new Intent(DetailActivity.this, DrawingViewImageActivity.class);
            intent.putExtra("create_date", list.get(index).getCreate_date());
            intent.putExtra("position", index);
            startActivityForResult(intent, 111);

            // Intent intent = new Intent(DetailActivity.this, DrawingViewImageActivity.class);
            //intent.putExtra("id", id);
            //intent.putExtra("position", index);
            //startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.action_detail_delete) {
            mAdapter.removeData(index);
            if (viewPager().size() == 0) {
                onSupportNavigateUp();
            } else {
                viewPager();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void showProgress() {
        Toast.makeText(this, "Show progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {
        Toast.makeText(this, "Hide progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}