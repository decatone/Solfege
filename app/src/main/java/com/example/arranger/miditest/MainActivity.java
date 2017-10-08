package com.example.arranger.miditest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener,
        View.OnTouchListener, View.OnClickListener {
    //**this class is for multiple delayed-play TimeTask
    private class TaskPlayNote extends TimerTask{
        public void run(){
            playNote();
        }
    }

    private MidiDriver midiDriver;
    private byte[] event;
    private int[] config;
    private Button buttonPlayNote;
    private Button buttonNoteGen;
    //**All twelve tones...
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button10;
    private Button button11;
    private Button button12;
    //**indicator
    private TextView tv;
    private int pitch = 60; // 60 = middle C
    //**Delay Timer
    private Timer timer1;
    private Timer timer2;
    TaskPlayNote taskPlayNote = new TaskPlayNote();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //**Test Button, Delete it when tested.
        buttonNoteGen = (Button)findViewById(R.id.buttonNoteGen);
        buttonNoteGen.setOnClickListener(this);
        //**play button
        buttonPlayNote = (Button)findViewById(R.id.buttonPlayNote);
        buttonPlayNote.setOnTouchListener(this);
        //**buttons for all the twelve notes
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);

        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);

        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(this);

        button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(this);

        button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(this);

        button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(this);

        button7 = (Button)findViewById(R.id.button7);
        button7.setOnClickListener(this);

        button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(this);

        button9 = (Button)findViewById(R.id.button9);
        button9.setOnClickListener(this);

        button10 = (Button)findViewById(R.id.button10);
        button10.setOnClickListener(this);

        button11 = (Button)findViewById(R.id.button11);
        button11.setOnClickListener(this);

        button12 = (Button)findViewById(R.id.button12);
        button12.setOnClickListener(this);

        //**TextView
        tv = (TextView) findViewById(R.id.textView);
        //**Timer
        timer1 = new Timer();

        //**Instantiate the driver.
        midiDriver = new MidiDriver();
        //**Set the listener.
        midiDriver.setOnMidiStartListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();

        // Get the configuration.
        config = midiDriver.config();

        // Print out the details.
        Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    @Override
    public void onMidiStart() {
        Log.d(this.getClass().getName(), "onMidiStart()");
    }

    private void playNote() {

        // Construct a note ON message for generated notes at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) pitch;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    private void stopNote() {

        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = (byte) pitch;  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    @Override
    public boolean onTouch(View v1, MotionEvent event) {

        Log.d(this.getClass().getName(), "Motion event: " + event);
        if (v1.getId() == R.id.buttonPlayNote) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_DOWN");
                playNote();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_UP");
                stopNote();
            }
        }

        return false;
    }

    private int noteGen(){
        // A random note generator
        int max = 72;
        int min = 60;
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    //Delay play method.
    private void delayPlay(){
        timer1 = new Timer();
        taskPlayNote = new TaskPlayNote();
        timer1.schedule(taskPlayNote,500);
    }


    //a method to determine if the answer is correct.
    private void noteCorrect(int intrvl){
        if(pitch == intrvl | pitch == 12 + intrvl | pitch == 24 + intrvl| pitch == 36 + intrvl | pitch == 48 + intrvl | pitch == 60 + intrvl | pitch == 72 + intrvl | pitch == 84 + intrvl | pitch == 96 + intrvl | pitch == 108 + intrvl){
            tv.setText("Correct!");
            tv.setTextColor(0xff00ff00);
            pitch = noteGen();
            delayPlay();
        }
        else{
            tv.setText("Wrong!");
            tv.setTextColor(0xFFFF0000);
        }
    }

    @Override
    public void onClick(View v2) {
        // When btnNoteGen is clicked,
        if(v2.getId() == R.id.buttonNoteGen) {
            // Set pitch to a new one.
            //Log.i("btnNoteGen", "test");
            pitch = noteGen();
            //Log.d(String.valueOf(pitch), "is new pitch");
        }
        if(v2.getId() == R.id.button1){
            noteCorrect(0);
        }
        if(v2.getId() == R.id.button2){
            noteCorrect(1);
        }
        if(v2.getId() == R.id.button3){
            noteCorrect(2);
        }
        if(v2.getId() == R.id.button4){
            noteCorrect(3);
        }
        if(v2.getId() == R.id.button5){
            noteCorrect(4);
        }
        if(v2.getId() == R.id.button6){
            noteCorrect(5);
        }
        if(v2.getId() == R.id.button7){
            noteCorrect(6);
        }
        if(v2.getId() == R.id.button8){
            noteCorrect(7);
        }
        if(v2.getId() == R.id.button9){
            noteCorrect(8);
        }
        if(v2.getId() == R.id.button10){
            noteCorrect(9);
        }
        if(v2.getId() == R.id.button11){
            noteCorrect(10);
        }
        if(v2.getId() == R.id.button12){
            noteCorrect(11);
        }
    }
}