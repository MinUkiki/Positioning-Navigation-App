package com.example.wepcrawling;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ImaveViewActivity extends AppCompatActivity {
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imave_view);

        imageView2 = findViewById(R.id.imageView2);


        Intent j = getIntent();
        String result = j.getStringExtra("key");

            assert result != null;

            if (result.equals("좌측 입출구1")){
                imageView2.setImageResource(R.drawable.left1);
            } else if (result.equals("좌측 입출구2")) {
                imageView2.setImageResource(R.drawable.left2);
            } else if (result.equals("좌측 데스크1")) {
                imageView2.setImageResource(R.drawable.leftdesk1);
            } else if (result.equals("좌측 데스크2")) {
                imageView2.setImageResource(R.drawable.leftdesk2);
            } else if (result.equals("좌측 휴식처")) {
                imageView2.setImageResource(R.drawable.leftrest);
            } else if (result.equals("여자화장실")) {
                imageView2.setImageResource(R.drawable.woman);
            } else if (result.equals("엘리베이터")) {
                imageView2.setImageResource(R.drawable.elevator);
            } else if (result.equals("리치 회의실")) {
                imageView2.setImageResource(R.drawable.rich);
            } else if (result.equals("에이다 회의실")) {
                imageView2.setImageResource(R.drawable.ada);
            } else if (result.equals("산악협력 회의실")) {
                imageView2.setImageResource(R.drawable.san);
            } else if (result.equals("스톨먼 작업실")) {
                imageView2.setImageResource(R.drawable.stallman);
            } else if (result.equals("코딩 클리닉실")) {
                imageView2.setImageResource(R.drawable.coding);
            } else if (result.equals("TU융합데이터 허브실")) {
                imageView2.setImageResource(R.drawable.datahub);
            } else if (result.equals("고성능 인공지능 실험실")) {
                imageView2.setImageResource(R.drawable.ai);
            } else if (result.equals("우측 입출구1")) {
                imageView2.setImageResource(R.drawable.right1);
            } else if (result.equals("우측 입출구2")) {
                imageView2.setImageResource(R.drawable.right2);
            } else if (result.equals("우측 휴식처")) {
                imageView2.setImageResource(R.drawable.rightrest);
            } else if (result.equals("창업동아리실")) {
                imageView2.setImageResource(R.drawable.chang);
            } else if (result.equals("남자 화장실")) {
                imageView2.setImageResource(R.drawable.man);
            }
    }
}