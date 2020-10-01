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
import com.kotov.smartnotes.model.Item;
import com.kotov.smartnotes.utils.Utils;
import com.kotov.smartnotes.utils.alarm.NotificationUtils;
import com.kotov.smartnotes.utils.drawingview.DrawingViewActivity;
import com.kotov.smartnotes.utils.drawingview.DrawingViewImage;
import com.kotov.smartnotes.utils.drawingview.DrawingViewImageActivity;

import java.util.ArrayList;
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
import io.realm.Realm;
import io.realm.RealmResults;

import static androidx.constraintlayout.widget.ConstraintSet.VISIBLE;
import static com.kotov.smartnotes.file.Save.saveFile;

public class DetailActivity extends AppCompatActivity implements View {


    private SliderAdapter mAdapter;
    private Presenter presenter;
    private ViewPager2 mViewPager;
    private String id;
    private List<Item> list;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                list = presenter.get(id).getImage();
            } else {
                list = (List<Item>) intent.getExtras().getSerializable("list");
            }
            viewPager(list, intent);
        }
    }

    private void viewPager(List<Item> list, Intent intent) {
        mAdapter = new SliderAdapter(getApplicationContext(), list);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(intent.getIntExtra("position", 0));
        index = mViewPager.getCurrentItem();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getStrings(mViewPager.getCurrentItem() + 1, list.size()));
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                index = position;
                Objects.requireNonNull(getSupportActionBar()).setTitle(getStrings(position + 1, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });
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
    private List<Item> rst = new ArrayList<>();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 111) {
                Realm.getDefaultInstance().executeTransaction(realm -> {
                    list.remove(index);
                    list.add(new Item(data.getByteArrayExtra("image")));
                    realm.insertOrUpdate(list);
                });
                finish();
            } else {
                pickedImage = data.getData();
            }
        }
    }
    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_redactor) {
            Intent intent = new Intent(DetailActivity.this, DrawingViewImageActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("position", index);
            startActivityForResult(intent, 111);

           // Intent intent = new Intent(DetailActivity.this, DrawingViewImageActivity.class);
            //intent.putExtra("id", id);
            //intent.putExtra("position", index);
            //startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.action_detail_delete) {
            mAdapter.removeData(index);
            mAdapter.notifyDataSetChanged();
            if (list.size() == 0) {
                onSupportNavigateUp();
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getStrings(mViewPager.getCurrentItem() + 1, list.size()));
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