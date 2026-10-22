package com.rosary.maybe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private SharedPreferences prefs;

    private int count = 0;
    private int goal = 33;
    private int dhikrIndex = 0;

    private TextView counter;
    private TextView dhikrText;
    private TextView progressText;
    private TextView audioStatus;

    private Spinner dhikrSpinner;
    private Spinner goalSpinner;
    private Spinner surahSpinner;
    private Spinner reciterSpinner;

    private Button tasbeehButton;
    private Button playButton;

    private MediaPlayer mediaPlayer;

    private final int background = Color.rgb(8, 17, 15);
    private final int cardColor = Color.rgb(19, 36, 29);
    private final int green = Color.rgb(32, 180, 105);
    private final int lightGreen = Color.rgb(178, 255, 208);
    private final int white = Color.WHITE;
    private final int gray = Color.rgb(174, 193, 181);

    private final int[] goals = {33, 100, 1000, 0};

    private final String[] goalNames = {
        "33 تسبيحة",
        "100 تسبيحة",
        "1000 تسبيحة",
        "بدون هدف"
    };

    private final String[] adhkar = {
        "سبحان الله",
        "الحمد لله",
        "الله أكبر",
        "أستغفر الله",
        "لا إله إلا الله",
        "سبحان الله وبحمده",
        "لا حول ولا قوة إلا بالله",
        "اللهم صلِّ وسلم على نبينا محمد"
    };

    private final String[] hadiths = {
        "إنما الأعمال بالنيات، وإنما لكل امرئ ما نوى. — متفق عليه",
        "من كان يؤمن بالله واليوم الآخر فليقل خيرًا أو ليصمت. — متفق عليه",
        "الكلمة الطيبة صدقة. — متفق عليه",
        "لا يؤمن أحدكم حتى يحب لأخيه ما يحب لنفسه. — متفق عليه",
        "يسروا ولا تعسروا، وبشروا ولا تنفروا. — متفق عليه",
        "من لا يرحم لا يُرحم. — متفق عليه",
        "الطهور شطر الإيمان. — رواه مسلم",
        "الدين النصيحة. — رواه مسلم"
    };

    private final String[] surahNames = {
        "الفاتحة", "البقرة", "آل عمران", "النساء",
        "المائدة", "الأنعام", "الأعراف", "الأنفال",
        "التوبة", "يونس", "هود", "يوسف",
        "الرعد", "إبراهيم", "الحجر", "النحل",
        "الإسراء", "الكهف", "مريم", "طه",
        "الأنبياء", "الحج", "المؤمنون", "النور",
        "الفرقان", "الشعراء", "النمل", "القصص",
        "العنكبوت", "الروم", "لقمان", "السجدة",
        "الأحزاب", "سبأ", "فاطر", "يس",
        "الصافات", "ص", "الزمر", "غافر",
        "فصلت", "الشورى", "الزخرف", "الدخان",
        "الجاثية", "الأحقاف", "محمد", "الفتح",
        "الحجرات", "ق", "الذاريات", "الطور",
        "النجم", "القمر", "الرحمن", "الواقعة",
        "الحديد", "المجادلة", "الحشر", "الممتحنة",
        "الصف", "الجمعة", "المنافقون", "التغابن",
        "الطلاق", "التحريم", "الملك", "القلم",
        "الحاقة", "المعارج", "نوح", "الجن",
        "المزمل", "المدثر", "القيامة", "الإنسان",
        "المرسلات", "النبأ", "النازعات", "عبس",
        "التكوير", "الانفطار", "المطففين", "الانشقاق",
        "البروج", "الطارق", "الأعلى", "الغاشية",
        "الفجر", "البلد", "الشمس", "الليل",
        "الضحى", "الشرح", "التين", "العلق",
        "القدر", "البينة", "الزلزلة", "العاديات",
        "القارعة", "التكاثر", "العصر", "الهمزة",
        "الفيل", "قريش", "الماعون", "الكوثر",
        "الكافرون", "النصر", "المسد", "الإخلاص",
        "الفلق", "الناس"
    };

    private final ArrayList<Reciter> reciters = new ArrayList<>();
    private final ArrayList<String> reciterNames = new ArrayList<>();

    private int selectedSurah = 1;
    private int selectedReciter = -1;

    private boolean isPreparing = false;

    private static class Reciter {
        String name;
        String server;
        String surahList;

        Reciter(String name, String server, String surahList) {
            this.name = name;
            this.server = server;
            this.surahList = surahList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("misbaha_data", MODE_PRIVATE);

        count = Math.max(0, prefs.getInt("count", 0));
        goal = prefs.getInt("goal", 33);
        dhikrIndex = prefs.getInt("dhikr", 0);

        if (goal != 33 && goal != 100 &&
            goal != 1000 && goal != 0) {
            goal = 33;
        }

        if (dhikrIndex < 0 || dhikrIndex >= adhkar.length) {
            dhikrIndex = 0;
        }

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        buildInterface();
        updateDisplay();
        loadReciters();
    }

    private void buildInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(background);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(18), dp(22), dp(18), dp(25));

        scroll.addView(root);

        TextView title = makeText(
            "📿 المسبحة الإلكترونية", 26, white, true
        );
        title.setGravity(Gravity.CENTER);
        root.addView(title, fullWidth());

        TextView subtitle = makeText(
            "سبّح واطمئن قلبك", 16, lightGreen, false
        );
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams = fullWidth();
        subtitleParams.topMargin = dp(8);
        subtitleParams.bottomMargin = dp(20);
        root.addView(subtitle, subtitleParams);

        // قسم الأذكار
        LinearLayout dhikrCard = makeCard();

        addLabel(dhikrCard, "اختر الذكر");

        dhikrSpinner = makeSpinner(adhkar);
        dhikrSpinner.setSelection(dhikrIndex);
        dhikrCard.addView(dhikrSpinner, fullWidth());

        dhikrSpinner.setOnItemSelectedListener(
            new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    android.widget.AdapterView<?> parent,
                    View view, int position, long id
                ) {
                    dhikrIndex = position;
                    saveData();
                    updateDisplay();
                }

                @Override
                public void onNothingSelected(
                    android.widget.AdapterView<?> parent
                ) {}
            }
        );

        root.addView(dhikrCard, withBottom(16));

        // قسم العداد
        LinearLayout counterCard = makeCard();
        counterCard.setGravity(Gravity.CENTER_HORIZONTAL);

        addLabel(counterCard, "عدد التسبيحات");

        counter = makeText("000", 58, lightGreen, true);
        counter.setGravity(Gravity.CENTER);
        counterCard.addView(counter, fullWidth());

        dhikrText = makeText(adhkar[dhikrIndex], 21, white, true);
        dhikrText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams dhikrParams = fullWidth();
        dhikrParams.topMargin = dp(8);
        counterCard.addView(dhikrText, dhikrParams);

        progressText = makeText("", 14, lightGreen, false);
        progressText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams progressParams = fullWidth();
        progressParams.topMargin = dp(12);
        progressParams.bottomMargin = dp(12);
        counterCard.addView(progressText, progressParams);

        tasbeehButton = new Button(this);
        tasbeehButton.setText("سَبِّح");
        tasbeehButton.setTextSize(27);
        tasbeehButton.setTextColor(white);
        tasbeehButton.setAllCaps(false);
        tasbeehButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tasbeehButton.setBackground(rounded(green, 150));

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(dp(205), dp(205));

        buttonParams.gravity = Gravity.CENTER;
        counterCard.addView(tasbeehButton, buttonParams);

        tasbeehButton.setOnClickListener(v -> addTasbeeh());

        Button resetButton = makeButton(
            "تصفير العداد", Color.rgb(120, 45, 45)
        );

        LinearLayout.LayoutParams resetParams = fullWidth();
        resetParams.topMargin = dp(12);
        counterCard.addView(resetButton, resetParams);

        resetButton.setOnClickListener(v -> confirmReset());

        root.addView(counterCard, withBottom(16));

        // الأهداف
        LinearLayout goalCard = makeCard();

        addLabel(goalCard, "🎯 هدف التسبيح");

        goalSpinner = makeSpinner(goalNames);
        goalSpinner.setSelection(getGoalIndex());
        goalCard.addView(goalSpinner, fullWidth());

        goalSpinner.setOnItemSelectedListener(
            new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    android.widget.AdapterView<?> parent,
                    View view, int position, long id
                ) {
                    goal = goals[position];
                    saveData();
                    updateDisplay();
                }

                @Override
                public void onNothingSelected(
                    android.widget.AdapterView<?> parent
                ) {}
            }
        );

        root.addView(goalCard, withBottom(16));

        // قسم القرآن الكريم
        LinearLayout quranCard = makeCard();

        addLabel(quranCard, "📖 القرآن الكريم");

        TextView surahLabel = makeText(
            "اختر السورة", 15, gray, true
        );
        quranCard.addView(surahLabel, fullWidth());

        surahSpinner = makeSpinner(surahNames);
        quranCard.addView(surahSpinner, withBottom(10));

        surahSpinner.setOnItemSelectedListener(
            new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    android.widget.AdapterView<?> parent,
                    View view, int position, long id
                ) {
                    selectedSurah = position + 1;
                }

                @Override
                public void onNothingSelected(
                    android.widget.AdapterView<?> parent
                ) {}
            }
        );

        TextView reciterLabel = makeText(
            "اختر القارئ", 15, gray, true
        );
        quranCard.addView(reciterLabel, withBottom(5));

        reciterSpinner = makeSpinner(
            new String[]{"جارٍ تحميل القراء..."}
        );
        quranCard.addView(reciterSpinner, withBottom(10));

        audioStatus = makeText(
            "اتصل بالإنترنت لتحميل قائمة القراء",
            13, lightGreen, false
        );
        audioStatus.setGravity(Gravity.CENTER);
        quranCard.addView(audioStatus, withBottom(8));

        playButton = makeButton("▶ تشغيل التلاوة", green);
        quranCard.addView(playButton, fullWidth());

        playButton.setOnClickListener(v -> playSelectedSurah());

        Button stopButton = makeButton(
            "■ إيقاف التلاوة", cardColor
        );

        LinearLayout.LayoutParams stopParams = fullWidth();
        stopParams.topMargin = dp(8);
        quranCard.addView(stopButton, stopParams);

        stopButton.setOnClickListener(v -> stopAudio());

        root.addView(quranCard, withBottom(16));

        // قسم الأحاديث
        LinearLayout hadithCard = makeCard();

        addLabel(hadithCard, "📚 أحاديث نبوية");

        TextView hadithText = makeText(
            hadiths[0], 17, white, false
        );
        hadithText.setGravity(Gravity.CENTER);
        hadithText.setPadding(dp(8), dp(12), dp(8), dp(12));

        hadithCard.addView(hadithText, fullWidth());

        Button nextHadith = makeButton(
            "حديث آخر", green
        );
        hadithCard.addView(nextHadith, fullWidth());

        final int[] hadithIndex = {0};

        nextHadith.setOnClickListener(v -> {
            hadithIndex[0] =
                (hadithIndex[0] + 1) % hadiths.length;

            hadithText.setText(hadiths[hadithIndex[0]]);
        });

        root.addView(hadithCard, withBottom(16));

        TextView footer = makeText(
            "يتم حفظ عدد التسبيحات تلقائيًا",
            12, gray, false
        );

        footer.setGravity(Gravity.CENTER);
        root.addView(footer, fullWidth());

        setContentView(scroll);
    }

    private void loadReciters() {
        audioStatus.setText("جارٍ تحميل القراء...");

        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                    "https://www.mp3quran.net/api/v3/reciters?language=ar"
                );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    throw new Exception("HTTP " + responseCode);
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        connection.getInputStream(), "UTF-8"
                    )
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject root = new JSONObject(response.toString());
                JSONArray array = root.optJSONArray("reciters");

                if (array == null) {
                    throw new Exception("قائمة القراء غير متاحة");
                }

                ArrayList<Reciter> found = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);

                    if (item == null) continue;

                    String name = item.optString("name", "قارئ");

                    JSONArray moshafs = item.optJSONArray("moshaf");

                    if (moshafs == null) continue;

                    for (int j = 0; j < moshafs.length(); j++) {
                        JSONObject moshaf =
                            moshafs.optJSONObject(j);

                        if (moshaf == null) continue;

                        String server = moshaf.optString("server", "");
                        String surahList =
                            moshaf.optString("surah_list", "");

                        if (!server.isEmpty()) {
                            found.add(
                                new Reciter(name, server, surahList)
                            );
                        }
                    }
                }

                runOnUiThread(() -> {
                    reciters.clear();
                    reciters.addAll(found);

                    reciterNames.clear();

                    for (Reciter reciter : reciters) {
                        reciterNames.add(reciter.name);
                    }

                    if (reciterNames.isEmpty()) {
                        audioStatus.setText(
                            "لم يتم العثور على قراء."
                        );
                        return;
                    }

                    ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            reciterNames
                        );

                    adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                    );

                    reciterSpinner.setAdapter(adapter);

                    audioStatus.setText(
                        "تم تحميل " + reciters.size() + " قراءة متاحة"
                    );
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    audioStatus.setText(
                        "تعذر تحميل القراء. تحقق من الإنترنت."
                    );

                    showMessage(
                        "تعذر تحميل قائمة القراء. حاول مجددًا."
                    );
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void playSelectedSurah() {
        if (reciters.isEmpty()) {
            showMessage(
                "انتظر تحميل القراء وتأكد من اتصال الإنترنت."
            );
            return;
        }

        int position = reciterSpinner.getSelectedItemPosition();

        if (position < 0 || position >= reciters.size()) {
            showMessage("اختر قارئًا أولًا.");
            return;
        }

        Reciter reciter = reciters.get(position);

        if (!reciter.surahList.isEmpty()) {
            String[] available = reciter.surahList.split(",");
            boolean found = false;

            for (String number : available) {
                if (number.trim().equals(
                    String.valueOf(selectedSurah)
                )) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                showMessage(
                    "هذه السورة غير متاحة في القراءة المختارة. " +
                    "جرّب قارئًا آخر."
                );
                return;
            }
        }

        String surahNumber = String.format(
            Locale.US, "%03d", selectedSurah
        );

        String server = reciter.server;

        if (!server.endsWith("/")) {
            server += "/";
        }

        String audioUrl = server + surahNumber + ".mp3";

        releasePlayer();

        audioStatus.setText(
            "جارٍ تجهيز سورة " + surahNames[selectedSurah - 1]
        );

        playButton.setEnabled(false);
        isPreparing = true;

        try {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    )
                    .build()
            );

            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> {
                isPreparing = false;
                playButton.setEnabled(true);

                mp.start();

                audioStatus.setText(
                    "يتم تشغيل سورة " +
                    surahNames[selectedSurah - 1] +
                    " بصوت " + reciter.name
                );

                playButton.setText("▶ إعادة التشغيل");
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                audioStatus.setText("انتهت التلاوة");
                playButton.setText("▶ تشغيل التلاوة");
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPreparing = false;
                playButton.setEnabled(true);

                audioStatus.setText("تعذر تشغيل هذه التلاوة");
                releasePlayer();

                showMessage(
                    "تعذر تشغيل الملف الصوتي. " +
                    "جرّب سورة أو قارئًا آخر."
                );

                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            isPreparing = false;
            playButton.setEnabled(true);
            releasePlayer();

            audioStatus.setText("تعذر تشغيل التلاوة");
            showMessage("حدث خطأ أثناء تجهيز التلاوة.");
        }
    }

    private void stopAudio() {
        releasePlayer();

        if (audioStatus != null) {
            audioStatus.setText("تم إيقاف التلاوة");
        }

        if (playButton != null) {
            playButton.setText("▶ تشغيل التلاوة");
            playButton.setEnabled(true);
        }
    }

    private void releasePlayer() {
        isPreparing = false;

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {}

            try {
                mediaPlayer.reset();
            } catch (Exception ignored) {}

            try {
                mediaPlayer.release();
            } catch (Exception ignored) {}

            mediaPlayer = null;
        }
    }

    private void addTasbeeh() {
        if (count < Integer.MAX_VALUE) {
            count++;
        }

        saveData();
        updateDisplay();

        if (goal > 0 && count == goal) {
            new AlertDialog.Builder(this)
                .setTitle("ما شاء الله!")
                .setMessage(
                    "وصلت إلى هدفك: " + goal + " تسبيحة."
                )
                .setPositiveButton("متابعة", null)
                .setNegativeButton(
                    "تصفير العداد",
                    (dialog, which) -> {
                        count = 0;
                        saveData();
                        updateDisplay();
                    }
                )
                .show();
        }
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
            .setTitle("تصفير العداد؟")
            .setMessage(
                "هل أنت متأكد أنك تريد تصفير عدد التسبيحات؟"
            )
            .setPositiveButton(
                "نعم، صفّر",
                (dialog, which) -> {
                    count = 0;
                    saveData();
                    updateDisplay();
                }
            )
            .setNegativeButton("إلغاء", null)
            .show();
    }

    private void updateDisplay() {
        if (counter == null) return;

        counter.setText(
            String.format(Locale.getDefault(), "%03d", count)
        );

        dhikrText.setText(adhkar[dhikrIndex]);

        if (goal == 0) {
            progressText.setText("التسبيح بدون هدف محدد");
        } else if (count >= goal) {
            progressText.setText(
                "اكتمل الهدف • يمكنك المتابعة"
            );
        } else {
            progressText.setText(
                "باقي " + (goal - count) +
                " للوصول إلى الهدف"
            );
        }
    }

    private void saveData() {
        if (prefs == null) return;

        prefs.edit()
            .putInt("count", count)
            .putInt("goal", goal)
            .putInt("dhikr", dhikrIndex)
            .apply();
    }

    private int getGoalIndex() {
        for (int i = 0; i < goals.length; i++) {
            if (goals[i] == goal) return i;
        }

        return 0;
    }

    private void addLabel(LinearLayout parent, String text) {
        TextView label = makeText(text, 17, white, true);
        label.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = fullWidth();
        params.bottomMargin = dp(10);

        parent.addView(label, params);
    }

    private Spinner makeSpinner(String[] items) {
        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            items
        );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        return spinner;
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(rounded(cardColor, 22));
        card.setElevation(dp(3));

        return card;
    }

    private TextView makeText(
        String value, int size, int color, boolean bold
    ) {
        TextView text = new TextView(this);

        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);

        if (bold) {
            text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return text;
    }

    private Button makeButton(String value, int color) {
        Button button = new Button(this);

        button.setText(value);
        button.setTextSize(15);
        button.setTextColor(white);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(color, 15));

        return button;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();

        shape.setColor(color);
        shape.setCornerRadius(dp(radius));

        return shape;
    }

    private LinearLayout.LayoutParams fullWidth() {
        return new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams withBottom(int bottom) {
        LinearLayout.LayoutParams params = fullWidth();
        params.bottomMargin = dp(bottom);

        return params;
    }

    private int dp(float value) {
        return (int) (
            value *
            getResources().getDisplayMetrics().density + 0.5f
        );
    }

    // هذه الدالة موجودة لحل أخطاء showMessage السابقة
    private void showMessage(String message) {
        Toast.makeText(
            MainActivity.this,
            message,
            Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
}