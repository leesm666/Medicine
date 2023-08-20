package kr.co.company.medicine;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;
import java.util.stream.Collectors;

public class MedCamera extends AppCompatActivity {

    ImageView clear, getImage, copy, setImage;
    //    EditText recgText;
    Uri imageUri;
    TextView recgTV, recgTV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_camera);

        getImage = findViewById(R.id.getimage);
        setImage = findViewById(R.id.setimage);
//        recgText = findViewById(R.id.recgText);
        recgTV = findViewById(R.id.recgTV);
        recgTV2 = findViewById(R.id.recgTV2);

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(MedCamera.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

    }

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
                setImage.setImageURI(imageUri);
                recognizeText();

            }
        }
        else {
            Toast.makeText(this, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void recognizeText() {

        if (imageUri!=null){

            try {
                InputImage inputImage = InputImage.fromFilePath(MedCamera.this,imageUri);

//                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                TextRecognizer recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                Task<Text> result = recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                // 전체 코드 출력 코드
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
                                            String[] contas = {"안액","캡술","캡슐","연고","리그","정","동광","익셀", "캅셀"};
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
                                        recgTV.setText(result);
                                    }
                                }
                                System.out.println(tx);
                                List<String> resultTx = new ArrayList<String>();
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
                                    List<String>List = new ArrayList<>();

                                    for(int i= 0; i<pillArray.length(); i++){
                                        jsonObject = pillArray.getJSONObject(i);
                                        for(String j : resultTx){
                                            if(jsonObject.getString("pname").contains(j)){
                                                Pill pill = new Pill();

                                                pill.setPcode(jsonObject.getString("pcode"));
                                                pill.setPname(jsonObject.getString("pname"));
                                                pill.setPcompany(jsonObject.getString("pcompany"));

                                                String list = jsonObject.getString("pname");
                                                List.add(list);
                                                System.out.println(jsonObject.getString("pname"));
//                                                recgTV2.setText(jsonObject.getString("pname"));

                                            }
                                        }
                                    }
                                    // json 에서 파싱한 값 추출 결과
                                    recgTV2.setText(List.toString());

                                }catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
//                                jsonParsing();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MedCamera.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
//    private String getJsonString(){ // json 파일 읽기
//        String json = "";
//
//        try {
//            InputStream is = getAssets().open("pilljson.json");
//            int fileSize = is.available();
//
//            byte[] buffer = new byte[fileSize];
//            is.read(buffer);
//            is.close();
//
//           String json = new String(buffer, "UTF-8");
//
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }
//
//        return json;
//    }

//    private  void jsonParsing(){
//        try {
//
//            InputStream is = getAssets().open("pilljson.json");
//            int fileSize = is.available();
//
//            byte[] buffer = new byte[fileSize];
//            is.read(buffer);
//            is.close();
//
//            String json = new String(buffer, "UTF-8");
//
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray pillArray = jsonObject.getJSONArray("Pills");
//
//            for(int i= 0; i<pillArray.length(); i++){
//                jsonObject = pillArray.getJSONObject(i);
//
//                if(jsonObject.getString("pname").contains("크로세프")){
//                    Pill pill = new Pill();
//
//                    pill.setPcode(jsonObject.getString("pcode"));
//                    pill.setPname(jsonObject.getString("pname"));
//                    pill.setPcompany(jsonObject.getString("pcompany"));
//
//                    System.out.println(jsonObject.getString("pname"));
//                }
//
//            }
//
//        }catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//    }
}