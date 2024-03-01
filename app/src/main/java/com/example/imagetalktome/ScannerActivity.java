package com.example.imagetalktome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScannerActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView detectedTxt;
    private Button takeSnap, detectTxt;
    private Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        imageView = findViewById(R.id.imageView);
        detectedTxt = findViewById(R.id.detectedText);
        takeSnap = findViewById(R.id.takeSnap);
        detectTxt = findViewById(R.id.detectText);

        detectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetectText();
            }
        });
        
        takeSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission()){
                    captureImage();
                }else{
                    requestPermission();
                }
            }
        });

    }

    private boolean CheckPermission() {
        int camPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA_SERVICE);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA
        }, PERMISSION_CODE);
    }

    private void captureImage() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getPackageManager()) != null) startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
        detectedTxt.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0){
            boolean camPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(camPermission){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                captureImage();
            }
            else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void DetectText() {
        InputImage img = InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
        );
        Task<Text> result = recognizer.process(img).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block : text.getTextBlocks()){
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();

                    for(Text.Line line: block.getLines()){
                        String lineText = line.getText();
                        Point[] lineCornerPoints = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();

                        for(Text.Element e: line.getElements()){
                            String elementTxt = e.getText();
                            result.append(elementTxt+" ");
                        }
                        result.append("\n");
                    }
                }
                detectedTxt.setText(result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this,
                        "Failed to Detect Text From Image...", Toast.LENGTH_SHORT).show();
            }
        });

    }
}