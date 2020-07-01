package com.example.interceptevents_tvn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Random random = new Random();
    private ItemDataAdapter adapter;
    private List<ItemData> samples = new ArrayList<>();
    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 100;
    private static final String FILE_NAME_SAMPLES = "samples.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        FloatingActionButton fab = findViewById(R.id.fab);
        fillSamples();
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                permissionStatus();

            }
        });
        adapter = new ItemDataAdapter(this, null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showItemData(position);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view,
                                           int position,
                                           long id) {
                adapter.removeItem(position);
                readerDataFile();
                return true;
            }
        });
    }

    private void fillSamples() {
        samples.add(new ItemData(getDrawable(R.drawable.calendar),
                getString(R.string.title3),
                getString(R.string.subtitle2),
                random.nextBoolean()));
        samples.add(new ItemData(getDrawable(R.drawable.spinner),
                getString(R.string.title2),
                getString(R.string.subtitle2),
                random.nextBoolean()));
        samples.add(new ItemData(getDrawable(R.drawable.notes),
                getString(R.string.title1),
                getString(R.string.subtitle1),
                random.nextBoolean()));
    }

    private void showItemData(int position) {
        ItemData itemData = adapter.getItem(position);
        Toast.makeText(MainActivity.this,
                "Title: " + itemData.getTitle() + "\n" +
                        "Subtitle: " + itemData.getSubtitle() + "\n" +
                        "Checked: " + itemData.isChecked(),
                Toast.LENGTH_SHORT).show();
    }

    private void permissionStatus() {
        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            saveItemDataToFile();  // добавить метод сохранение строк в файл

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_STORAGE
            );
        }
    }

    public void readerDataFile() {
        if (isExternalStorageReadable()) {
            File sampleFile = new File(getApplicationContext().getExternalFilesDir(null),
                    FILE_NAME_SAMPLES);
            String[] text = new String[0];
            try {
                String line;
                FileReader fileReader = new FileReader(sampleFile);
                BufferedReader br = new BufferedReader(fileReader);
                while ((line = br.readLine()) != null) {
                    text = line.split(";");
                }
                if (adapter.getCount() != text.length) {
                    boolean deleteFile = sampleFile.delete();
                    if(deleteFile) {
                        Toast.makeText(this,
                                R.string.delete_file,
                                Toast.LENGTH_SHORT).show();
                    }
                    sampleFile = new File(getApplicationContext().getExternalFilesDir(null),
                            FILE_NAME_SAMPLES);
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(sampleFile, true);

                        for (int i = 0; i < adapter.getCount(); i++) {
                            fileWriter.append(adapter.getItem(i).getTitle()).append(" ").
                                    append(adapter.getItem(i).getSubtitle()).
                                    append(" - ").append(getString(R.string.author)).append(";");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fileWriter != null) {
                                fileWriter.close();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveItemDataToFile() {
        if (isExternalStorageWritable()) {
            File sampleFile = new File(getApplicationContext().getExternalFilesDir(null),
                    FILE_NAME_SAMPLES);
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(sampleFile, true);
                int randomValue = random.nextInt(samples.size());
                adapter.addItem(samples.get(randomValue));
                fileWriter.append(samples.get(randomValue).getTitle()).append(" ").
                        append(samples.get(randomValue).getSubtitle()).
                        append(" - ").append(getString(R.string.author)).append(";");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_WRITE_STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveItemDataToFile();
            } else {
                Toast.makeText(this,
                        R.string.not_run_request,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
