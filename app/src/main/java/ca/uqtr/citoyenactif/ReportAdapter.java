package ca.uqtr.citoyenactif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ca.uqtr.citoyenactif.databinding.ReportBinding;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;
import ca.uqtr.citoyenactif.db.repository.CitoyenRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private static List<ReportEntity> data;
    private static Consumer<ReportEntity> onClickRunnable;
    private static Consumer<ReportEntity> onLongClickRunnable;
    private final Context context;
    private ReportBinding binding;


    public ReportAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ReportViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.setReport(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnclick(Consumer<ReportEntity> clickListener) {
        onClickRunnable = clickListener;
    }

    public void setOnLongclick(Consumer<ReportEntity> clickListener) {
        onLongClickRunnable = clickListener;
    }

    public void updateDataset(List<ReportEntity> reports) {
        data.clear();
        data.addAll(reports);
        notifyDataSetChanged();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        private final ReportBinding binder;
        private ReportEntity p;

        public ReportViewHolder(@NonNull ReportBinding binder) {
            super(binder.getRoot());
            this.binder = binder;

            binder.getRoot().setOnClickListener(v -> {
                if (onLongClickRunnable != null) {
                    onLongClickRunnable.accept(p);

                    data.remove(p);
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            binder.getRoot().setOnClickListener(v -> {
                if (onClickRunnable != null) {
                    onClickRunnable.accept(p);
                }
            });
        }

        public void setReport(ReportEntity p) {
            this.p = p;
            binder.textReportTitle.setText(p.getTitre());
            binder.textReportDescription.setText(p.getDescription());

            CitoyenRepository repo = new CitoyenRepository(context);

            repo.getCitoyen(p.getCitoyenId(), citoyen -> {
                if (citoyen != null) {
                    binder.textFullName.setText(citoyen.getPrenom() + " " + citoyen.getNom());
                }
            });

            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                Glide.with(binder.imageViewReport.getContext())
                        .load(new File(p.getImagePath()))
                        .into(binder.imageViewReport);
            } else {
                //Définir une image par défaut si aucun chemin n'est disponible
                binder.imageViewReport.setImageResource(R.drawable.baseline_photo_24);
            }
        }
    }

}
