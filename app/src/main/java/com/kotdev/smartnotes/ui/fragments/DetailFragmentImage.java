package com.kotdev.smartnotes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.presenter.PresenterDetailImage;
import com.kotdev.smartnotes.app.view.ContractDetailImage;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.databinding.FragmentDetailImageBinding;
import com.kotdev.smartnotes.databinding.FragmentFirstBinding;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.activity.MainActivity;
import com.kotdev.smartnotes.ui.adapters.ImagesAdapter;
import com.kotdev.smartnotes.ui.adapters.SliderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

import static android.view.View.GONE;

public class DetailFragmentImage extends DaggerFragment implements ContractDetailImage.ViewContractImage {

    private FragmentDetailImageBinding binding;
    private int index;
    private List<Image> list;

    @Inject
    PresenterDetailImage presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentDetailImageBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        presenter.setAdapter(new SliderAdapter());
        index = requireArguments().getInt("position");
        viewPager();
    }

    private void init() {
        list = new ArrayList<>();
        presenter.attachView(this);
    }

    private void viewIsReady() {
        binding.viewPager.setAdapter(presenter.getAdapter());
        binding.viewPager.setCurrentItem(index);

        index = binding.viewPager.getCurrentItem();
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(getStrings(binding.viewPager.getCurrentItem() + 1, list.size()));
        List<Image> finalList = list;
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                index = position;
                Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(getStrings(position + 1, finalList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });
    }

    private List<Image> viewPager() {
        Disposable dispose = presenter.getAllImages().subscribe(images -> {
            for (Image image : images) {
                if (image.notes_images_id == null || image.notes_images_id == requireArguments().getLong("notes_image_id")) {
                    list.add(image);
                    presenter.getAdapter().setItems(list);
                }
            }
            viewIsReady();
        });
        return list;
    }

    private String getStrings(int pos, int size) {
        return String.format("%s из %s", pos, size);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_redactor) {

            return true;
        }

        if (item.getItemId() == R.id.action_delete) {
            presenter.delete(list.get(index));
            presenter.getAdapter().remove(index);
            if (viewPager().size() == 0) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_note", requireArguments().getParcelable("selected_note"));
                Navigation.findNavController(requireView()).navigate(R.id.action_detailFragmentImage_to_create_notes, bundle);
            } else {
                viewIsReady();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        presenter.detachView();
        Note note = requireArguments().getParcelable("selected_note");
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(note.title);
    }
}