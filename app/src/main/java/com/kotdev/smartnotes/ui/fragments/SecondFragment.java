package com.kotdev.smartnotes.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.presenter.PresenterCreateNotes;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.databinding.FragmentSecondBinding;
import com.kotdev.smartnotes.helpers.utils.Utils;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.checkbox.Checkbox;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.activity.MainActivity;
import com.kotdev.smartnotes.ui.adapters.ImagesAdapter;
import com.kotdev.smartnotes.ui.adapters.draggable.AdapterCheck;
import com.kotdev.smartnotes.ui.adapters.draggable.DragItemTouchHelper;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import static com.kotdev.smartnotes.helpers.utils.Utils.PR;
import static com.kotdev.smartnotes.helpers.utils.Utils.PRIORITY;

public class SecondFragment extends DaggerFragment implements ContractNotes.ViewContractCreateNotes, CallbackClickListener<Image> {


    @Inject
    PresenterCreateNotes presenter;

    private FragmentSecondBinding binding;
    private Note mNoteFinal;
    private String categoryTitle;
    private CompositeDisposable disposable;
    private final List<Checkbox> listCheckboxes = new ArrayList<>();
    private final List<Image> listImages = new ArrayList<>();
    private Integer single_choice_selected = 4;
    private long ACTION_DELETE;
    private long ID_DEFAULT;
    private String ACTION_CATEGORY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        disposable = new CompositeDisposable();
        mNoteFinal = new Note();
        ID_DEFAULT = Long.parseLong(getDate().replace("-", "").replace(":", "").replace(" ", ""));
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean checkTextUtils() {
        return !TextUtils.isEmpty(Objects.requireNonNull(binding.editeTitle.getText()).toString())
                || !TextUtils.isEmpty(Objects.requireNonNull(binding.noteText.getText()).toString());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mNoteFinal.id = ID_DEFAULT;
        mNoteFinal.title = Objects.requireNonNull(binding.editeTitle.getText()).toString();
        mNoteFinal.content = Objects.requireNonNull(binding.noteText.getText()).toString();
        mNoteFinal.priority = single_choice_selected;
        mNoteFinal.password = Objects.requireNonNull(binding.editePassword.getText()).toString();
        if (binding.checkBox.isChecked()) {
            mNoteFinal.fixNote = 1;
        } else {
            mNoteFinal.fixNote = -1;
        }
        categoryTitle = Objects.requireNonNull(binding.editeCategory.getText()).toString();
        if (item.getItemId() == R.id.action_save) {
            presenter.insertNote(checkTextUtils(), categoryTitle, mNoteFinal, getDate(), requireArguments().getParcelable("selected_note"), ACTION_DELETE, requireView());
            return true;
        }
        if (item.getItemId() == R.id.action_delete) {
            Bundle bundle = new Bundle();
            Note note = requireArguments().getParcelable("selected_note");
            if (note != null) {
                note.id = ACTION_DELETE;
                presenter.deleteNotes(mNoteFinal);
                bundle.putString("id_fragmentTwo_to_FragmentOneString", ACTION_CATEGORY);
            }
            bundle.putLong("id_fragmentTwo_to_FragmentOne", presenter.getCategoryId(categoryTitle));
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_SecondFragment_to_FirstFragment, bundle);
            return true;
        }
        if (item.getItemId() == R.id.action_priority) {
            showSingleChoiceDialog();
        }
        if (item.getItemId() == R.id.action_to_txt) {
            presenter.share(requireActivity(), binding.editeTitle.getText().toString(), presenter.getCategoryById(mNoteFinal.categoryId), binding.noteText.getText().toString());
        }
        if (item.getItemId() == R.id.action_photo) {
            presenter.check(requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            cropActivityResultLauncher.launch(actionPick());
        }
        if (item.getItemId() == R.id.action_read_aloud) {
            presenter.voiceInput(requireActivity(), action_read_aloud, listenToSpeech);
        }
        if (item.getItemId() == R.id.action_read) {
            presenter.voiceRead(requireActivity(), binding.noteText.getText().toString());
        }
        if (item.getItemId() == R.id.action_sharing) {
            try {
                startActivity(Utils.shareNote(mNoteFinal.title, mNoteFinal.content, mNoteFinal.create_date, listImages, requireContext()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (item.getItemId() == R.id.action_drawing) {
            presenter.check(requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Bundle bundle = new Bundle();
            if (requireArguments().getParcelable("selected_note") != null) {
                bundle.putParcelable("selected_note", requireArguments().getParcelable("selected_note"));
            } else {
                bundle.putParcelable("selected_note", null);
            }
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_create_notes_to_drawingFragment, bundle);
        }
        if (item.getItemId() == R.id.action_checkbox) {
            String time = getDate();
            Checkbox checkBox = new Checkbox();
            checkBox.id = Long.parseLong(getDate().replace("-", "").replace(":", "").replace(" ", ""));
            checkBox.check = -1;
            checkBox.create_date = time;
            checkBox.update_date = time;
            checkBox.notes_checkboxes_id = null;
            listCheckboxes.add(checkBox);
            presenter.insertWithoutId(checkBox);
            onResume();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    ActivityResultLauncher<Intent> action_read_aloud = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            data -> {
                Intent intent = data.getData();
                if (intent != null) {
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
            });

    @SuppressLint("SetTextI18n")
    ActivityResultLauncher<Intent> listenToSpeech = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            data -> {
                Intent intent = data.getData();
                if (intent != null) {
                    ArrayList<String> suggestedWords = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    binding.noteText.setText(Objects.requireNonNull(binding.noteText.getText()).toString() + " " + Objects.requireNonNull(suggestedWords).iterator().next());
                }
            });

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Priority");
        builder.setSingleChoiceItems(PRIORITY, 0, (dialogInterface, i) -> single_choice_selected = PR[i]);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
        {
            if (single_choice_selected.equals(Utils.PRIORITY_RED)) {
                binding.noteText.setBackgroundColor(getResources().getColor(R.color.red_600, null));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_YELLOW)) {
                binding.noteText.setBackgroundColor(getResources().getColor(R.color.yellow, null));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_GREEN)) {
                binding.noteText.setBackgroundColor(getResources().getColor(R.color.green, null));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_DEFAULT)) {
                binding.noteText.setBackgroundColor(0);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
/*
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mMode = savedInstanceState != null ? savedInstanceState.getInt("mode") : -1;
        if(mMode == EDIT_MODE_ENABLED){
            enableEditMode();
        }
    }*/

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        if (!getIncomingIntent()) {
            setNoteProperties();
        } else {
            binding.editeCategory.setText(requireArguments().getString("category"));
        }
        presenter.setAdapter(new ImagesAdapter());
        presenter.setAdapterCheck(new AdapterCheck());
    }

    @Override
    public void onResume() {
        super.onResume();
        disposable.add(presenter.getAllImages().subscribe(images -> {
            listImages.clear();
            for (Image image : images) {
                if (image.notes_images_id == null || image.notes_images_id == ACTION_DELETE) {
                    listImages.add(image);
                    presenter.getAdapter().setItems(listImages);
                }
            }
            presenter.viewIsReady();
        }));
        disposable.add(presenter.getAllCheckbox().subscribe(checkboxes -> {
            listCheckboxes.clear();
            for (Checkbox checkbox : checkboxes) {
                if (checkbox.notes_checkboxes_id == null || checkbox.notes_checkboxes_id == ACTION_DELETE) {
                    listCheckboxes.add(checkbox);
                    presenter.getAdapterCheck().setCheckboxes(listCheckboxes);
                }
            }
            presenter.viewIsReady();
        }));
    }

    @SuppressLint("IntentReset")
    public Intent actionPick() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        return i;
    }

    ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            data -> {
                Intent intent = data.getData();
                if (intent != null) {
                    String time = getDate();
                    try (InputStream stream = requireActivity().getContentResolver().openInputStream(intent.getData())) {
                        if (stream != null) {
                            byte[] inputData = getBytes(stream);
                            Image image = new Image();
                            image.id = Long.parseLong(getDate().replace("-", "").replace(":", "").replace(" ", ""));
                            image.image = inputData;
                            image.create_date = time;
                            image.update_date = time;
                            image.notes_images_id = null;
                            listImages.add(image);
                            presenter.insertWithoutId(image);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    public byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream rst = new ByteArrayOutputStream();
        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            rst = byteBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rst.toByteArray();
    }


    private boolean getIncomingIntent() {
        if (requireArguments().getParcelable("selected_note") != null) {
            mNoteFinal = requireArguments().getParcelable("selected_note");
            ACTION_DELETE = mNoteFinal.id;
            ACTION_CATEGORY = presenter.getCategoryById(mNoteFinal.categoryId);
            single_choice_selected = mNoteFinal.priority;
            binding.editeCategory.setText(presenter.getCategoryById(mNoteFinal.categoryId));
            binding.editePassword.setText(mNoteFinal.password);
            binding.editeCategory.setText(presenter.getCategoryById(mNoteFinal.categoryId));
            binding.checkBox.setChecked(mNoteFinal.fixNote == 1);
            return false;
        }
        return true;
    }

    private void setNoteProperties() {
        binding.editeTitle.setText(mNoteFinal.title);
        binding.noteText.setText(mNoteFinal.content);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.deleteImageNullNotesId();
        presenter.deleteCheckboxNullNotesId();
        binding = null;
        presenter.onDestroy();
        //presenter.detachView();
        ((MainActivity) requireActivity()).menu();
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(categoryTitle);
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void imagesListIsEmpty(int view) {

    }


    @Override
    public void imagesListNotEmpty(int view) {
        presenter.getAdapter().setClickListener(this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setDrawingCacheEnabled(true);
        binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recyclerView.setAdapter(presenter.getAdapter());

        binding.recyclerViewCheck.setHasFixedSize(true);
        binding.recyclerViewCheck.setItemViewCacheSize(20);
        binding.recyclerViewCheck.setDrawingCacheEnabled(true);
        binding.recyclerViewCheck.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recyclerViewCheck.setAdapter(presenter.getAdapterCheck());

        presenter.getAdapterCheck().setCallbackUpdateCheck((checking, create_date) -> presenter.updateCheck(checking, create_date));
        presenter.getAdapterCheck().setOnItemClickListener((view1, social, i) -> {
            presenter.delete(social);
            binding.recyclerViewCheck.scrollToPosition(presenter.getAdapterCheck().getItemCount());

        });
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(presenter.getAdapterCheck()));
        mItemTouchHelper.attachToRecyclerView(binding.recyclerViewCheck);
        presenter.getAdapterCheck().setDragListener(mItemTouchHelper::startDrag);
    }

    @Override
    public void clickListener(int position, Image model) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        if (requireArguments().getParcelable("selected_note") != null) {
            bundle.putLong("notes_image_id", ACTION_DELETE);
            bundle.putParcelable("selected_note", requireArguments().getParcelable("selected_note"));
        } else {
            bundle.putLong("notes_image_id", ID_DEFAULT);
            bundle.putParcelable("selected_note", mNoteFinal);
        }
        Navigation.findNavController(requireView())
                .navigate(R.id.action_create_notes_to_detailFragmentImage, bundle);
    }
}