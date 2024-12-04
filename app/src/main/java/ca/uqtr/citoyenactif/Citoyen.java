package ca.uqtr.citoyenactif;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import ca.uqtr.citoyenactif.databinding.ActivityCitoyenBinding;
import ca.uqtr.citoyenactif.fragment.Profil;
import ca.uqtr.citoyenactif.fragment.ReportsList;
import ca.uqtr.citoyenactif.fragment.ReportsMap;

public class Citoyen extends AppCompatActivity {
    private ActivityCitoyenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citoyen);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citoyen);
        getSupportFragmentManager().beginTransaction().replace(R.id.contenu, new ReportsList()).commit();
        binding.navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.listes) {
                getSupportFragmentManager().beginTransaction().replace(R.id.contenu, new ReportsList()).commit();
                return true;
            } else if (item.getItemId() == R.id.reports) {

                getSupportFragmentManager().beginTransaction().replace(R.id.contenu, new ReportsMap()).commit();
                return true;
            } else if (item.getItemId() == R.id.profil) {
                getSupportFragmentManager().beginTransaction().replace(R.id.contenu, new Profil()).commit();

            }
            return false;
        });
    }
}