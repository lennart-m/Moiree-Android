package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
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
import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.ItemPersistableMoireeTransformationBinding;
import de.lennartmeinhardt.android.moiree.databinding.MenuLoadTransformationBinding;
import de.lennartmeinhardt.android.moiree.util.CallbackAsyncTask;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseDeleterTask;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseQuerierTask;
import de.lennartmeinhardt.android.moiree.util.db.Querier;
import de.lennartmeinhardt.android.moiree.util.db.room.AppDatabase;
import de.lennartmeinhardt.android.moiree.util.db.room.PersistableMoireeTransformation;

public class LoadTransformationMenu extends BaseTransformationSetupFragment {

    private static final String KEY_CUSTOM_TRANSFORMATIONS_EXPANDED = "loadMoireeTransformationSetup:customTransformationsExpanded";

    private MenuLoadTransformationBinding binding;
    private MoireeTransformationAdapter transformationsAdapter;

    private final CallbackAsyncTask.Callback<List<PersistableMoireeTransformation>> deleteListener = new CallbackAsyncTask.Callback<List<PersistableMoireeTransformation>>() {
        @Override
        public void call(List<PersistableMoireeTransformation> deletedTransformations) {
            beginMenuBoundsTransition();

            for(PersistableMoireeTransformation deletedTransformation: deletedTransformations) {
                transformationsAdapter.deleteMoireeTransformation(deletedTransformation);
            }
            updateNoDataState();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.menu_load_transformation, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        MoireeTransformation moireeTransformationForPreview = new MoireeTransformation();
        moireeTransformationForPreview.setToIdentity();
        binding.setMoireeTransformation(moireeTransformationForPreview);

        initCardExpandables();

        RecyclerView storedTransformationsList = binding.storedTransformationsListCard.expandableViewContent;
        storedTransformationsList.setNestedScrollingEnabled(false);
        storedTransformationsList.setItemAnimator(null);

        int recyclerViewColumns = getResources().getInteger(R.integer.transformation_list_view_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), recyclerViewColumns);
        layoutManager.setAutoMeasureEnabled(true); // TODO aufraeumen
        storedTransformationsList.setLayoutManager(layoutManager);
        transformationsAdapter = new MoireeTransformationAdapter(new ArrayList<PersistableMoireeTransformation>(), true);
        storedTransformationsList.setAdapter(transformationsAdapter);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        boolean cardExpanded = preferences.getBoolean(KEY_CUSTOM_TRANSFORMATIONS_EXPANDED, true);
        binding.storedTransformationsListCard.setExpanded(cardExpanded);

        binding.setNoData(true);

        Querier<PersistableMoireeTransformation> querier = AppDatabase.getInstance(getContext()).persistableMoireeTransformationDao();
        new DatabaseQuerierTask<>(querier)
                .completed(new CallbackAsyncTask.Callback<List<PersistableMoireeTransformation>>() {
                    @Override
                    public void call(List<PersistableMoireeTransformation> persistableMoireeTransformations) {
                        beginMenuBoundsTransition();
                        transformationsAdapter.setMoireeTransformations(persistableMoireeTransformations);
                        transformationsAdapter.notifyDataSetChanged();
                        binding.setBusy(false);
                        updateNoDataState();
                    }
                })
                .execute();
    }

    private void initCardExpandables() {
        Observable.OnPropertyChangedCallback transitionStarter = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                // start the transition only if the expanded property has changed
                if (i == BR.expanded)
                    beginMenuTransition(createMenuBoundsAndHeaderIndicatorTransition());
            }
        };
        binding.storedTransformationsListCard.addOnPropertyChangedCallback(transitionStarter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_load_transformation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.revert_transformation:
                resetTransformationToBackup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateNoDataState() {
        boolean isNoData = transformationsAdapter.getItemCount() == 0;
        binding.setNoData(isNoData);
    }

    private void onApplyMoireeTransformation(PersistableMoireeTransformation persistableMoireeTransformation) {
        beginMoireeTransformationTransitionIfWanted();
        persistableMoireeTransformation.storeDataInTransformation(moireeTransformation);
    }

    private void onDeleteCustomTransformation(PersistableMoireeTransformation persistableMoireeTransformation, ItemPersistableMoireeTransformationBinding binding) {
        beginMenuBoundsTransition();
        binding.setBusy(true);
        new DatabaseDeleterTask<>(
                AppDatabase.getInstance(getContext()).persistableMoireeTransformationDao()
        ).completed(deleteListener).execute(persistableMoireeTransformation);
    }

    @Override
    public void onPause() {
        super.onPause();

        preferences.edit()
                .putBoolean(KEY_CUSTOM_TRANSFORMATIONS_EXPANDED, binding.storedTransformationsListCard.getExpanded())
                .apply();
    }


    private class MoireeTransformationAdapter extends RecyclerView.Adapter<MoireeTransformationAdapter.ViewHolder> {

        private final List<PersistableMoireeTransformation> transformations;

        private final boolean deletable;


        MoireeTransformationAdapter(List<PersistableMoireeTransformation> transformations, boolean deletable) {
            this.transformations = transformations;
            this.deletable = deletable;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemPersistableMoireeTransformationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_persistable_moiree_transformation, parent, false);
            binding.setMoireeTransformation(new MoireeTransformation());
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.persistableMoireeTransformation = transformations.get(position);
            holder.persistableMoireeTransformation.storeDataInTransformation(holder.binding.getMoireeTransformation());
            holder.binding.setTransformationName(holder.persistableMoireeTransformation.transformationName);
        }

        @Override
        public int getItemCount() {
            return transformations.size();
        }

        void deleteMoireeTransformation(PersistableMoireeTransformation persistableMoireeTransformations) {
            int removedIndex = transformations.indexOf(persistableMoireeTransformations);
            if(removedIndex >= 0){
                transformations.remove(removedIndex);
                notifyItemRemoved(removedIndex);
            }
        }

        void setMoireeTransformations(List<PersistableMoireeTransformation> persistableMoireeTransformationsList) {
            transformations.clear();
            transformations.addAll(persistableMoireeTransformationsList);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPersistableMoireeTransformationBinding binding;
            private PersistableMoireeTransformation persistableMoireeTransformation;

            ViewHolder(ItemPersistableMoireeTransformationBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.setDeletable(deletable);

                if(deletable)
                    binding.getRoot().findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onDeleteCustomTransformation(persistableMoireeTransformation, ViewHolder.this.binding);
                        }
                    });

                binding.clickableRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onApplyMoireeTransformation(persistableMoireeTransformation);
                    }
                });
            }
        }
    }
}
