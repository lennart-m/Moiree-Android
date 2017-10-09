package de.lennartmeinhardt.android.moiree.menu.color;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.lennartmeinhardt.android.moiree.BR;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.ItemPersistableMoireeColorsBinding;
import de.lennartmeinhardt.android.moiree.databinding.MenuLoadMoireeColorsBinding;
import de.lennartmeinhardt.android.moiree.util.CallbackAsyncTask;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseDeleterTask;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseQuerierTask;
import de.lennartmeinhardt.android.moiree.util.db.Querier;
import de.lennartmeinhardt.android.moiree.util.db.room.AppDatabase;
import de.lennartmeinhardt.android.moiree.util.db.room.PersistableMoireeColors;

public class LoadMoireeColorsMenu extends BaseMoireeColorsSetupFragment {

    private static final String KEY_CUSTOM_COLORS_EXPANDED = "loadMoireeColorsSetup:customColorsExpanded";
    private static final String KEY_PRESET_COLORS_EXPANDED = "loadMoireeColorsSetup:presetColorsExpanded";

    private MenuLoadMoireeColorsBinding binding;

    private MoireeColorsAdapter customColorsAdapter;

    private final CallbackAsyncTask.Callback<List<PersistableMoireeColors>> deleteListener = new CallbackAsyncTask.Callback<List<PersistableMoireeColors>>() {
        @Override
        public void call(List<PersistableMoireeColors> deletedColors) {
            beginMenuBoundsTransition();

            for(PersistableMoireeColors deletedColor : deletedColors) {
                customColorsAdapter.deleteMoireeColors(deletedColor);
            }
        }
    };

    private final Observable.OnPropertyChangedCallback expandedTransitionStarter = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.expanded)
                beginMenuTransition(createMenuBoundsAndHeaderIndicatorTransition());
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.menu_load_moiree_colors, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        initializePresetColorsCard();
        initializeCustomColorsCard();
    }

    private void initializePresetColorsCard() {
        List<PersistableMoireeColors> presetColors = getPresetMoireeColors();
        int recyclerViewColumns = getResources().getInteger(R.integer.color_list_view_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), recyclerViewColumns);
        layoutManager.setAutoMeasureEnabled(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.presetMoireeColorsSetup.presetColorsList.setLayoutManager(layoutManager);
        binding.presetMoireeColorsSetup.presetColorsList.setNestedScrollingEnabled(false);
        binding.presetMoireeColorsSetup.presetColorsList.setAdapter(new MoireeColorsAdapter(presetColors, false));
        binding.presetMoireeColorsSetup.presetColorsList.setItemAnimator(null);
        binding.presetMoireeColorsSetup.addOnPropertyChangedCallback(expandedTransitionStarter);

        boolean presetColorsExpanded = preferences.getBoolean(KEY_PRESET_COLORS_EXPANDED, true);
        binding.presetMoireeColorsSetup.setExpanded(presetColorsExpanded);
    }

    private void initializeCustomColorsCard() {
        ObservableArrayList<PersistableMoireeColors> customColors = new ObservableArrayList<>();
        binding.customMoireeColorsSetup.setCustomColors(customColors);

        int recyclerViewColumns = getResources().getInteger(R.integer.color_list_view_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), recyclerViewColumns);
        layoutManager.setAutoMeasureEnabled(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.customMoireeColorsSetup.customColorsList.setLayoutManager(layoutManager);
        binding.customMoireeColorsSetup.customColorsList.setNestedScrollingEnabled(false);
        customColorsAdapter = new MoireeColorsAdapter(customColors, true);
        binding.customMoireeColorsSetup.customColorsList.setAdapter(customColorsAdapter);
        binding.customMoireeColorsSetup.customColorsList.setItemAnimator(null);
        binding.customMoireeColorsSetup.addOnPropertyChangedCallback(expandedTransitionStarter);

        boolean customColorsExpanded = preferences.getBoolean(KEY_CUSTOM_COLORS_EXPANDED, true);
        binding.customMoireeColorsSetup.setExpanded(customColorsExpanded);

        queryCustomMoireeColors();
    }

    private void queryCustomMoireeColors() {
        binding.customMoireeColorsSetup.setBusy(true);
        Querier<PersistableMoireeColors> querier = AppDatabase.getInstance(getContext()).persistableMoireeColorsDao();
        new DatabaseQuerierTask<>(querier).completed(new CallbackAsyncTask.Callback<List<PersistableMoireeColors>>() {
            @Override
            public void call(List<PersistableMoireeColors> persistableMoireeColors) {
                beginMenuBoundsTransition();
                customColorsAdapter.setMoireeColors(persistableMoireeColors);
                customColorsAdapter.notifyDataSetChanged();
                binding.customMoireeColorsSetup.setBusy(false);
            }
        }).execute();
    }

    void onDeleteCustomColors(PersistableMoireeColors persistableMoireeColors, ItemPersistableMoireeColorsBinding binding) {
        beginMenuBoundsTransition();
        binding.setBusy(true);
        new DatabaseDeleterTask<>(
                AppDatabase.getInstance(getContext()).persistableMoireeColorsDao()
        ).completed(deleteListener).execute(persistableMoireeColors);
    }

    void onApplyMoireeColors(PersistableMoireeColors persistableMoireeColors) {
        beginMoireeColorTransitionIfWanted();
        persistableMoireeColors.storeDataInMoireeColors(moireeColors);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_load_moiree_colors, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.reset_colors:
                resetColorsToBackup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        preferences.edit()
            .putBoolean(KEY_PRESET_COLORS_EXPANDED, binding.presetMoireeColorsSetup.getExpanded())
            .putBoolean(KEY_CUSTOM_COLORS_EXPANDED, binding.customMoireeColorsSetup.getExpanded())
            .apply();
    }


    private List<PersistableMoireeColors> getPresetMoireeColors() {
        ArrayList<PersistableMoireeColors> colors = new ArrayList<>();

        int[] foregroundColors = getColors(R.array.preset_foreground_colors);
        int[] backgroundColors = getColors(R.array.preset_background_colors);
        String[] colorNames = getContext().getResources().getStringArray(R.array.preset_color_names);

        for(int i = 0; i < foregroundColors.length; i++) {
            PersistableMoireeColors color = new PersistableMoireeColors();
            color.name = colorNames[i];
            color.foregroundColor = foregroundColors[i];
            color.backgroundColor = backgroundColors[i];
            colors.add(color);
        }

        return colors;
    }

    private int[] getColors(@ArrayRes int arrayResId) {
        TypedArray typedArray = getContext().getResources().obtainTypedArray(arrayResId);
        int[] colors = new int[typedArray.length()];
        for(int i = 0; i < colors.length; i++)
            colors[i] = typedArray.getColor(i, 0);
        typedArray.recycle();
        return colors;
    }

    private class MoireeColorsAdapter extends RecyclerView.Adapter<MoireeColorsAdapter.ViewHolder> {

        private final List<PersistableMoireeColors> colors;

        private final boolean deletable;


        MoireeColorsAdapter(List<PersistableMoireeColors> colors, boolean deletable) {
            this.colors = colors;
            this.deletable = deletable;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemPersistableMoireeColorsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_persistable_moiree_colors, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.binding.setPersistableMoireeColors(colors.get(position));
        }

        @Override
        public int getItemCount() {
            return colors.size();
        }

        void deleteMoireeColors(PersistableMoireeColors persistableMoireeColors) {
            int removedIndex = colors.indexOf(persistableMoireeColors);
            if(removedIndex >= 0){
                colors.remove(removedIndex);
                notifyItemRemoved(removedIndex);
            }
        }

        void setMoireeColors(List<PersistableMoireeColors> persistableMoireeColorsList) {
            colors.clear();
            colors.addAll(persistableMoireeColorsList);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPersistableMoireeColorsBinding binding;

            ViewHolder(ItemPersistableMoireeColorsBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.setDeletable(deletable);

                if(deletable)
                    binding.getRoot().findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onDeleteCustomColors(ViewHolder.this.binding.getPersistableMoireeColors(), ViewHolder.this.binding);
                        }
                    });

                binding.clickableRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onApplyMoireeColors(ViewHolder.this.binding.getPersistableMoireeColors());
                    }
                });
            }
        }
    }
}
