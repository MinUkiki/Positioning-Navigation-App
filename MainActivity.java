package com.example.wepcrawling;

import static java.lang.Float.parseFloat;
import static java.lang.String.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wepcrawling.ml.First;
import com.example.wepcrawling.ml.Mobilenet2;
import com.example.wepcrawling.ml.Mobilenet2311201;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String[] classes = {"좌측 입출구1",
            "좌측 입출구2",
            "좌측 데스크1",
            "좌측 데스크2",
            "좌측 휴식처",
            "여자화장실",
            "엘리베이터",
            "리치 회의실",
            "에이다 회의실",
            "산악협력 회의실",
            "스톨먼 작업실",
            "코딩 클리닉실",
            "TU융합데이터 허브실",
            "고성능 인공지능 실험실",
            "우측 입출구1",
            "우측 입출구2",
            "우측 휴식처",
            "창업동아리실",
            "남자 화장실"};

    TextView textView, text;
    Button picture, gallery, findPlace;
    ImageView imageView;
    int imageSize = 224;
    int maxPos = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        text = findViewById(R.id.textView2);
        picture = findViewById(R.id.btn);
        gallery = findViewById(R.id.btn2);
        findPlace = findViewById(R.id.btn3);
        imageView = findViewById(R.id.imageView);

        // 버튼 클릭시 사진을 찍기
        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // 권한이 있는 경우 카메라 실행
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    // 카메라 권한이 없으면 요청
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        // 갤러리에서 가져오기
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        // 현재 위치가 어딘지
        findPlace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImaveViewActivity.class);
                if (maxPos == 19){
                    Toast.makeText(getApplicationContext(), "사진을 찍어주세요", Toast.LENGTH_SHORT).show();
                }

                else {
                    String aClass = classes[maxPos];
                    Log.d("maxPos", String.valueOf(maxPos));
                    Log.d("Class", aClass);

                    intent.putExtra("key", aClass); // classes 값 넘겨 주기
                    startActivityForResult(intent, 5);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 사진을 찍은 경우
        if (requestCode == 3 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        // 갤러리에서 사진을 가져온 경우
        } else if (requestCode == 1){
            Uri dat = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                image = rotateImage(image, 90);
//                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        else{
         // pass
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix,true);
    }

    public void classifyImage(Bitmap image) {
        try {
            // 학습한 모델
            Mobilenet2311201 model = Mobilenet2311201.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // 모델 추론하고 그 결과
            Mobilenet2311201.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String a = String.valueOf(maxPos);
            Log.d("maxPos", a);

            String b = String.valueOf(confidences[0]);
            Log.d("confidences", b);

            text.setText("제 1정보통신관(15번건물)");

            textView.setText(format("%s 입니다.",classes[maxPos]));

            // 모델 리소스를 제거
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}