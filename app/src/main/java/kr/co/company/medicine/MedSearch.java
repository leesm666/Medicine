package kr.co.company.medicine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MedSearch extends AppCompatActivity {
    private List<String> list, list_ca;
    ListView listView1;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem_ca;
    Button button3, button4, button_add;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_search);

        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        listView1 = findViewById(R.id.listView1);
        button_add = findViewById(R.id.button);

        // 리스트 생성, list에 데이터 추가하는 메소드
        list = new ArrayList<>();
        // 자동완성 메소드
        settingList();
        // 다음으로 클릭시 약품명 리스트 값 넘겨주는 코드
        SettingListener();

        // 자동완성 AutoCompleteTextView 파싱, AutoCompleteTextView의 단어 출력을 위해 어댑터 연결
        final AutoCompleteTextView autoCompleteTV = findViewById(R.id.autoCompleteTV);
        autoCompleteTV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list));


        // 리스트 뷰에 저장 하는 코드
        listItem_ca = new ArrayList<String>();
        // 카메라로 찍은 약품명 가져오는 리스트
        list_ca = new ArrayList<String>();

        // 카메라 기능 listItem에 약추가
        button4.setOnClickListener(new View.OnClickListener() { // 클릭 시 카메라, 갤러리 버튼 열림
            @Override
            public void onClick(View view) {
                ImagePicker.with(MedSearch.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        // 리스트 뷰에 약품명 추가하는 코드 (+)버튼 누룰 시
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteTV.getText().toString().isEmpty()) { // 약이름 입력 없이 + 버튼 클릭 시
                    Toast.makeText(MedSearch.this, "약품명이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    listItem_ca.add(autoCompleteTV.getText().toString());
                    adapter.notifyDataSetChanged(); // 변경되었음을 알려주는 코드
                    Toast.makeText(getApplicationContext(), autoCompleteTV.getText().toString() + " 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    autoCompleteTV.setText("");
                }
            }
        });
        // 어댑터와 리스트뷰 연결 코드
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,listItem_ca);
        listView1.setAdapter(adapter);

        // 약 이름 클릭시 삭제 코드
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),listItem_ca.get(i).toString() + " 삭제되었습니다.",Toast.LENGTH_SHORT).show();

                listItem_ca.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void settingList() { // 약품명 자동완성 메소드 코드
        try {
            InputStream is = getAssets().open("pilljson.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray pillArray = jsonObject.getJSONArray("Pills");
            for(int i= 0; i<pillArray.length(); i++){
                jsonObject = pillArray.getJSONObject(i);

                String plist = jsonObject.getString("pname");
                list.add(plist);
            }
        }
        catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    // 카메라 이미지값 전달 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            if(data!= null){
                Bitmap imagebitmap = null;
                imageUri = data.getData();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                        imagebitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),imageUri));
                    }else{
                        imagebitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "이미지가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                recognizeText();

            }
        }
        else {
            Toast.makeText(this, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText() { // 약품명 글자 인식 코드

        if (imageUri!=null){

            try {
                InputImage inputImage = InputImage.fromFilePath(MedSearch.this,imageUri);

//                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                TextRecognizer recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                Task<Text> result = recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                // 전체 페이지 출력 코드
//                                String recognizeText = text.getText();
//                                recgText.setText(recognizeText);
//                                ------------------------
                                StringBuilder result = new StringBuilder();
                                List<String> tx = new ArrayList<>();
                                for (Text.TextBlock block : text.getTextBlocks()) {
                                    String blockText = block.getText();
                                    for (Text.Line line : block.getLines()) {
                                        String TextBlock = line.getText();
                                        for (Text.Element element : line.getElements()) {
                                            String elementText = element.getText();
                                            String[] contas = {"캡술","캡슐","리그","정","동광","익셀", "캅셀","캡슬"};
                                            for (String conta : contas){
                                                if (elementText.contains(conta)){
                                                    if (elementText.contains("정제")) continue;
                                                    else if (elementText.contains("코팅")) continue;
                                                    else if (elementText.contains("정씩")) continue;
                                                    else if (elementText.contains("1정")) continue;
                                                    else if (elementText.contains("일분")) continue;
                                                    else if (elementText.contains("경질")) continue;
                                                    else if (elementText.contains("결정")) continue;

                                                    result.append(elementText);
                                                    result.append("\n");
                                                    tx.add(elementText.length() > 6 ? elementText
                                                            .replace("캡술", "캡슐")
                                                            .replace("캡슬", "캡슐")
                                                            .replace("일리","밀리")
                                                            .substring(0, elementText.length()-2) : elementText);
                                                }
                                            }
                                        }
                                    }
                                }

                                List<String> resultTx = new ArrayList<String>();
                                // 리스트 안 중복 값 제거
                                resultTx = tx.stream().distinct().collect(Collectors.toList());
                                System.out.println(resultTx);
                                try {
                                    InputStream is = getAssets().open("pilljson.json");
                                    int fileSize = is.available();

                                    byte[] buffer = new byte[fileSize];
                                    is.read(buffer);
                                    is.close();

                                    String json = new String(buffer, "UTF-8");

                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONArray pillArray = jsonObject.getJSONArray("Pills");

                                    for(int i= 0; i<pillArray.length(); i++){
                                        jsonObject = pillArray.getJSONObject(i);
                                        for(String j : resultTx){
                                            if(jsonObject.getString("pname").contains(j)){
                                                Pill pill = new Pill();

                                                pill.setPcode(jsonObject.getString("pcode"));
                                                pill.setPname(jsonObject.getString("pname"));
                                                pill.setPcompany(jsonObject.getString("pcompany"));

                                                String list = jsonObject.getString("pname");
                                                list_ca.add(list);
                                                System.out.println(jsonObject.getString("pname"));
                                            }
                                        }
                                    }
                                    // json 에서 파싱한 값 추출 결과
                                    // 리스트 값 리스트 리스트뷰에 추가하는 코드
                                    for (String k : list_ca) {
                                        listItem_ca.add(k);
                                        adapter.notifyDataSetChanged(); // 변경되었음을 알려주는 코드
                                        Toast.makeText(MedSearch.this, k + " 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    //두번 이상 카메라/갤러리를 사용할 때 리스트 값에 계속 추가되지 않게 방지하는 코드
                                    list_ca.clear();



                                }catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MedSearch.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void SettingListener() { //다음 클릭시
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MedRecord.class);
                intent.putStringArrayListExtra("pill_list", listItem_ca);
                startActivity(intent);
            }
        });
    }
}