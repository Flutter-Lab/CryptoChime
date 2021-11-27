package com.example.cryptochime.SettingsActivity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cryptochime.R;

import java.util.ArrayList;

public class SelectSoundFragment extends Fragment {

    SharedPreferences selectedAlarmTone;

    Button audioSelectDoneButton;

    ListView listView;
    String soundTitle[] = {"Happy Bells", "iPhone 6 Remix", "Siren Alarm", "Sound Effect 1", "Sound Effect 2"};
    int[] checkImages = {R.drawable.ic_white_sqr, R.drawable.ic_white_sqr, R.drawable.ic_white_sqr, R.drawable.ic_white_sqr, R.drawable.ic_white_sqr };

    MediaPlayer curMP;
    ArrayList<MediaPlayer> mpL = new ArrayList<>();

    int audioPlaying = 0;

    int[] audioArray = {R.raw.happy_bells, R.raw.iphone_6_remix,
    R.raw.siren_alarm, R.raw.sound_effect_1, R.raw.sound_effect_2};




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_sound, container, false);


        audioSelectDoneButton = view.findViewById(R.id.soundSelectDoneButoon);

        mpL.add(MediaPlayer.create(getContext(), audioArray[0]));
        mpL.add(MediaPlayer.create(getContext(), audioArray[1]));
        mpL.add(MediaPlayer.create(getContext(), audioArray[2]));
        mpL.add(MediaPlayer.create(getContext(), audioArray[3]));
        mpL.add(MediaPlayer.create(getContext(), audioArray[4]));


        selectedAlarmTone = getContext().getSharedPreferences("selectedAlarmTone", Context.MODE_PRIVATE);





        audioSelectDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curMP != null && curMP.isPlaying()){
                    curMP.stop();
                }
                getFragmentManager().popBackStack();

                int seletedAudio = selectedAlarmTone.getInt("selectedAlarmTone", 0);
                Log.i(TAG, "onClick: Number "+ seletedAudio + "Selected");
            }
        });


        listView = view.findViewById(R.id.soundListView);

        //Create a adapter class
        MyAdapter adapter = new MyAdapter(getContext(), soundTitle, checkImages);
        listView.setAdapter(adapter);



        //Set on item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                changeOtherimages();
                checkImages[position] = R.drawable.ic_check;
                stopAllAudio(position);
                adapter.notifyDataSetChanged();
            }
        });






        return view;
    }

    private void stopAllAudio(int position) {
        MediaPlayer mp;
        if (curMP != null && curMP.isPlaying()){
            curMP.stop();
        }

        mp = MediaPlayer.create(getContext(), audioArray[position]);
        mp.start();
        selectedAlarmTone.edit().putInt("selectedAlarmTone", position).apply();
        curMP = mp;
        Log.i("TAG", "stopAllAudio: "+position);

    }

    private void changeOtherimages() {
        for (int i=0; i<5; i++){
            checkImages[i] = R.drawable.ic_white_sqr;
        }

    }







    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] rTitle;
        int[] images;

        MyAdapter(Context c, String title[], int images[]){
            super(c, R.layout.sound_list_item, R.id.soundNameTextView, title);
            this.context = c;
            this.rTitle = title;
            this.images = images;

        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.sound_list_item, parent, false);
            ImageView image = row.findViewById(R.id.soundCheckImgView);
            TextView myTitle = row.findViewById(R.id.soundNameTextView);

            //Set resources on views
            image.setImageResource(images[position]);
            myTitle.setText(rTitle[position]);



            return row;
        }
    }


}