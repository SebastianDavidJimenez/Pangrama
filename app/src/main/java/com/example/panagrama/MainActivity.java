package com.example.panagrama;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.*;
public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectFileButton = findViewById(R.id.button_select_file);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(inputStream)));
        String line;
        List<String> results = new ArrayList<>();
        while((line = reader.readLine()) != null){
            String isPangram = isPangram(line) ? "SI" : "NO";
            int charCount = countCharacters(line);
            results.add(isPangram + " " + charCount);
        }

        reader.close();
        writeResultsToFile(results);
    }

    private void writeResultsToFile(List<String> results) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "SOLUCION.TXT");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
            for (String result : results) {
                writer.println(result);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isPangram(String str) {
        if (str != null) {
            Set<Character> alphabetSet = new HashSet<>();
            for (char c : str.toLowerCase().trim().toCharArray()) {
                if (Character.isLetter(c)) {
                    alphabetSet.add(Character.toLowerCase(c));
                }
            }
            return alphabetSet.size() == 26;
        } else {
            Toast.makeText(this, "Es nulo: "+str+" .",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static int countCharacters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            }
        }
        return count;
    }
}
