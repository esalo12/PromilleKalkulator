package no.nord.promillekalkulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "TAG";

    private Button beregne;
    private RadioGroup kjonn;
    private RadioButton mann;
    private RadioButton dame;
    private EditText vekt;
    private Spinner start;
    private Spinner stop;
    private Spinner oller;
    private Spinner vin;
    private Spinner hetvin;
    private Spinner spritsvak;
    private Spinner spritsterk;
    private Double promille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Definerer feltene som skal brukes i kjønns og vekt beregning
        kjonn = (RadioGroup)findViewById(R.id.radioGruppe);
        mann = (RadioButton)findViewById(R.id.mann);
        dame = (RadioButton)findViewById(R.id.kvinne);
        vekt = (EditText) findViewById(R.id.vekt);

        // Definerer og fyller tid
        start = (Spinner) findViewById(R.id.startTid);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tiden, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        start.setAdapter(adapter);
        stop = (Spinner) findViewById(R.id.stopTid);
        stop.setAdapter(adapter);

        // Definerer og fyller valgmuligheter for antall enheter av de forskjellige typer drikke
        oller = (Spinner) findViewById(R.id.oller);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.enheter, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        oller.setAdapter(adapter2);
        vin = (Spinner) findViewById(R.id.vin);
        vin.setAdapter(adapter2);
        hetvin = (Spinner) findViewById(R.id.hetvin);
        hetvin.setAdapter(adapter2);
        spritsvak = (Spinner) findViewById(R.id.spritsvak);
        spritsvak.setAdapter(adapter2);
        spritsterk = (Spinner) findViewById(R.id.spritsterk);
        spritsterk.setAdapter(adapter2);


        // Definerer knapp og metoder som skal brukes ved onClick()
        beregne =(Button) findViewById(R.id.beregne);
        beregne.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                // Sjekker at kjønn er valgt
                if (kjonn.getCheckedRadioButtonId() == -1){
                    Toast toast = Toast.makeText(getApplicationContext(), "Du må velge kjønn", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                // Sjekker at vekt er satt
                else if (vekt.getText().toString().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Du må sette vekt", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    // Sjekker kun at personen setter tid altså minimum 1 time
                    if (start.getSelectedItem()==stop.getSelectedItem()){
                        Toast toast = Toast.makeText(getApplicationContext(), "Har du satt riktig tid?", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    // Starter innhenting av data fra spinnere, radiobuttons og teksteditfelt
                    else {
                        String startTid = (String) start.getSelectedItem();
                        String stopTid =(String) stop.getSelectedItem();
                        int tiden = beregneTid(startTid, stopTid);
                        int vekten = Integer.parseInt(vekt.getText().toString());
                        String antOl = (String) oller.getSelectedItem();
                        String antVin = (String) vin.getSelectedItem();
                        String antHetvin = (String) hetvin.getSelectedItem();
                        String antSpritsvak = (String) spritsvak.getSelectedItem();
                        String antSpritsterk = (String) spritsterk.getSelectedItem();
                        RadioButton kjonnet = (RadioButton) findViewById(kjonn.getCheckedRadioButtonId());
                        // Sjekker kjønn og implimenterer metoder etter kjønnet i if/else-statement
                        // og sender til resultat siden(classen)
                        if (kjonnet == findViewById(R.id.mann)){
                            promille = beregnPromilleMann(antOl, antVin, antHetvin, antSpritsvak, antSpritsterk, tiden, vekten);
                            sendResultat();
                        }
                        else {
                            promille = beregnPromilleKvinne(antOl, antVin, antHetvin, antSpritsvak, antSpritsterk, tiden, vekten);
                            sendResultat();
                        }
                    }
                }
            }
        });
    }
    // Finner antall timer man har drukket
    public int beregneTid(String starten, String slutten) {
        String[] startTid = starten.split(":");
        String[] stopTid = slutten.split(":");
        int drikketid;
        int start = Integer.parseInt(startTid[0]);
        int stop = Integer.parseInt(stopTid[0]);
        if (start>=stop){
            drikketid = (24-start)+stop;
            return drikketid;
        }
        else {
            drikketid = stop-start;
            return drikketid;
        }
    }
    // Metode for beregning av promille for menn, med "konservativ" vekt på forbrenning og vekt/opptaksforhold
    // (0.10 gr pr time, og 60% vekt oppløsningsforhold)
    public Double beregnPromilleMann(String oller, String vin, String hetvin, String spritsvak, String spritsterk, int drikketid, int vekt) {
        int antOl = Integer.parseInt(oller);
        int antVin = Integer.parseInt(vin);
        int antHetvin = Integer.parseInt(hetvin);
        int antSpritsvak = Integer.parseInt(spritsvak);
        int antSpritsterk = Integer.parseInt(spritsterk);
        int vektForhold =(int)Math.round(vekt * 0.60);
        Double promilleGrunnlag = (antOl*18.0)+(antVin*14.4)+(antHetvin*13.2)+(antSpritsvak*13.8)+(antSpritsterk*19.2);
        Double promille = (promilleGrunnlag/vektForhold)-(0.10*drikketid);
        if (promille<=0){
            Double kjor = 0.00;
            return kjor;
        }
        else {
            BigDecimal tilRunding = new BigDecimal(promille).setScale(3, RoundingMode.HALF_EVEN);
            promille = tilRunding.doubleValue();
            promille = Math.round(promille*100)/100.0d;
            return promille;
        }
    }

    // Metode for beregning av promille for kvinner, med "konservativ" vekt på forbrenning og vekt/opptaksforhold
    // (0.10 gr pr time, og 50% vekt oppløsningsforhold)
    public Double beregnPromilleKvinne(String oller, String vin, String hetvin, String spritsvak, String spritsterk, int drikketid, int vekt) {
        int antOl = Integer.parseInt(oller);
        int antVin = Integer.parseInt(vin);
        int antHetvin = Integer.parseInt(hetvin);
        int antSpritsvak = Integer.parseInt(spritsvak);
        int antSpritsterk = Integer.parseInt(spritsterk);
        int vektForhold =(int)Math.round(vekt * 0.50);
        Double promilleGrunnlag = (antOl*18.0)+(antVin*14.4)+(antHetvin*13.2)+(antSpritsvak*13.8)+(antSpritsterk*19.2);
        Double promille = (promilleGrunnlag/vektForhold)-(0.10*drikketid);
        if (promille<=0){
            Double kjor = 0.00;
            return kjor;
        }
        else {
            BigDecimal tilRunding = new BigDecimal(promille).setScale(3, RoundingMode.HALF_EVEN);
            promille = tilRunding.doubleValue();
            promille = Math.round(promille*100)/100.0d;
            return promille;
        }
    }
    public void sendResultat() {
        Intent intent = new Intent(this, Resultat.class);
        intent.putExtra("promille", promille);
        startActivity(intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}


