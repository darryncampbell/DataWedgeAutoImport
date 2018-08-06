package com.darryncampbell.datawedgeautoimport;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createTempFolder();
            }
            else
            {
                output("Please grant file permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        Button importButton = (Button) findViewById(R.id.btnImport);
        importButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  copy all files from the staging folder to the auto import folder
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                File stagingDirectory = new File(externalStorageDirectory.getPath(), "/datawedge_import");
                File[] filesToStage = stagingDirectory.listFiles();
                File outputDirectory = new File ("/enterprise/device/settings/datawedge/autoimport");
                if (!outputDirectory.exists())
                    outputDirectory.mkdirs();
                if (filesToStage.length == 0)
                    output("No files found in staging directory");
                for (int i = 0; i < filesToStage.length; i++)
                {
                    //  Write the file as .tmp to the autoimport directory
                    try {
                        InputStream in = new FileInputStream(filesToStage[i]);
                        File outputFile = new File(outputDirectory, filesToStage[i].getName() + ".tmp");
                        OutputStream out = new FileOutputStream(outputFile);
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;

                        // write the output file (You have now copied the file)
                        out.flush();
                        out.close();
                        out = null;

                        //  Rename the temp file
                        String outputFileName = outputFile.getAbsolutePath();
                        outputFileName = outputFileName.substring(0, outputFileName.length() - 4);
                        File fileToImport = new File(outputFileName);
                        outputFile.renameTo(fileToImport);
                        //set permission to the file to read, write and exec.
                        fileToImport.setExecutable(true, false);
                        fileToImport.setReadable(true, false);
                        fileToImport.setWritable(true, false);
                        output("File(s) copied to DW autoimport directory");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        output("File Not Found: " + e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        output("Exception: " + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            createTempFolder();
        }
    }

    private void createTempFolder()
    {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File stagingDirectory = new File(externalStorageDirectory.getPath(), "/datawedge_import");
        boolean success = stagingDirectory.mkdirs();
        TextView txtInstructions = (TextView)findViewById(R.id.instructions);
        txtInstructions.setText("Please copy all DataWedge profiles to the staging directory: adb push (file) " + stagingDirectory.getPath());
    }

    private void output(String output)
    {
        TextView txtOutput = (TextView)findViewById(R.id.txtOutput);
        txtOutput.setText(output);
    }
}
