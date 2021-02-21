package com.kotdev.smartnotes.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.presenter.PresenterNotes;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.databinding.FragmentFirstBinding;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.adapters.NotesAdapter;
import com.kotdev.smartnotes.ui.adapters.NotesAdapterFix;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class FirstFragment extends DaggerFragment implements ContractNotes.ViewContractNotes, CallbackClickListener<Note> {


    @Inject
    PresenterNotes presenter;

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentFirstBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    Bundle bundle;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        bundle = getArguments();
        initAdapter();
        initAdapterFix();
        binding.fab.setOnClickListener(view1 -> {
            Bundle createNoteBundle = new Bundle();
            createNoteBundle.putParcelable("selected_note", null);
            if (bundle != null) {
                if (bundle.getParcelable("category_id") != null) {
                    Category category = requireArguments().getParcelable("category_id");
                    createNoteBundle.putString("category", category.title);
                } else {
                    createNoteBundle.putString("category", requireArguments().getString("id_fragmentTwo_to_FragmentOneString"));
                }
            } else {
                createNoteBundle.putString("category", "All notes");
            }
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, createNoteBundle);
        });
    }

    private void initAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setDrawingCacheEnabled(true);
        binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        presenter.setAdapter(new NotesAdapter());
        if (bundle != null) {
            if (bundle.getParcelable("category_id") != null) {
                Category category = requireArguments().getParcelable("category_id");
                presenter.getNotes(category.id).removeObservers(getViewLifecycleOwner());
                presenter.getNotes(category.id).observe(getViewLifecycleOwner(), notes -> {
                    presenter.getAdapter().setNotes(notes);
                    binding.recyclerView.setAdapter(presenter.getAdapter());
                });
            } else {
                presenter.getNotes(requireArguments().getLong("id_fragmentTwo_to_FragmentOne")).removeObservers(getViewLifecycleOwner());
                presenter.getNotes(requireArguments().getLong("id_fragmentTwo_to_FragmentOne")).observe(getViewLifecycleOwner(), notes -> {
                    presenter.getAdapter().setNotes(notes);
                    binding.recyclerView.setAdapter(presenter.getAdapter());
                });
            }
            presenter.getAdapter().setClickListener(this);
            presenter.deleteNotes().attachToRecyclerView(binding.recyclerView);
        }
    }


    private void initAdapterFix() {
        binding.recyclerViewFix.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFix.setHasFixedSize(true);
        binding.recyclerViewFix.setItemViewCacheSize(20);
        binding.recyclerViewFix.setDrawingCacheEnabled(true);
        binding.recyclerViewFix.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        presenter.setAdapterFix(new NotesAdapterFix());
        if (bundle != null) {
            if (bundle.getParcelable("category_id") != null) {
                Category category = requireArguments().getParcelable("category_id");
                presenter.getNotesFix(category.id).removeObservers(getViewLifecycleOwner());
                presenter.getNotesFix(category.id).observe(getViewLifecycleOwner(), notes -> {
                    presenter.getAdapterFix().setNotes(notes);
                    binding.recyclerViewFix.setAdapter(presenter.getAdapterFix());
                });
                presenter.getAdapterFix().setClickListener(this);
                presenter.deleteNotes().attachToRecyclerView(binding.recyclerView);
            } else {
                presenter.getNotesFix(requireArguments().getLong("id_fragmentTwo_to_FragmentOne")).removeObservers(getViewLifecycleOwner());
                presenter.getNotesFix(requireArguments().getLong("id_fragmentTwo_to_FragmentOne")).observe(getViewLifecycleOwner(), notes -> {
                    presenter.getAdapterFix().setNotes(notes);
                    binding.recyclerViewFix.setAdapter(presenter.getAdapterFix());
                });
                presenter.getAdapterFix().setClickListener(this);
                presenter.deleteNotes().attachToRecyclerView(binding.recyclerViewFix);
            }
        }
    }

    @Override
    public void clickListener(int position, Note model) {
        if (model.password.isEmpty()) {
            navigate(model);
        } else {
            dialog(model);
        }
    }

    private void dialog(Note item) {
        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_category);
        dialog.setCancelable(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        EditText editText = dialog.findViewById(R.id.category);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> dialog.dismiss());
        (dialog.findViewById(R.id.bt_accept)).setOnClickListener(v -> {
            if (editText.getText().toString().equals(item.password)) {
                navigate(item);
                dialog.dismiss();
            } else {
                Toast.makeText(requireActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void navigate(Note model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_note", model);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

}