package com.posttracking;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.posttracking.Boundaries.InvoiceDAO;
import com.posttracking.Boundaries.LocalConfig;
import com.posttracking.Boundaries.PackageDAO;
import com.posttracking.Entities.Invoice;
import com.posttracking.Entities.Quotation;
import com.posttracking.api.PostTrackingAPI;
import com.posttracking.api.RetrofitClient;
import com.posttracking.api.models.Package;
import com.posttracking.api.models.Path;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewQuotationActivity extends AppCompatActivity {

     InvoiceDAO iDAO = new InvoiceDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);
        final ViewQuotationActivity _this = this;
        final RadioGroup rg = findViewById(R.id.rdGroup);
        final TextView tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText("Working...");
        final Button btnGenerateInvoice = findViewById(R.id.btnGenerate);
        btnGenerateInvoice.setVisibility(View.GONE);
        final List<Path> paths = new ArrayList<Path>();
        final List<Invoice> invoices = new ArrayList<Invoice>();


        final int package_id = getIntent().getIntExtra("package_id", 0);
        if(package_id==0) {
            //Show Error
            finish();
        }

        PackageDAO pDAO = new PackageDAO(this);
        final Package p = pDAO.getPackage(package_id);

        PostTrackingAPI api = RetrofitClient.getRetrofitInstance().create(PostTrackingAPI.class);
        Call<List<Path>> call = api.getQuotation(String.valueOf(p.getOrigin().getId()),
                String.valueOf(p.getDestination().getId()),String.valueOf(p.getWeight()),
                String.valueOf(p.getVolume()));

        call.enqueue(new Callback<List<Path>>() {
            //ArrayAdapter adap;
            @Override
            public void onResponse(Call<List<Path>> call, Response<List<Path>> response) {
                if(response.body().size()==0) {
                    tvMessage.setText("Unable to find a Quotation");
                } else {
                    for(int i=0; i < response.body().size(); ++i) {
                        paths.add(response.body().get(i));
                        // Generating Invoice
                        Invoice inv = new Invoice();
                        inv.setDeliveryTime(response.body().get(i).getNOfDays());
                        inv.setCust_id(LocalConfig.customerId);
                        inv.setPack_id(package_id);
                        inv.generateAmount(p.getWeight(), p.getVolume());
                        //Generating Radio
                        RadioButton rdbtn = new RadioButton(_this);
                        rdbtn.setId(View.generateViewId());
                        rdbtn.setTextColor(Color.BLACK);
                        rdbtn.setTextSize(24);
                        rdbtn.setText(response.body().get(i).toString());
                        rdbtn.setId(i);
                        rg.addView(rdbtn);
                    }
                btnGenerateInvoice.setVisibility(View.VISIBLE);
                tvMessage.setText("");
                tvMessage.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Path>> call, Throwable t) {
                tvMessage.setText("Sorry, Unable to connect to the API");
            }
        });

        btnGenerateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
