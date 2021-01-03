package gurveen.com.twitterc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FollowActivity extends AppCompatActivity {
    ListView followersListView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> keysList = new ArrayList<>();

    Map<String,String> followersMap =  new HashMap<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        setTitle("Current Users");

        followersListView = findViewById(R.id.followersListView);
        followersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_checked,usersList);
        followersListView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               String email = dataSnapshot.child("email").getValue().toString();
               usersList.add(email);
               keysList.add(dataSnapshot.getKey());
               arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        followersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if (checkedTextView.isChecked()){
                    followersMap.put("followEmail",usersList.get(position));
                    if (mAuth.getCurrentUser() != null){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("isFollowing")
                            .push().setValue(followersMap);
                    }


                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.follow_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.timeline){
            //go to timeline activity
            Intent intent = new Intent(FollowActivity.this,TimelineActivity.class);
            startActivity(intent);
        }else{
            //logout the user
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
