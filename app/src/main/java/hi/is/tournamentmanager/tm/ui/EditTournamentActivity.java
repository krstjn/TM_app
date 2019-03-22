package hi.is.tournamentmanager.tm.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import hi.is.tournamentmanager.tm.R;
import hi.is.tournamentmanager.tm.model.Tournament;

public class EditTournamentActivity extends AppCompatActivity {

    private static final String TOKEN_PREFERENCE = "TOKEN_PREFERENCE";
    private static final String TOURNAMENT_ITEM = "TOURNAMENT_ITEM";
    Tournament t;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.profile:
                    i = new Intent(EditTournamentActivity.this, ViewProfileActivity.class);
                    // startActivityForResult(i, REQUEST_CODE_VIEWALLTOURNAMENTS);
                    startActivity(i);
                    finish();
                    return true;
                case R.id.view:
                    i = ViewAllTournamentsActivity.newIntent(EditTournamentActivity.this, true);
                    // startActivityForResult(i, REQUEST_CODE_VIEWALLTOURNAMENTS);
                    startActivity(i);
                    finish();
                    return true;
                case R.id.create:
                    i = new Intent(EditTournamentActivity.this, CreateTournamentActivity.class);
                    // startActivityForResult(i, REQUEST_CODE_VIEWALLTOURNAMENTS);
                    startActivity(i);
                    finish();
                    return true;
            }
            return false;
        }
    };

    SharedPreferences mSharedPreferences;


    EditText mName;
    EditText mMaxTeams;
    EditText mRounds;
    EditText mSignUp;
    EditText mTeamName;
    Button mAddTeam;
    Button mSave;
    TextView mEmptyList;
    ListView mTeamsList;
    Spinner mSport;
    DatePicker mDatePicker;
    List<String> teams = new ArrayList<>();
    ArrayAdapter<String> mAdapter;
    String isoSignUp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tournament);
        t = (Tournament)getIntent().getSerializableExtra(TOURNAMENT_ITEM);
        mSharedPreferences = getSharedPreferences(TOKEN_PREFERENCE, Context.MODE_PRIVATE);
        System.out.println(t.getName());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mName = findViewById(R.id.input_name);
        mSave = findViewById(R.id.btn_edit_tournament);
        mMaxTeams = findViewById(R.id.input_maxTeams);
        mRounds = findViewById(R.id.input_rounds);
        mSignUp = findViewById(R.id.input_signUp);
        mDatePicker = findViewById(R.id.signUp_picker);
        mAddTeam = findViewById(R.id.btn_addTeam);
        mTeamName = findViewById(R.id.input_team_name);
        mTeamsList = findViewById(R.id.teams_list);
        mEmptyList = findViewById(R.id.teams_list_empty);
        mAdapter = new ArrayAdapter<String>(this, R.layout.addteam_list_row, R.id.item_team_name, teams);
        mTeamsList.setAdapter(mAdapter);
        mSport = findViewById(R.id.input_sport);

        mName.setText(t.getName(), TextView.BufferType.EDITABLE);
        mMaxTeams.setText(Integer.toString(t.getMaxTeams()), TextView.BufferType.EDITABLE);
        mRounds.setText(Integer.toString(t.getNrOfRounds()), TextView.BufferType.EDITABLE);

        for(int i = 0; i < t.getTeams().size(); i++) {
            teams.add(t.getTeams().get(i).getName());
        }

        Calendar cal;
        if (t.getSignUpExpiration() != null) {
            cal = Calendar.getInstance();
            cal.setTime(t.getSignUpExpiration());
            String year = String.valueOf(t.getSignUpExpiration().getYear());
            String month = String.valueOf(t.getSignUpExpiration().getMonth()+1);
            String day = String.valueOf(t.getSignUpExpiration().getDay());
            mSignUp.setText(day + "/" + month + "/" + year);
            isoSignUp = year + "-" + month + "-" + day;
        } else {
            cal = Calendar.getInstance(TimeZone.getDefault());
        }

        mDatePicker.init(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), datePickerListener);

        mSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                View focus = getCurrentFocus();
                if(focus != null){
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(
                                    Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                    Objects.requireNonNull(getCurrentFocus()).clearFocus();
                }

                mDatePicker.setVisibility(View.VISIBLE);
            }
        });

        mAddTeam.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String input = mTeamName.getText().toString();
                int maxTeams = Integer.parseInt("0"+mMaxTeams.getText().toString());
                if(input.length() > 0 && teams.size() < maxTeams){
                    mTeamName.setText("");
                    teams.add(input);
                    mEmptyList.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mTeamsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                System.out.println("Clicked: " + arg3);
                teams.remove((int)arg3);
                mAdapter.notifyDataSetChanged();
                if(teams.size() == 0) mEmptyList.setVisibility(View.VISIBLE);
            }
        });

        mSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Mögulega að verifya input eitthvað

            }
        });
    }

    private DatePicker.OnDateChangedListener datePickerListener = new DatePicker.OnDateChangedListener() {

        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mDatePicker.setVisibility(View.GONE);
            String year1 = String.valueOf(year);
            String month1 = String.valueOf(monthOfYear + 1);
            String day1 = String.valueOf(dayOfMonth);
            mSignUp.setText(day1 + "/" + month1 + "/" + year1);
            isoSignUp = year1 + "-" + month1 + "-" + day1;
        }
    };

    private void saveTournament() {

    }
}
