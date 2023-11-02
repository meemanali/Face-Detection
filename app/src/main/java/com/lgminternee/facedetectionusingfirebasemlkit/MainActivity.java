package com.lgminternee.facedetectionusingfirebasemlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.lgminternee.facedetectionusingfirebasemlkit.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Dialog dialog;
    private final int reqCode_getImage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // set background of dialog transparent
        dialog.getWindow().getAttributes().windowAnimations = R.style.myDialog;
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        binding.btnGet.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, reqCode_getImage);
        });

        binding.image.setOnClickListener(view -> dialog.show());

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> dialog.dismiss());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == reqCode_getImage && resultCode == RESULT_OK && data != null) {

            binding.image.setImageURI(data.getData());

            binding.btnGet.setText("Get New Image");

            TextView txtTitle = dialog.findViewById(R.id.txtTitle);
            TextView txtMsg = dialog.findViewById(R.id.txtMsg);

            try {

                InputImage inputImage = InputImage.fromFilePath(this, data.getData());

                FaceDetectorOptions highAccuracyOpts =
                        new FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                .setMinFaceSize(0.15f)
                                .enableTracking()
                                .build();

                FaceDetector faceDetector = FaceDetection.getClient(highAccuracyOpts); // we can also use default faceDetection client by not giving any argument here in constructor

                Task<List<Face>> result = faceDetector.process(inputImage)
                        .addOnSuccessListener(
                                faces -> {

                                    String message = " ";
                                    int count = 1;

                                    for (Face face : faces) {

//                                        Rect bounds = face.getBoundingBox();
//                                        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
//                                        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

//                                        message = message.concat("\nbound = " + bounds.left + " " + bounds.right + " " + bounds);
//                                        message = message.concat("\nrotY = " + rotY);
//                                        message = message.concat("\nrotZ = " + rotZ);

                                        // If landmark detection was enabled (mouth, ears, eyes, cheeks and nose available):
//                                        FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
//                                        if (leftEar != null) {
//                                            PointF leftEarPos = leftEar.getPosition();                                        
//                                            message = message.concat("\nleftEarPos = " + leftEarPos);
//                                        }

                                        // If contour detection was enabled:
//                                        List<PointF> leftEyeContour = face.getContour(FaceContour.LEFT_EYE).getPoints();
//                                        List<PointF> upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();

//                                        message = message.concat("\nleftEyeContour = " + leftEyeContour);
//                                        message = message.concat("\nupperLipBottomContour = " + upperLipBottomContour);

                                        // If classification was enabled:
//                                        if (face.getSmilingProbability() != null) {
//                                            float smileProb = face.getSmilingProbability();
//
////                                            message = message.concat("\nsmileProb = " + smileProb);
//                                        }
//                                        if (face.getRightEyeOpenProbability() != null) {
//                                            float rightEyeOpenProb = face.getRightEyeOpenProbability();
//
////                                            message = message.concat("\nrightEyeOpenProb = " + rightEyeOpenProb);
//                                        }

                                        // If face tracking was enabled:
//                                        if (face.getTrackingId() != null) {
//                                            int id = face.getTrackingId();
//
////                                            message = message.concat("\nid = " + id);
//                                        }

                                        // -------------------

                                        message = message.concat("\nFace Number: " + count);
                                        message = message.concat("\nSmile: " + face.getSmilingProbability() * 100);
                                        message = message.concat("\nLeft Eye: " + face.getLeftEyeOpenProbability());
                                        message = message.concat("\nRight Eye: " + face.getRightEyeOpenProbability() + "\n\n");

                                        count++;
                                    }

                                    txtTitle.setText(faces.size() + " Face Detected");
                                    txtMsg.setText(message);
                                    dialog.show();

                                })
                        .addOnFailureListener(
                                e -> {
                                    txtTitle.setText("Error");
                                    txtMsg.setText(e.getLocalizedMessage());
                                    dialog.show();
                                });

            } catch (IOException e) {
                dialog.setTitle("Error");
                txtMsg.setText(e.getLocalizedMessage());
                dialog.show();
            }
        }

    }
}
