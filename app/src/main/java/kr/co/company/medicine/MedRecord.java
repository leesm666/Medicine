package kr.co.company.medicine;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MedRecord extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    int i = 1; //pk
    FirebaseAuth firebaseAuth;



    ImageView btn_back;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button start, end;
    EditText number, name;
    LinearLayout timelayout1, timelayout2, timelayout3, tl1, tl2, tl3;
    SQLiteDatabase sqlDB;
    Integer timesPerDay;
    Integer[] day_array;
    String[] time_array;
    String startday, endday, pill_list2;
    TextView startDate, endDate, pill_listTv;
    Integer Y, M, D;
    MyDBHelper myHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_record);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        myHelper = new MyDBHelper(this);
        //setTimePicker();

//        -----------------------------------------------//


        EditText alname = findViewById(R.id.nameEdit);
//        EditText aldays = findViewById(R.id.daysEdit);
        Button timeText1 = findViewById(R.id.timeText1);
        Button timeText2 = findViewById(R.id.timeText2);
        Button timeText3 = findViewById(R.id.timeText3);
        Button submitBtnTwo = findViewById(R.id.submitBtnTwo);

        // 약 이름 가지고오기
        pill_listTv = findViewById(R.id.pill_listTv);
        Intent intent = getIntent();
        // pill_list 배열에 복용 약 이름 저장
        ArrayList<String> pill_list = intent.getStringArrayListExtra("pill_list");
        pill_list2 = pill_list.toString().replace("]", "").replace("[","");
        pill_listTv.setText("복용 약 목록\n"+ pill_list2);

        //// 0713

        start = (Button) findViewById(R.id.startBtn);
        end = (Button) findViewById(R.id.endBtn);
//        name = (EditText) findViewById(R.id.name);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        Y = null;
        M = null;
        D = null;

        startday = null;
        endday = null;


        tl1 = findViewById(R.id.tl1);
        tl2 = findViewById(R.id.tl2);
        tl3 = findViewById(R.id.tl3);
        Button select1 = (Button) findViewById(R.id.select1);
        Button select2 = (Button) findViewById(R.id.select2);
        Button select3 = (Button) findViewById(R.id.select3);


        timesPerDay = null;

        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tl1.setVisibility(View.VISIBLE);
                tl2.setVisibility(View.GONE);
                tl3.setVisibility(View.GONE);
                timesPerDay = 1;
            }
        });

        select2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tl1.setVisibility(View.VISIBLE);
                tl2.setVisibility(View.VISIBLE);
                tl3.setVisibility(View.GONE);
                timesPerDay = 2;
            }

        });
        select3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tl1.setVisibility(View.VISIBLE);
                tl2.setVisibility(View.VISIBLE);
                tl3.setVisibility(View.VISIBLE);
                timesPerDay = 3;
            }
        });

        start.setOnClickListener(new View.OnClickListener() { //시작일 설정 버튼 누를시
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MedRecord.this, mDateSetListener, mYear, mMonth, mDay).show();
            }

            public DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            Y = year;
                            M = monthOfYear + 1;
                            D = dayOfMonth;
                            UpdateNow();
                        }
                    };

            void UpdateNow() {
                if (mMonth < 9) {
                    if (mDay < 10) {
                        startDate.setText(String.format(" %d년 0%d월 0%d일", mYear, mMonth + 1, mDay));
                        startday = String.format("%d-0%d-0%d", mYear, mMonth + 1, mDay);
                    } else {
                        startDate.setText(String.format(" %d년 0%d월 %d일", mYear, mMonth + 1, mDay));
                        startday = String.format("%d-0%d-%d", mYear, mMonth + 1, mDay);
                    }
                } else {
                    if (mDay < 10) {
                        startDate.setText(String.format(" %d년 %d월 0%d일", mYear, mMonth + 1, mDay));
                        startday = String.format("%d-%d-0%d", mYear, mMonth + 1, mDay);
                    } else {
                        startDate.setText(String.format(" %d년 %d월 %d일", mYear, mMonth + 1, mDay));
                        startday = String.format("%d-%d-%d", mYear, mMonth + 1, mDay);
                    }
                }
            }
        });


        end.setOnClickListener(new View.OnClickListener() { //종료일 설정 버튼 누를시
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MedRecord.this, mDateSetListener, mYear, mMonth, mDay).show();
            }

            public DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            UpdateNow();
                        }
                    };

            void UpdateNow() {
                Toast toast3 = new Toast(MedRecord.this);
                View toastView3 = (View) View.inflate(MedRecord.this, R.layout.toast, null);
                TextView toastText3 = (TextView) toastView3.findViewById(R.id.toast1);
                toastText3.setText("잘못된 선택입니다.");
                toast3.setView(toastView3);

                if (mMonth < 9) {
                    if (mYear < Y) {
                        toast3.show();
                    } else if (mYear == Y && mMonth + 1 < M) {
                        toast3.show();
                    } else if (mYear == Y && mMonth + 1 == M && mDay < D) {
                        toast3.show();
                    } else if (mDay < 10) {
                        endDate.setText(String.format(" %d년 0%d월 0%d일", mYear, mMonth + 1, mDay));
                        endday = String.format("%d-0%d-0%d", mYear, mMonth + 1, mDay);
                    } else {
                        endDate.setText(String.format(" %d년 0%d월 %d일", mYear, mMonth + 1, mDay));
                        endday = String.format("%d-0%d-%d", mYear, mMonth + 1, mDay);
                    }
                } else {
                    if (mYear < Y) {
                        toast3.show();
                    } else if (mYear == Y && mMonth + 1 < M) {
                        toast3.show();
                    } else if (mYear == Y && mMonth + 1 == M && mDay < D) {
                        toast3.show();
                    } else if (mDay < 10) {
                        endDate.setText(String.format(" %d년 %d월 0%d일", mYear, mMonth + 1, mDay));
                        endday = String.format("%d-%d-0%d", mYear, mMonth + 1, mDay);
                    } else {
                        endDate.setText(String.format(" %d년 %d월 %d일", mYear, mMonth + 1, mDay));
                        endday = String.format("%d-%d-%d", mYear, mMonth + 1, mDay);
                    }
                }
            }
        });



        /// 0807s


        final Button[] day = new Button[7];
        final Integer[] dayID = {R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat, R.id.sun};
        int i;
        day_array = new Integer[7];
        for (i = 0; i < dayID.length; i++) {
            day[i] = (Button) findViewById(dayID[i]);
            day_array[i] = 0;
        }

        for (i = 0; i < dayID.length; i++) {
            final int index;
            index = i;
            day[index].setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View view) {
                    if (day[index].getCurrentTextColor() == Color.BLACK) {
//                        day[index].setBackgroundResource(R.drawable.color2);
                        day[index].setTextColor(Color.WHITE);
                        day_array[index] = 1;
                    }
                    else{
//                        day[index].setBackgroundResource(R.drawable.color4);
                        day[index].setTextColor(Color.BLACK);
                        day_array[index] = 0;
                    }
                }
            });
        }


        /// 0807e


        //// 0713

        timelayout1 = (LinearLayout) findViewById(R.id.timeView1);
        timelayout2 = (LinearLayout) findViewById(R.id.timeView2);
        timelayout3 = (LinearLayout) findViewById(R.id.timeView3);

        final Button[] timeSet = new Button[3];
        final Integer[] timeSetID = {R.id.timeText1, R.id.timeText2, R.id.timeText3};
        int j;
        for (j = 0; j < timeSetID.length; j++) {
            timeSet[j] = (Button) findViewById(timeSetID[j]);
        }

        final View[] timePick = new View[3];
        final Integer[] timePickID = {R.layout.timepicker, R.layout.timepicker, R.layout.timepicker};
        final TimePicker[] times = new TimePicker[3];
        final Integer[] timesID = {R.id.timepicker, R.id.timepicker, R.id.timepicker,};
        final TextView[] tv = new TextView[3];
        final Integer[] tvID = {R.id.tv1, R.id.tv2, R.id.tv3};

        int k;
        for (k = 0; k < timePickID.length; k++) {
            timePick[k] = (View) View.inflate(MedRecord.this, timePickID[k], null);
            times[k] = (TimePicker) timePick[k].findViewById(timesID[k]);
            tv[k] = (TextView) findViewById(tvID[k]);
        }

        time_array = new String[3];
        for (k = 0; k < 3; k++) {
            time_array[k] = null;
        }

        for (k = 0; k < timePickID.length; k++) {
            final int index;
            index = k;
            timeSet[index].setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View view) {
                    AlertDialog.Builder dlg1 = new AlertDialog.Builder(MedRecord.this);
                    dlg1.setTitle("시간선택");
                    if (timePick[index].getParent() != null) {
                        ((ViewGroup) timePick[index].getParent()).removeView(timePick[index]);
                    }
                    times[index].setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
                    dlg1.setView(timePick[index]);
                    dlg1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (times[index].getCurrentHour() < 12) {
                                if (times[index].getCurrentHour() < 10) {
                                    if (times[index].getCurrentMinute() < 10) {
                                        tv[index].setText("오전 " + 0 + Integer.toString(times[index].getCurrentHour()) + " : " +
                                                0 + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = 0 + Integer.toString(times[index].getCurrentHour()) + ":" +
                                                0 + Integer.toString(times[index].getCurrentMinute());
                                    } else {
                                        tv[index].setText("오전 " + 0 + Integer.toString(times[index].getCurrentHour()) + " : "
                                                + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = 0 + Integer.toString(times[index].getCurrentHour()) + ":"
                                                + Integer.toString(times[index].getCurrentMinute());
                                    }
                                } else {
                                    if (times[index].getCurrentMinute() < 10) {
                                        tv[index].setText("오전 " + Integer.toString(times[index].getCurrentHour()) + " : "
                                                + 0 + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                                + 0 + Integer.toString(times[index].getCurrentMinute());
                                    } else {
                                        tv[index].setText("오전 " + Integer.toString(times[index].getCurrentHour()) + " : "
                                                + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                                + Integer.toString(times[index].getCurrentMinute());
                                    }
                                }
                            } else {
                                if (times[index].getCurrentHour() - 12 < 10) {
                                    if (times[index].getCurrentMinute() < 10) {
                                        tv[index].setText("오후 " + 0 + Integer.toString(times[index].getCurrentHour() - 12) + " : "
                                                + 0 + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                                + 0 + Integer.toString(times[index].getCurrentMinute());
                                    } else {
                                        tv[index].setText("오후 " + 0 + Integer.toString(times[index].getCurrentHour() - 12) + " : "
                                                + Integer.toString(times[index].getCurrentMinute()));
                                        timeSet[index].setText("수정");
                                        time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                                + Integer.toString(times[index].getCurrentMinute());
                                    }
                                } else if (times[index].getCurrentMinute() < 10) {
                                    tv[index].setText("오후 " + Integer.toString(times[index].getCurrentHour() - 12) + " : "
                                            + 0 + Integer.toString(times[index].getCurrentMinute()));
                                    timeSet[index].setText("수정");
                                    time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                            + 0 + Integer.toString(times[index].getCurrentMinute());
                                } else {
                                    tv[index].setText("오후 " + Integer.toString(times[index].getCurrentHour() - 12) + " : "
                                            + Integer.toString(times[index].getCurrentMinute()));
                                    timeSet[index].setText("수정");
                                    time_array[index] = Integer.toString(times[index].getCurrentHour()) + ":"
                                            + Integer.toString(times[index].getCurrentMinute());
                                }
                            }
                        }
                    });
                    dlg1.setNegativeButton("취소", null);
                    dlg1.show();
                }
            });


            //// 0807s

            submitBtnTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (alname == null || startday == null || endday == null || timesPerDay == null ||
                            (time_array[0] == null)){
                        Toast toast1 = new Toast(MedRecord.this);
                        View toastView1 = (View) View.inflate(MedRecord.this, R.layout.toast, null);
                        TextView toastText1 = (TextView) toastView1.findViewById(R.id.toast1);
                        toastText1.setText("모든 항목을 입력하세요");
                        toast1.setView(toastView1);
                        toast1.show();
                    } else {
                        sqlDB = myHelper.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO medi (mediList, mediName, startDate, endDate, timesPerDay," +
                                "mon, tue, wed, thu, fri, sat, sun) VALUES ('" + pill_list2 + "', '" +
                                alname.getText().toString() + "', '" + startday + "', '" +
                                endday + "', '" + timesPerDay + "', '" +
                                day_array[0] + "','" + day_array[1] + "','" + day_array[2] + "','" +
                                day_array[3] + "','" + day_array[4] + "','" + day_array[5] + "','" +
                                day_array[6] + "');");
                        sqlDB.execSQL("INSERT INTO time (oneTime, twoTime, threeTime) VALUES ('" + time_array[0] + "', '" + time_array[1] + "', '" + time_array[2] + "');");
                        sqlDB.close();
                        startService(new Intent(MedRecord.this, MyService.class));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            //// 0807e
        }
        //// 0714e

    }

}


