package no.nord.promillekalkulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Resultat extends AppCompatActivity {

    private Double promille;
    private int antTimer;
    private TextView tiden;
    private TextView resultatet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        promille = intent.getDoubleExtra("promille", 0);
        resultatet = (TextView) findViewById(R.id.resultat);
        String textVerdi= Double.toString(promille);
        resultatet.setText(textVerdi);
        tiden = (TextView) findViewById(R.id.tid);
        Double tidTilNull = promille;

        while (tidTilNull>=0) {
            tidTilNull= tidTilNull-0.10;
            antTimer++;
            if (tidTilNull<=0) {
                String antall= Integer.toString(antTimer);
                tiden.setText(antall);
            }
        }
    }
}
