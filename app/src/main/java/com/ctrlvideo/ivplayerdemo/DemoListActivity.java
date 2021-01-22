package com.ctrlvideo.ivplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DemoListActivity extends FragmentActivity {

    private RecyclerView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deme_list);

        listView = findViewById(R.id.recyclerview);

        listView.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    public void demo(View view) {
        Intent intent= new Intent(this, ExoplayerActivity.class);
        intent.putExtra("pid", "5166003853478754");
//        intent.putExtra("pid", "5139070546388488");

//        Intent intent= new Intent(this, HandlerTestActivity.class);
        startActivity(intent);
    }

    public void list(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    private void getData() {


        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://apiivetest.ctrlvideo.com/video/ajax/get_baidu_videolist/")
                .build();
        Call call = okHttpClient.newCall(request);
//
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String str = responseBody.string();
                    if (str != null) {

                        DemoResult demoResult = new Gson().fromJson(str, DemoResult.class);
                        if ("success".equals(demoResult.status)) {
                            final List<DemoResult.Project> projects = demoResult.result;
                            if (projects != null) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initList(projects);
                                    }
                                });


                            }
                        }
                    }
                }
            }
        });
    }

    private void initList(final List<DemoResult.Project> projects) {


        listView.setAdapter(new RecyclerView.Adapter<Holder>() {
            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                return new Holder(LayoutInflater.from(DemoListActivity.this).inflate(R.layout.item_demo, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull Holder holder, final int position) {
                holder.textView.setText(projects.get(position).video_title);

                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(DemoListActivity.this, MainActivity.class);
                        intent.putExtra("pid", projects.get(position).pid);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public int getItemCount() {
                return projects.size();
            }
        });


    }

    public class Holder extends RecyclerView.ViewHolder {

        public TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
