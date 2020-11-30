
package cn.cqs.im.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.ArrayMap;

import com.bingoloves.plugin_core.utils.DensityUtils;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_spa_demo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cqs.im.widget.EmojiSpan;

public class EmotionManager {

    /**
     * 表情类型标志符
     */
    public static final int EMOTION_CLASSIC_TYPE = 0x0001;//经典表情
    public static final int EMOTION_OTHER_TYPE = 0x0002;//经典表情

    /**
     * key-表情文字;
     * value-表情图片资源
     */
    public ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;
    public ArrayMap<String, Integer> EMOTION_OTHER_MAP;
    public ArrayMap<String, Integer> EMPTY_MAP;

    public ArrayMap<String, Integer> mEmojiSmileyToRes;
    public Pattern mPatternEmoji;

    private static EmotionManager emotionManager;

    public static EmotionManager getInstance() {
        if (emotionManager == null) {
            synchronized (EmotionManager.class) {
                if (emotionManager == null) {
                    emotionManager = new EmotionManager();
                }
            }
        }
        return emotionManager;
    }

    private EmotionManager() {
        mPatternEmoji = buildPatternEmoji();
        EMOTION_CLASSIC_MAP = buildEmojiSmileyToRes();

        //其它表情
        EMOTION_OTHER_MAP = new ArrayMap<>();
//        EMOTION_OTHER_MAP.put("[clay呵呵]", R.drawable.lt_001_s);
//        EMOTION_OTHER_MAP.put("[clay嘻嘻]", R.drawable.lt_002_s);
//        EMOTION_OTHER_MAP.put("[clay哈哈]", R.drawable.lt_003_s);
        //空表情
        EMPTY_MAP = new ArrayMap<>();
    }

    private final int[] EMOJI_SMILEY_RES_IDS = {
            R.mipmap.emoji_001, R.mipmap.emoji_002, R.mipmap.emoji_003, R.mipmap.emoji_004,
            R.mipmap.emoji_005, R.mipmap.emoji_006, R.mipmap.emoji_007, R.mipmap.emoji_008, R.mipmap.emoji_009, R.mipmap.emoji_010,
            R.mipmap.emoji_011, R.mipmap.emoji_012, R.mipmap.emoji_013, R.mipmap.emoji_014, R.mipmap.emoji_015, R.mipmap.emoji_016,
            R.mipmap.emoji_017, R.mipmap.emoji_018, R.mipmap.emoji_019, R.mipmap.emoji_020, R.mipmap.emoji_021, R.mipmap.emoji_022,
            R.mipmap.emoji_023, R.mipmap.emoji_024, R.mipmap.emoji_025, R.mipmap.emoji_026, R.mipmap.emoji_027, R.mipmap.emoji_028,
            R.mipmap.emoji_029, R.mipmap.emoji_030, R.mipmap.emoji_031, R.mipmap.emoji_032, R.mipmap.emoji_033, R.mipmap.emoji_034,
            R.mipmap.emoji_035, R.mipmap.emoji_036, R.mipmap.emoji_037, R.mipmap.emoji_038, R.mipmap.emoji_039, R.mipmap.emoji_040,
            R.mipmap.emoji_041, R.mipmap.emoji_042, R.mipmap.emoji_043, R.mipmap.emoji_044, R.mipmap.emoji_045, R.mipmap.emoji_046,
            R.mipmap.emoji_047, R.mipmap.emoji_048, R.mipmap.emoji_049, R.mipmap.emoji_050, R.mipmap.emoji_051, R.mipmap.emoji_052,
            R.mipmap.emoji_053, R.mipmap.emoji_054, R.mipmap.emoji_055, R.mipmap.emoji_056, R.mipmap.emoji_057, R.mipmap.emoji_058,
            R.mipmap.emoji_059, R.mipmap.emoji_060, R.mipmap.emoji_061, R.mipmap.emoji_062, R.mipmap.emoji_063, R.mipmap.emoji_064,
            R.mipmap.emoji_065, R.mipmap.emoji_066, R.mipmap.emoji_067, R.mipmap.emoji_068, R.mipmap.emoji_069, R.mipmap.emoji_070,
            R.mipmap.emoji_071, R.mipmap.emoji_072, R.mipmap.emoji_073, R.mipmap.emoji_074, R.mipmap.emoji_075, R.mipmap.emoji_076,
            R.mipmap.emoji_077, R.mipmap.emoji_078, R.mipmap.emoji_079, R.mipmap.emoji_080, R.mipmap.emoji_081, R.mipmap.emoji_082,
            R.mipmap.emoji_083, R.mipmap.emoji_084, R.mipmap.emoji_085, R.mipmap.emoji_086, R.mipmap.emoji_087, R.mipmap.emoji_088,
            R.mipmap.emoji_089, R.mipmap.emoji_090, R.mipmap.emoji_091, R.mipmap.emoji_092, R.mipmap.emoji_093, R.mipmap.emoji_094,
            R.mipmap.emoji_095, R.mipmap.emoji_096, R.mipmap.emoji_097, R.mipmap.emoji_098, R.mipmap.emoji_099, R.mipmap.emoji_100,
            R.mipmap.emoji_101, R.mipmap.emoji_102, R.mipmap.emoji_103, R.mipmap.emoji_104, R.mipmap.emoji_105, R.mipmap.emoji_106,
            R.mipmap.emoji_107, R.mipmap.emoji_108, R.mipmap.emoji_109, R.mipmap.emoji_110, R.mipmap.emoji_111, R.mipmap.emoji_112,
            R.mipmap.emoji_113, R.mipmap.emoji_114, R.mipmap.emoji_115, R.mipmap.emoji_116, R.mipmap.emoji_117, R.mipmap.emoji_118,
            R.mipmap.emoji_119, R.mipmap.emoji_120, R.mipmap.emoji_121, R.mipmap.emoji_122, R.mipmap.emoji_123, R.mipmap.emoji_124,
    };

    public final String[] EMOJI_TEXT_ARRAY = new String[]{
            "\uD83D\uDE0C", "\uD83D\uDE28", "\uD83D\uDE37", "\uD83D\uDE33", "\uD83D\uDE12", "\uD83D\uDE30", "\uD83D\uDE0A", "\uD83D\uDE03", "\uD83D\uDE1E",
            "\uD83D\uDE20", "\uD83D\uDE1C", "\uD83D\uDE0D", "\uD83D\uDE31", "\uD83D\uDE13", "\uD83D\uDE25", "\uD83D\uDE0F", "\uD83D\uDE14", "\uD83D\uDE01",
            "\uD83D\uDE09", "\uD83D\uDE23", "\uD83D\uDE16", "\uD83D\uDE2A", "\uD83D\uDE1D", "\uD83D\uDE32", "\uD83D\uDE2D", "\uD83D\uDE02", "\uD83D\uDE22",
            "☺", "\uD83D\uDE04", "\uD83D\uDE21", "\uD83D\uDE1A", "\uD83D\uDE18", "\uD83D\uDC4F", "\uD83D\uDC4D", "\uD83D\uDC4C", "\uD83D\uDC4E", "\uD83D\uDCAA",
            "\uD83D\uDC4A", "\uD83D\uDC46", "✌", "✋", "\uD83D\uDCA1", "\uD83C\uDF39", "\uD83C\uDF84", "\uD83D\uDEA4", "\uD83D\uDC8A", "\uD83D\uDEC1", "⭕",
            "❌", "❓", "❗", "\uD83D\uDEB9", "\uD83D\uDEBA", "\uD83D\uDC8B", "❤", "\uD83D\uDC94", "\uD83D\uDC98", "\uD83C\uDF81", "\uD83C\uDF89", "\uD83D\uDCA4",
            "\uD83D\uDCA8", "\uD83D\uDD25", "\uD83D\uDCA6", "⭐", "\uD83C\uDFC0", "⚽", "\uD83C\uDFBE", "\uD83C\uDF74", "\uD83C\uDF5A", "\uD83C\uDF5C", "\uD83C\uDF70",
            "\uD83C\uDF54", "\uD83C\uDF82", "\uD83C\uDF59", "☕", "\uD83C\uDF7B", "\uD83C\uDF49", "\uD83C\uDF4E", "\uD83C\uDF4A", "\uD83C\uDF53", "☀", "☔", "\uD83C\uDF19",
            "⚡", "⛄", "☁", "\uD83C\uDFC3", "\uD83D\uDEB2", "\uD83D\uDE8C", "\uD83D\uDE85", "\uD83D\uDE95", "\uD83D\uDE99", "✈", "\uD83D\uDC78", "\uD83D\uDD31",
            "\uD83D\uDC51", "\uD83D\uDC8D", "\uD83D\uDC8E", "\uD83D\uDC84", "\uD83D\uDC85", "\uD83D\uDC60", "\uD83D\uDC62", "\uD83D\uDC52", "\uD83D\uDC57", "\uD83C\uDF80",
            "\uD83D\uDC5C", "\uD83C\uDF40", "\uD83D\uDC9D", "\uD83D\uDC36", "\uD83D\uDC2E", "\uD83D\uDC35", "\uD83D\uDC2F", "\uD83D\uDC3B", "\uD83D\uDC37", "\uD83D\uDC30",
            "\uD83D\uDC24", "\uD83D\uDC2C", "\uD83D\uDC33", "\uD83C\uDFB5", "\uD83D\uDCF7", "\uD83C\uDFA5", "\uD83D\uDCBB", "\uD83D\uDCF1", "\uD83D\uDD52"
    };

    //聊天emoji表情
    private ArrayMap<String, Integer> buildEmojiSmileyToRes() {
        if (EMOJI_SMILEY_RES_IDS.length != EMOJI_TEXT_ARRAY.length) {
            //表情的数量需要和数组定义的长度一致！
            throw new IllegalStateException("Smiley resource ID/text mismatch");
        }

        ArrayMap<String, Integer> smileyToRes = new ArrayMap<String, Integer>(EMOJI_TEXT_ARRAY.length);
        for (int i = 0; i < EMOJI_TEXT_ARRAY.length; i++) {
            smileyToRes.put(EMOJI_TEXT_ARRAY[i], EMOJI_SMILEY_RES_IDS[i]);
        }

        return smileyToRes;
    }

    //构建Emoji正则表达式
    private Pattern buildPatternEmoji() {
        StringBuilder patternString = new StringBuilder(EMOJI_TEXT_ARRAY.length * 3);
        patternString.append('(');
        for (String s : EMOJI_TEXT_ARRAY) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        patternString.replace(patternString.length() - 1, patternString.length(), ")");

        return Pattern.compile(patternString.toString());
    }

    /**
     * 根据文本替换成emoji表情图片
     *
     * @param text
     * @param size dp值
     * @return
     */
    public CharSequence replaceEmoji(Context mContext, CharSequence text, int size) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = mPatternEmoji.matcher(text);
        while (matcher.find()) {
            int resId = mEmojiSmileyToRes.get(matcher.group());
            Drawable drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, DensityUtils.dp2px(mContext,size),DensityUtils.dp2px(mContext,size));//这里设置图片的大小
            EmojiSpan imageSpan = new EmojiSpan(drawable);
            builder.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName     名称
     * @return
     */
    public int getImgByName(int EmotionType, String imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                integer = EMOTION_CLASSIC_MAP.get(imgName);
                break;
            case EMOTION_OTHER_TYPE:
                integer = EMOTION_OTHER_MAP.get(imgName);
                break;
            default:
                LogUtils.e("the emojiMap is null!! Handle Yourself ");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     *
     * @param EmotionType
     * @return
     */
    public ArrayMap<String, Integer> getEmojiMap(int EmotionType) {
        ArrayMap EmojiMap = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                EmojiMap = EMOTION_CLASSIC_MAP;
                break;
            case EMOTION_OTHER_TYPE:
                EmojiMap = EMOTION_OTHER_MAP;
                break;
            default:
                EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }
}
