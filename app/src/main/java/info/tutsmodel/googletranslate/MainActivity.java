package info.tutsmodel.googletranslate;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt = (EditText) findViewById(R.id.editText);
                String input = edt.getText().toString();
                new Translate().execute(input);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt = (EditText) findViewById(R.id.editText);
                String input = edt.getText().toString();
                speech(input,"en");
            }
        });
    }

    class Translate extends AsyncTask<String,String,String>{
        String string = "";
        String toLang = "vi"; // ngôn ngữ cần dịch
        String fromLang = ""; // để trống là sẽ ở chế chộ phát hiện ngôn ngữ
        public Translate(String toLang,String fromLang){
            this.toLang = toLang;
            this.fromLang = fromLang;
        }
        public Translate(){}
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            BufferedReader bufferedReader = null;

            try {
                String translateUrl =
                        "https://translate.google.com/translate_a/single?client=t&sl=auto"
                                +"&tl="+toLang+"&hl="+fromLang
                                +"&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&dt=at&ie="
                                +"UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=5&tk=822010|693481&q="
                                + URLEncoder.encode(params[0],"UTF-8");
                URL url = new URL(translateUrl);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "Something Else");
                is = urlConnection.getInputStream();

                bufferedReader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    string += line;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String s) {

            TextView textView = (TextView) findViewById(R.id.textView);
            s = s.replace("[[","\n").replace("]]","\n").replace("],[","\n");

            textView.setText(s);
            super.onPostExecute(s);
        }
    }
    public void speech(final String text, final String lang){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlSpeed = "https://translate.google.com/translate_tts?ie=UTF-8&q="
                            +URLEncoder.encode(text,"UTF-8")+"&tl="+lang+"&total=1&idx=0&textlen="
                            +text.length()+"&tk=822010|693481&client=t&prev=input";

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(MainActivity.this, Uri.parse(urlSpeed));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
