package com.example.rostercreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class table extends AppCompatActivity {
    String cast[]={"SC","ST","OBC","UR"};
int val=0;
double SC=0,ST=0,OBC=0,UR=0;
int scc=1,stc=1,obcc=1;
TextView obc,sc,st,ur;
ListView listView;
//String path;
    ArrayList<String> table=new ArrayList<String>();
//TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        SC=getIntent().getIntExtra("sc",0);
        ST=getIntent().getIntExtra("st",0);
        OBC=getIntent().getIntExtra("obc",0);
        UR=getIntent().getIntExtra("ur",0);
        Log.d("ggdf",String.valueOf(SC));
        scc=(int)SC+1;
        stc=(int)ST+1;
        obcc=(int)OBC+1;
        obc=(TextView)findViewById(R.id.textView4);
        sc=(TextView)findViewById(R.id.textView6);
        st=(TextView)findViewById(R.id.textView5);
        ur=(TextView)findViewById(R.id.textView3);
        val = (int) (getIntent().getIntExtra("total", 0) - SC-ST-OBC-UR);
//        path=getIntent().getStringExtra("path");
        listView=(ListView) findViewById(R.id.listView);
        for(int i=0;i<val;i++){
            SC+=0.15;
            ST+=0.075;
            OBC+=0.27;
            if(SC>=scc){
                table.add("SC");
                scc++;
            }
            else if(ST>=stc){
                table.add("ST");
                stc++;
            }
            else if(OBC>=obcc){
                table.add("OBC");
                obcc++;
            }
            else table.add("UR");
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,table);
            listView.setAdapter(arrayAdapter);
            obc.setText("obc="+Integer.toString(obcc-1-getIntent().getIntExtra("obc",0)));
            sc.setText("sc="+Integer.toString(scc-1-getIntent().getIntExtra("sc",0)));
            st.setText("st="+Integer.toString(stc-1-getIntent().getIntExtra("st",0)));
            ur.setText("ur="+Integer.toString(val-obcc-scc-stc+3+getIntent().getIntExtra("obc",0)+getIntent().getIntExtra("sc",0)+getIntent().getIntExtra("st",0)));
        }

//        textView=(TextView) findViewById(R.id.textView);
//        textView.setText(Integer.toString(val));
    }
}