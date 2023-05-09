package com.example.rostercreator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    File file;

    Button btnUpDirectory,btnSDCard,submit;
    EditText total;
    ArrayList<String>pathHistory;
    String lastDirectory;
    int count=0,OBC=0,UR=0,SC=0,ST=0;
   // ArrayList<XYValue>uploadData;
    ListView lvInternalStorage;
    public void back(View v){
        if(count == 0){
            Log.d(TAG, "btnUpDirectory: You have reached the highest level directory.");
        }else{
            pathHistory.remove(count);
            count--;
            checkInternalStorage();
            Log.d(TAG, "btnUpDirectory: " + pathHistory.get(count));
        }
    }
    public void memory(View V){
        count = 0;
        pathHistory = new ArrayList<String>();
        pathHistory.add(count,System.getenv("EXTERNAL_STORAGE"));
        Log.d(TAG, "btnSDCard: 93 " + pathHistory.get(count));
        checkInternalStorage();
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvInternalStorage=(ListView) findViewById(R.id.list);
        btnUpDirectory=(Button) findViewById(R.id.btnUp);
        btnSDCard=(Button) findViewById(R.id.btnViewSD);
        submit=(Button) findViewById(R.id.Submit);
        total=(EditText)findViewById(R.id.editTextNumber2) ;
       // requestPermission();
        // checkFilePermissions();
         lvInternalStorage.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            lastDirectory = pathHistory.get(count);

            if(lastDirectory.equals(adapterView.getItemAtPosition(i))){
                Log.d(TAG, "lvInternalStorage: Selected a file for upload: " + lastDirectory);

                //Execute method for reading the excel data.
                read Runnable= new read(lastDirectory);
                new Thread(Runnable).start();
               // readExcelData(lastDirectory);

            }else
            {
                count++;
                pathHistory.add(count,(String) adapterView.getItemAtPosition(i));
                checkInternalStorage();
                Log.d(TAG, "lvInternalStorage: " + pathHistory.get(count));
            }
        });
    }
    private void checkInternalStorage() {
        Log.d(TAG, "checkInternalStorage: Started.");
        try{
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                toastMessage("No SD card found.");
            }
            else{
                // Locate the image folder in your SD Car;d
                file = new File(pathHistory.get(count));
                Log.d(TAG, "checkInternalStorage: directory path: " + pathHistory.get(count));
            }

            listFile = file.listFiles();

            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];

            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }

            for (int i = 0; i < listFile.length; i++)
            {
                Log.d("Files", "FileName:" + listFile[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilePathStrings);
            lvInternalStorage.setAdapter(adapter);

        }catch(NullPointerException e){
            Log.e(TAG, "checkInternalStorage: NULLPOINTEREXCEPTION " + e.getMessage() );
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                someActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                someActivityResultLauncher.launch(intent);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},2296);
        }
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                // perform action when allow permission success
                            } else {
                                toastMessage( "Allow permission for storage access!");
                            }
                        }
                    }
                }
            });
    private void checkFilePermissions() {

        if(SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            permissionCheck += ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            permissionCheck +=ContextCompat.checkSelfPermission(this, MANAGE_EXTERNAL_STORAGE);
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 2296); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    class read implements Runnable{
        String filePath;
        read(String filePath){
            this.filePath=filePath;
        }
        @Override
        public void run() {


//            private void readExcelData(String filePath){
                Log.d(TAG, "readExcelData: Reading Excel File.");

                //decarle input file
                File inputFile = new File(filePath);

                try {
                    InputStream inputStream = new FileInputStream(inputFile);
                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = sheet.iterator();
                    //int rowsCount = sheet.getPhysicalNumberOfRows();
                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    //StringBuilder sb = new StringBuilder();

                    //outter loop, loops through rows
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        //For each row, iterate through each columns
                        Iterator < Cell > cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {

                            Cell cell = cellIterator.next();
                            String value=cell.getStringCellValue();
//                            if (cell.getRichStringCellValue().equals("obc")) {
//                                OBC++;
//                                Log.d("obc------", String.valueOf(OBC));
//                            }
                            if(value.equals("obc"))OBC+=1;
                            else if(value.equals("sc"))SC+=1;
                            else if(value.equals("st"))ST+=1;
                            else if(value.equals("gen"))UR+=1;

                           Log.d("value=----",value);
                        }
                    }
                    Log.d("obc------333", String.valueOf(OBC));
                    Log.d("obc------333", String.valueOf(SC));
                    Log.d("obc------333", String.valueOf(ST));
                    Log.d("obc------333", String.valueOf(UR));

                } catch (FileNotFoundException e) {
                    Log.e(TAG, "readExcelData: FileNotFoundException. " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "readExcelData: Error reading inputstream. " + e.getMessage());
                }
        }
    }
    public void submission(View v){
        Intent intent = new Intent(this,table.class);
        Bundle bundle= new Bundle();
        int n=Integer.valueOf(String.valueOf(total.getText()));
        bundle.putInt("total",n);
        bundle.putInt("obc",OBC);
        bundle.putInt("sc",SC);
        bundle.putInt("st",ST);
        bundle.putInt("ur",UR);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}