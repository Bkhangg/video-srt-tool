import json, pathlib, sys

base = pathlib.Path('/root/.buddy/work/apk-analysis')
pairs = [line.rstrip('\n').split('\t') for line in (base / 'strings.tsv').read_text().splitlines()]
en = [json.loads(p[0]) for p in pairs]
vi = [json.loads(p[1]) for p in pairs]
n = len(en)

tr = {i: ['', '', ''] for i in range(1, n + 1)}
for f in ['tr1.tsv', 'tr2.tsv', 'tr3.tsv', 'tr4.tsv']:
    for line in (base / f).read_text().splitlines():
        if not line.strip():
            continue
        parts = line.split('\t')
        idx = int(parts[0])
        for k in range(3):
            tr[idx][k] = parts[1 + k] if 1 + k < len(parts) else ''

problems = []
for i in range(1, n + 1):
    if not any(tr[i]):
        problems.append(i)
# placeholder / whitespace sanity
for i, key in enumerate(en, 1):
    for k, name in enumerate(['zh', 'ja', 'ko']):
        val = tr[i][k]
        if key != key.strip() and val == val.strip() and val != '':
            print(f'WARN trailing/leading space lost at {i} {name}: {key!r} -> {val!r}', file=sys.stderr)
        if '%s' in key and '%s' not in val:
            print(f'WARN %s missing at {i} {name}: {key!r} -> {val!r}', file=sys.stderr)
print('missing rows:', problems, file=sys.stderr)
print('total:', n, file=sys.stderr)


def jstr(s):
    return json.dumps(s, ensure_ascii=False)


def arr(name, values):
    lines = []
    for i in range(0, len(values), 4):
        chunk = ', '.join(jstr(v) for v in values[i:i + 4])
        lines.append('        ' + chunk + ',')
    body = '\n'.join(lines)
    return f'    private static final String[] {name} = {{\n{body}\n    }};\n'


zh = [tr[i][0] for i in range(1, n + 1)]
ja = [tr[i][1] for i in range(1, n + 1)]
ko = [tr[i][2] for i in range(1, n + 1)]

src = f'''package com.buddyapps.videotool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Locale;

/* Multi-language UI: English (source strings), Chinese, Japanese, Korean, Vietnamese. */
public class Localization implements View.OnClickListener {{
    public static final String[] CODES = {{"en", "zh", "ja", "ko", "vi"}};
    public static final String[] NAMES = {{"English", "中文", "日本語", "한국어", "Tiếng Việt"}};
    public static final String[] SUBTITLE_CODES = {{"en", "vi", "zh", "zh-Hant", "ja", "ko", "fr", "de", "es", "it", "pt", "pt-BR", "ru", "uk", "th", "id", "ms", "ar", "hi", "tr", "nl", "pl", "sv", "da", "fi", "no", "cs", "el", "he", "hu", "ro"}};
    private static final String PREFS = "bkggds_ui";
    private static final String PREF_LANG = "lang";
    private static final String PREF_VI = "vietnamese";

{arr('EN', en)}
{arr('ZH', zh)}
{arr('JA', ja)}
{arr('KO', ko)}
{arr('VI', vi)}
    private static final HashMap<String, Integer> INDEX = new HashMap<>();
    private static final HashMap<String, String> ANY_TO_EN = new HashMap<>();
    private static HashMap forward;
    private static String lang = "en";
    private Activity activity;

    static {{
        for (int i = 0; i < EN.length; i++) {{
            INDEX.put(EN[i], Integer.valueOf(i));
            ANY_TO_EN.put(EN[i], EN[i]);
            if (ZH[i].length() > 0) {{
                ANY_TO_EN.put(ZH[i], EN[i]);
            }}
            if (JA[i].length() > 0) {{
                ANY_TO_EN.put(JA[i], EN[i]);
            }}
            if (KO[i].length() > 0) {{
                ANY_TO_EN.put(KO[i], EN[i]);
            }}
            if (VI[i].length() > 0) {{
                ANY_TO_EN.put(VI[i], EN[i]);
            }}
        }}
        rebuild();
    }}

    private static String[] table(String str) {{
        if ("zh".equals(str)) {{
            return ZH;
        }}
        if ("ja".equals(str)) {{
            return JA;
        }}
        if ("ko".equals(str)) {{
            return KO;
        }}
        if ("vi".equals(str)) {{
            return VI;
        }}
        return EN;
    }}

    private static void rebuild() {{
        String[] strArr = table(lang);
        HashMap hashMap = new HashMap();
        for (int i = 0; i < EN.length; i++) {{
            hashMap.put(EN[i], strArr[i]);
        }}
        forward = hashMap;
    }}

    public static String validCode(String str) {{
        if (str != null) {{
            for (int i = 0; i < CODES.length; i++) {{
                if (CODES[i].equals(str)) {{
                    return str;
                }}
            }}
        }}
        return "en";
    }}

    public static String current() {{
        return lang;
    }}

    public static int index() {{
        for (int i = 0; i < CODES.length; i++) {{
            if (CODES[i].equals(lang)) {{
                return i;
            }}
        }}
        return 0;
    }}

    public static String name() {{
        return NAMES[index()];
    }}

    public static boolean isCjk() {{
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }}

    public static Locale locale() {{
        return new Locale(lang);
    }}

    /* Any language tag written in the current UI language, e.g. "en" -> "Vietnamese" on English UI. */
    public static String languageLabel(String str) {{
        if (str == null || str.length() == 0) {{
            return "";
        }}
        String str2 = str;
        try {{
            str2 = Locale.forLanguageTag(str).getDisplayName(locale());
        }} catch (Exception e) {{
            str2 = str;
        }}
        if (str2 == null || str2.length() == 0) {{
            str2 = str;
        }}
        if (str2.length() > 0 && str2.charAt(0) >= 'a' && str2.charAt(0) <= 'z') {{
            str2 = Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
        }}
        return str2;
    }}

    /* Language names written in the current UI language: English UI shows "Vietnamese". */
    public static String displayName(String str) {{
        int i = -1;
        for (int j = 0; j < CODES.length; j++) {{
            if (CODES[j].equalsIgnoreCase(str)) {{
                i = j;
            }}
        }}
        String str2 = languageLabel(str);
        if (i >= 0 && str2.equalsIgnoreCase(str)) {{
            str2 = NAMES[i];
        }}
        return str2;
    }}

    public static String[] languageNames() {{
        String[] strArr = new String[CODES.length];
        for (int i = 0; i < CODES.length; i++) {{
            strArr[i] = displayName(CODES[i]);
        }}
        return strArr;
    }}

    public static String[] subtitleLabels() {{
        String[] strArr = new String[SUBTITLE_CODES.length];
        for (int i = 0; i < SUBTITLE_CODES.length; i++) {{
            strArr[i] = SUBTITLE_CODES[i] + " · " + displayName(SUBTITLE_CODES[i]);
        }}
        return strArr;
    }}

    public static int subtitleIndex(String str) {{
        for (int i = 0; i < SUBTITLE_CODES.length; i++) {{
            if (SUBTITLE_CODES[i].equalsIgnoreCase(str)) {{
                return i;
            }}
        }}
        return 0;
    }}

    public static void setLanguage(Activity activity, String str) {{
        lang = validCode(str);
        SharedPreferences.Editor edit = activity.getSharedPreferences(PREFS, 0).edit();
        edit.putString(PREF_LANG, lang);
        edit.putBoolean(PREF_VI, "vi".equals(lang));
        edit.apply();
        rebuild();
    }}

    public Localization(Activity activity) {{
        this.activity = activity;
    }}

    public static void footer(Activity activity, android.widget.LinearLayout linearLayout) {{
        AppSettings.footer(activity, linearLayout);
    }}

    public static void init(Activity activity) {{
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS, 0);
        String string = sharedPreferences.getString(PREF_LANG, null);
        if (string == null) {{
            string = sharedPreferences.getBoolean(PREF_VI, false) ? "vi" : "en";
        }}
        lang = validCode(string);
        rebuild();
        Window window = activity.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(-1));
        if (Build.VERSION.SDK_INT >= 29) {{
            window.getDecorView().setForceDarkAllowed(false);
            window.setNavigationBarContrastEnforced(false);
            window.setStatusBarContrastEnforced(false);
        }}
    }}

    public static void refreshLabels(View view) {{
        walk(view);
    }}

    public static String tr(String str) {{
        Integer num = INDEX.get(str);
        return num == null ? str : table(lang)[num.intValue()];
    }}

    private static void walk(View view) {{
        if (view instanceof EditText) {{
            return;
        }}
        if (view instanceof TextView) {{
            TextView textView = (TextView) view;
            String cur = textView.getText().toString();
            if (cur.length() > 0) {{
                String en = ANY_TO_EN.get(cur);
                Object obj = en == null ? null : forward.get(en);
                if (obj != null) {{
                    textView.setText((String) obj);
                }}
            }}
        }}
        if (view instanceof ViewGroup) {{
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {{
                walk(viewGroup.getChildAt(i));
            }}
        }}
    }}

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {{
        int next = (index() + 1) % CODES.length;
        setLanguage(this.activity, CODES[next]);
        walk(this.activity.getWindow().getDecorView());
        ((MainActivity) this.activity).refreshLanguage();
    }}
}}
'''
(base / 'src/sources/com/buddyapps/videotool/Localization.java').write_text(src)
print('wrote Localization.java', len(src), file=sys.stderr)
