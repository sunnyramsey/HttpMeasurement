package com.zjulist.httpmeasurement;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button button1,button2,button3,button4,button5;
    ListView urlList;
    TextView infoText;
    EditText countText;
    List<UrlState> srcDataList = new ArrayList<UrlState>();
    UrlListAdapter myAdapter;
    OkHttpClient okHttpClient;
    String srcUrlResponse;
    int totalUrl;
    int successUrl;
    int failedUrl;
    int defaultTotalNumber;
    AsynTaskManager asynTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);
        button5 = (Button)findViewById(R.id.button5);
        urlList = (ListView)findViewById(R.id.listView);
        infoText = (TextView)findViewById(R.id.textView);
        countText = (EditText)findViewById(R.id.editText);
        okHttpClient = new OkHttpClient(this);




        myAdapter = new UrlListAdapter(this,srcDataList);
        asynTaskManager = AsynTaskManager.getInstance();
        urlList.setAdapter(myAdapter);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!countText.getText().equals(""))
                        defaultTotalNumber = Integer.parseInt(countText.getText().toString());
                    else
                        defaultTotalNumber = 0;
                } catch (Exception e) {
                    Log.e("Parse", "Get wrong count!");
                }
                GetUrlThread getUrlThread = new GetUrlThread();
                getUrlThread.start();
//                testDataSrc();
//                myAdapter.notifyDataSetChanged();
//                totalUrl = 3;
//                refreshInfo();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successUrl = 0;
                failedUrl = 0;
                refreshInfo();
                for (int i = 0; i < srcDataList.size(); i++)
                {
                   //asynTaskManager.postTask(new HttpTask("http://10.214.148.120:8080",1));
                    asynTaskManager.postTask(new HttpTask(srcDataList.get(i).getUrl(),i));
                }
                //asynTaskManager.postTask(new HttpTask("http://10.214.148.120:8080",1));

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeToSD();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("DATABASE","SAVE FAILED");
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        if (!countText.getText().equals(""))
                            defaultTotalNumber = Integer.parseInt(countText.getText().toString());
                        else
                            defaultTotalNumber = 0;
                    } catch (Exception e) {
                        Log.e("Parse", "Get wrong count!");
                    }
                    GetUrlForChinaThread getUrlThread = new GetUrlForChinaThread();
                    getUrlThread.start();
                }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshInfo();
                String toastString  ="";
                for(int i =0 ;i<srcDataList.size();i++)
                {
                    if(!srcDataList.get(i).isFinished())
                        toastString +=srcDataList.get(i).getUrl()+";";
                }
                Toast.makeText(getApplicationContext(),toastString,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void testDataSrc()
    {
        UrlState a = new UrlState("abkjl");
        UrlState b = new UrlState("123545");
        UrlState c = new UrlState("234ydsfhsj");
        srcDataList.add(a);
        srcDataList.add(b);
        srcDataList.add(c);

    }

    public class GetUrlForChinaThread extends  Thread{
        @Override
        public void run() {
            String url = "http://garuda.cs.northwestern.edu:3000/fetch-china-url";
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    srcDataList.clear();
                    srcUrlResponse = response.body().string();
                    parseUrlList(srcUrlResponse);
                    successUrl = 0;
                    failedUrl = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            refreshInfo();
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }else
                {
                    Toast.makeText(getApplicationContext(),"Get URL failed",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetUrlThread extends  Thread{
        @Override
        public void run() {
            String url = "http://garuda.cs.northwestern.edu:3000/fetch-url";
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    srcDataList.clear();
                    srcUrlResponse = response.body().string();
                    parseUrlList(srcUrlResponse);
                    successUrl = 0;
                    failedUrl = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            refreshInfo();
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }else
                {
                    Toast.makeText(getApplicationContext(),"Get URL failed",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseUrlList(String src)
    {
        src = src.replace("\"","");
        src = src.replace("[","");
        src = src.replace("]","");
        String[] srcList =src.split(",");
        if(defaultTotalNumber == 0 ) {
            totalUrl = srcList.length;
        }else {
            totalUrl = defaultTotalNumber;
        }
        for(int i = 0;i<totalUrl;i++)
        {
            UrlState urlState = new UrlState(srcList[i]);
            srcDataList.add(urlState);
        }

    }

    private void writeToSD() throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        String DB_PATH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DB_PATH = getApplicationContext().getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
        }
        else {
            DB_PATH = getApplicationContext().getFilesDir().getPath() + getApplicationContext().getPackageName() + "/databases/";
        }
        Log.i("DB", DB_PATH);

        if (sd.canWrite()) {
            String currentDBPath = "NetProphet.db";
            String backupDBPath = "NetProphet_backup.db";
            File currentDB = new File(DB_PATH, currentDBPath);
            if(!currentDB.exists())
            {
                Log.i("DB", "No database");
                return;
            }
            File backupDB = new File(sd, backupDBPath);
            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

    private class HttpTask implements Runnable{

        private int number;
        private String url;

        public HttpTask(String url,int no)
        {
            this.url = url;
            this.number = no;
        }

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Request-Info",url+" is failed");
                    failedUrl++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshInfo();
                            refreshData(number, true);
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        Log.i("Request-Info", url + " is successful");
                        successUrl++;
                        response.body().string();
                        response.body().close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshInfo();
                                refreshData(number, true);
                            }
                        });
//                        System.out.println(response.code());
//                        System.out.println(response.body().string());
                    }else{
                        Log.e("Request-Info-R",url+" is failed");
                        failedUrl++;
                        response.body().string();
                        response.body().close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshInfo();
                                refreshData(number, true);
                            }
                        });
                    }
                }
            });
        }
    }

    /*
    Should be called in UI thread.
    */
    private void refreshInfo()
    {
        infoText.setText("Total:"+totalUrl+";Successful:"+successUrl+";Failed:"+failedUrl+";");
    }

    /*
    Should be called in UI thread.
    */
    private void refreshData(int number,boolean state)
    {
        srcDataList.get(number).setIsFinished(state);
        myAdapter.notifyDataSetChanged();
    }

}
