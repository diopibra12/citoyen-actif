package ca.uqtr.citoyenactif.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.stream.Collectors;

import ca.uqtr.citoyenactif.ReportAdapter;
import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.ReportEditor;
import ca.uqtr.citoyenactif.databinding.ReportsListBinding;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;
import ca.uqtr.citoyenactif.fragment.model.ReportViewModel;


public class ReportsList extends Fragment {
    private ReportsListBinding binding;
    private ReportAdapter adapterClass;
    private RecyApp app;
    private ReportViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReportsListBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.reportList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapterClass = new ReportAdapter(requireContext());
        binding.reportList.setAdapter(adapterClass);
        app = (RecyApp) requireActivity().getApplicationContext();

        adapterClass.setOnclick(reportListe -> {
            app.setCurrent(reportListe);
            startActivity(new Intent(requireContext(), ReportEditor.class));
        });

        adapterClass.setOnLongclick(report -> {
            this.viewModel.delete(report);
            Toast.makeText(requireContext(), "Vous avez bien supprimé le report", Toast.LENGTH_SHORT).show();
        });

        binding.addButton.setOnClickListener(v -> {
            app.setCurrent(null);
            startActivity(new Intent(requireContext(), ReportEditor.class));
        });

        this.viewModel.getReports().observe(getViewLifecycleOwner(), reports -> this.adapterClass.updateDataset(reports));
    }

    @Override
    public void onResume() {
        super.onResume();

        this.viewModel.updateReports();

        adapterClass.setOnclick(des -> {
            app.setCurrent(des);
            startActivity(new Intent(requireContext(), ReportEditor.class));
        });
    }



}

