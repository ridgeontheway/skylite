package com.example.skylite.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skylite.MainActivity;
import com.example.skylite.Model.ModelConstellationInfo;
import com.example.skylite.R;

import java.util.List;


/**
 * This class is entered when a card view in the recycler view is pressed, it will show a larger image of the constellation
 * and will provide more of a description for it, this data displayed will be based on the index of the card selected
 * as i pass the index into this activity by intent and using that index, i can reference the correct object from the
 * modelConstellationInfo object list and this will have the correct data
 */

public class ConstellationInDepthView extends AppCompatActivity {
    List<ModelConstellationInfo> modelConstellationInfo = MainActivity.modelConstellationInfo;

    public TextView name, description, longdesc;
    public ImageView imageView;
    public static boolean achievement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = getIntent();
        
        //getting index of the object from the intent
        int intValue = mIntent.getIntExtra("intVariableName", 0);
        setContentView(R.layout.fragment_constellation_info);
        ModelConstellationInfo temp = modelConstellationInfo.get(intValue);
        name = findViewById(R.id.constellationTitle);
        description = findViewById(R.id.constellationDescription);
        longdesc = findViewById(R.id.constellationDescriptionlong);
        imageView = findViewById(R.id.constellationImage);

        if (achievement == false) {
            Toast.makeText(ConstellationInDepthView.this, "You Unlocked the Achievment:Constellation researcher", Toast.LENGTH_LONG).show();
        }
        achievement = true;

        name.setText(temp.getTitle());
        description.setText(temp.getDescriptionShort());
        longdesc.setText(temp.getDescriptionLong());
        imageView.setImageResource(getResources().getIdentifier(temp.getImageName(), "drawable", getPackageName()));

    }


}
