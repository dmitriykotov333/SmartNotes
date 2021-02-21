package com.kotdev.smartnotes.ui.activity;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.databinding.ActivityMainBinding;
import com.kotdev.smartnotes.room.Database;
import com.kotdev.smartnotes.room.category.Category;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Objects;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends DaggerAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfig;
    private CompositeDisposable disposable;
    private Database database;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        appBarConfig = new AppBarConfiguration.Builder(binding.navView.getMenu())
                .setOpenableLayout(binding.drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(binding.navView, navController);
        menu = binding.navView.getMenu();
        database = new Database(getApplicationContext());
        disposable = new CompositeDisposable();
        toolbarTitle();
        menu();
        binding.navView.invalidate();
        binding.navView.setNavigationItemSelectedListener(this);
    }

    public void toolbarTitle() {
        disposable.add(database.getDatabase()
                .getCategoryDao()
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    if (!categories.isEmpty()) {
                        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true)
                                .build();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("category_id", categories.get(0));
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment)
                                .navigate(R.id.nav_notes, bundle, navOptions);
                        Objects.requireNonNull(getSupportActionBar()).setTitle(categories.get(0).title);
                    }
                }));
    }

    public void menu() {
        disposable.add(database.getDatabase()
                .getCategoryDao()
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    menu.clear();
                    menu.add(R.id.main_group, R.id.nav_notes, 0, "Add category").setChecked(true).setIcon(R.drawable.ic_add_black_24dp);
                    menu.setGroupCheckable(R.id.main_group, true, true);
                    menu.setGroupVisible(R.id.main_group, true);
                    for (Category category : categories) {
                        menu.add(R.id.main_group, (int) category.id, 0, category.title);
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_notes) {
            showCustomDialogCategory();
        }
        disposable.add(database.getDatabase()
                .getCategoryDao()
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    for (Category category : categories) {
                        if (category.title.equals(item.getTitle().toString())) {
                            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true)
                                    .build();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("category_id", category);
                            bundle.putLong("id", category.id);
                            Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment)
                                    .navigate(R.id.nav_notes, bundle, navOptions);
                            Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
                        }
                    }
                }));
        item.setChecked(true);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private Dialog getDialogCategoryOrPassword() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_category);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;
    }

    private void showCustomDialogCategory() {
        Dialog dialog = getDialogCategoryOrPassword();
        EditText editText = dialog.findViewById(R.id.category);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> dialog.dismiss());
        (dialog.findViewById(R.id.bt_accept)).setOnClickListener(v -> {
            Category category = new Category();
            category.title = editText.getText().toString();
            database.getDatabase().getCategoryDao().insert(category).subscribe();
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}