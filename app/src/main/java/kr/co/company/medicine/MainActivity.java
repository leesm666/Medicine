package kr.co.company.medicine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public CalendarView calendarView;


    RecyclerView rv_list;
    ListItemRecyclerViewAdapter listItemRecyclerViewAdapter;
    ArrayList<ListItem> selectingList = new ArrayList<>();
    MyDBHelper myHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Button_add = findViewById(R.id.button);
        calendarView = findViewById(R.id.calendarView);


        //임시 코드
        Button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MedSearch.class);
                startActivity(intent);
            }
        }); //임시 코드


        myHelper = new MyDBHelper(this);

        selectingList.addAll(myHelper.allListItems());

        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        listItemRecyclerViewAdapter = new ListItemRecyclerViewAdapter(selectingList, this);
        rv_list.setAdapter(listItemRecyclerViewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        
        //0811S



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String  Year = String.valueOf(year);
                String  Month = String.valueOf(month);
                String  curDate = String.valueOf(dayOfMonth);

                String Selected = Year + "-" + Month + "-" + curDate;
            }
        });
        
        //0811E

    }
}