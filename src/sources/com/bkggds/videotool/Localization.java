package com.bkggds.videotool;

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
public class Localization implements View.OnClickListener {
    public static final String[] CODES = {"en", "zh", "ja", "ko", "vi"};
    public static final String[] NAMES = {"English", "中文", "日本語", "한국어", "Tiếng Việt"};
    private static final String[] EN_NAMES = {"English", "Chinese", "Japanese", "Korean", "Vietnamese"};
    public static final String[] SUBTITLE_CODES = {"en", "vi", "zh", "zh-Hant", "ja", "ko", "fr", "de", "es", "it", "pt", "pt-BR", "ru", "uk", "th", "id", "ms", "ar", "hi", "tr", "nl", "pl", "sv", "da", "fi", "no", "cs", "el", "he", "hu", "ro"};
    private static final String PREFS = "bkggds_ui";
    private static final String PREF_LANG = "lang";
    private static final String PREF_VI = "vietnamese";

    private static final String[] EN = {
        "Save file", "Help", "Import video", "Extract subtitles",
        "Edit subtitles", "Write the result to Downloads", "Formats and notes", "Pick an MKV or MP4",
        "Read embedded tracks", "Review the SRT text", "Paste or load an SRT", "Video Subtitle Tool",
        "Extract, merge and save subtitles", "  ·  MKV / MP4  ·  SRT output", "Select a video first.", "No text subtitle track to export.",
        "Extracting ", " tracks...", "That track is image based (PGS / VobSub) and cannot become text.", "Nothing to save yet. Extract subtitles first.",
        "Pick the folder where the subtitle files will be saved.", "Extract", "Saving ", "Reading subtitle tracks...",
        " cues in SRT format", "Nothing extracted yet", "Extracted subtitles", "You can edit the text (including the time codes) before saving.",
        "No subtitles yet. Select a video and run Extract subtitles first.", "Save", "Jump to the save screen with this text.", "Go to Save file",
        "Other languages in this video", "Tap a language to extract it into the editor.", "Save this subtitle", "Save this subtitle as SRT",
        "Save as... (choose a folder)", "Save every language (", " files)", "Extract embedded subtitles",
        "Every subtitle track inside the video is listed below. Pick the language you want.", "Video", "No video selected", "Select a video first from Import video.",
        "Reading tracks...", "Read subtitle tracks", "Tracks are listed automatically. If nothing appears, tap Read subtitle tracks.", " subtitle track",
        " found", "Extracting...", "Extract selected track", "Extract all ",
        " tracks to Downloads", "This video only has image based subtitles (PGS / VobSub). They cannot be converted to text.", "Result", " subtitle cues extracted",
        "Review subtitles", "Formats", "Video: MKV (Matroska) and MP4.", "Subtitles read: SRT, ASS/SSA, WebVTT, mov_text.",
        "Not supported: PGS and VobSub, they are images.", "How it works", "1. Import a video.", "2. Extract the embedded subtitle track.",
        "4. Save that subtitle, or the whole file, with Extract all tracks.", "Edit subtitles has Save this subtitle and Save as... buttons.", "Notes", "Everything runs on the phone, nothing is uploaded.",
        "Saving uses the system Downloads folder, no storage permission needed.", "Video file", "Choose an MKV or MP4 file from this device.", "Selected file",
        "No file selected", "Select video from device", "Continue to extract subtitles", "Preview",
        "Text subtitles only: SRT, ASS/SSA, WebVTT and mov_text. Image based subtitles such as PGS or VobSub cannot be converted to text.", "Save subtitle file", "The result is written to Downloads as an SRT file.", "File name",
        "Save all ", " language tracks", "Save only the subtitle above", "Save to Downloads",
        "Save all tracks, choose a folder...", " text tracks in this video. Each language is saved as its own file.", " cues ready to save.", "Load or paste another SRT. It is merged with the extracted one and sorted by time.",
        "Select SRT file", "Clear", "If the second track is an SRT, its cues are merged and sorted by time. Plain text is appended under every extracted cue.", "Extracting track ",
        " of ", "Could not extract any track. Try one track at a time.", "Saved ", " file",
        " to Downloads:", " track", " could not be read.", " SRT file",
        " saved to Downloads.", " SRT files", "Extracted ", " cues from ",
        " To keep other languages, use Extract all ", " tracks.", "Could not read text from that track.", "Error: ",
        "Saved: Downloads/", " cues)", "Saved to Downloads/", "Saved to Downloads",
        "Save failed.", "Loaded: ", "Could not read that file: ", "Saved to the location you picked.",
        "File saved.", "Could not write the file. Try again.", " to the folder you picked:", " saved.",
        "No subtitle tracks found in this video.", " found.", "Second subtitle cleared.", "Selected: ",
        "Nothing extracted yet. Run Extract subtitles...", "Pick a video from this device to begin.", "s", "Language changed.",
        "Language: English  •  Tiếng Việt", "Track ", " (image)", " cues",
        "Add a new SRT to the video", "3. Add a new SRT to the video.", "Unknown language", "Loading preview...",
        "Preview unavailable. This device may not support the video codec. Subtitle extraction is still available.", "Top", "End", "Full screen",
        "Done", "Start of subtitles", "End of subtitles", "Clear subtitles?",
        "This will remove the text in this editor.", "Cancel", "Choose another video", "Settings",
        "Language", "Appearance", "Light", "Dark",
        "Apply", "Preferences updated.", "Play", "Pause",
        "Seek video", "Close", "Copy", "Copied",
        "Source text changed", "Replace", "Paste SRT text to merge with the extracted subtitles...", "Check one or more text tracks to extract. Each track remains a separate SRT.",
        "Selected tracks", "Extract selected tracks", "Select all", "Deselect all",
        "Scroll the track list to see more.", "Extracting subtitles", "Extracted tracks", "Could not extract the selected tracks. Your existing subtitles were kept.",
        "Some tracks could not be read", "Choose an extracted track to edit", "Save extracted tracks to a folder", "Add SRT",
        "Could not open the folder picker.", "Saving extracted tracks", "Saved files", "Some files could not be saved",
        "You are on the first screen.", "Subtitle tracks found", "Could not read subtitle tracks. Please try again.", "Font",
        "Default font", "Font preview", "Please import a video first.", "Track name",
        "Subtitle language code", "Your SRT Language dialogue", "For example: vi / vie, en / eng", "Output filename",
        "The name and language are saved in the filename only. SRT files cannot contain embedded track metadata.", "Enter a track name and a valid language code.", "Save this added SRT", "Use with extracted subtitles",
        "Enter a track name.", "Use a language code such as vi/vie or en/eng.", "Paste or load valid SRT text first.", "Extract subtitles first, or save this SRT separately.",
        "Could not open the file picker.", "Saving SRT", "Saved SRT", "Embed SRT in MKV / MP4",
        "Video export embeds the track name and language. SRT-only export stores them in the filename. The original video is kept.", "A video export is already in progress.", "Choose a different output file. The original video cannot be overwritten.", "Preparing video export",
        "Preparing a local video copy...", "Reading video information...", "Embedding subtitles", "Embedding subtitle track...",
        "Checking the new video...", "Saving the new video...", "Video saved", "Video saved with the new subtitle track.",
        "Video export failed", "Video export failed. The original video was not changed.", "The video engine is unavailable on this device.", "Only MKV and MP4 videos are supported.",
        "Not enough free storage to export this video.", "Could not create temporary storage.", "Could not open the video.", "Could not read this video. Try importing a local MKV or MP4 file.",
        "The imported file has no video track.", "Could not embed subtitles. This video may contain tracks that cannot be copied into its container.", "The new video could not be verified.", "The subtitle metadata could not be verified.",
        "The subtitle format could not be verified.", "Could not write the output video.", "TXT recognized as SRT. Save a new .srt file or embed it in the video.", "The subtitle timing is outside the video. Adjust the SRT timestamps first.",
        "The subtitle text could not be verified.", "Subtitle track", "No subtitles", "Track",
        "Audio track", "Playback speed", "Aspect ratio", "Fullscreen",
        "Exit fullscreen", "Fit", "Fill", "Stretch",
        "Back 10 seconds", "Forward 10 seconds", "Volume", "Brightness",
        "No audio tracks",
    };

    private static final String[] ZH = {
        "保存文件", "帮助", "导入视频", "提取字幕",
        "编辑字幕", "将结果保存到“下载”", "格式与说明", "选择 MKV 或 MP4",
        "读取内嵌字幕轨", "检查 SRT 文本", "粘贴或加载 SRT", "视频字幕工具",
        "提取、合并并保存字幕", "  ·  MKV / MP4  ·  输出 SRT", "请先选择视频。", "没有可导出的文本字幕轨。",
        "正在提取 ", " 个字幕轨...", "该字幕轨是图像格式（PGS / VobSub），无法转换为文本。", "还没有可保存的内容。请先提取字幕。",
        "选择保存字幕文件的文件夹。", "提取", "正在保存 ", "正在读取字幕轨...",
        " 条字幕（SRT 格式）", "尚未提取任何内容", "已提取的字幕", "保存前可以编辑文本（包括时间码）。",
        "还没有字幕。请选择视频并先运行“提取字幕”。", "保存", "带着这段文本跳转到保存页面。", "前往保存文件",
        "此视频中的其他语言", "点按一种语言，将其提取到编辑器。", "保存此字幕", "将此字幕保存为 SRT",
        "另存为...（选择文件夹）", "保存所有语言（", " 个文件）", "提取内嵌字幕",
        "视频中的每个字幕轨都列在下方。请选择需要的语言。", "视频", "未选择视频", "请先从“导入视频”中选择视频。",
        "正在读取字幕轨...", "读取字幕轨", "字幕轨会自动列出。如果没有显示，请点按“读取字幕轨”。", " 个字幕轨",
        " 个已找到", "正在提取...", "提取所选字幕轨", "全部提取 ",
        " 个字幕轨到“下载”", "此视频只有图像字幕（PGS / VobSub），无法转换为文本。", "结果", " 条字幕已提取",
        "查看字幕", "格式", "视频：MKV (Matroska) 和 MP4。", "可读取字幕：SRT、ASS/SSA、WebVTT、mov_text。",
        "不支持：PGS 和 VobSub（它们是图像）。", "使用方法", "1. 导入视频。", "2. 提取内嵌字幕轨。",
        "4. 保存该字幕，或用“提取全部字幕轨”保存整个文件。", "“编辑字幕”中有“保存此字幕”和“另存为...”按钮。", "说明", "全部在本机运行，不会上传任何内容。",
        "保存使用系统的“下载”文件夹，无需存储权限。", "视频文件", "从本机选择 MKV 或 MP4 文件。", "已选文件",
        "未选择文件", "从设备选择视频", "继续提取字幕", "预览",
        "仅支持文本字幕：SRT、ASS/SSA、WebVTT 和 mov_text。PGS 或 VobSub 等图像字幕无法转换为文本。", "保存字幕文件", "结果将以 SRT 文件写入“下载”。", "文件名",
        "全部保存 ", " 个语言轨", "仅保存上面的字幕", "保存到“下载”",
        "保存所有字幕轨，选择文件夹...", " 个字幕轨。每种语言保存为单独文件。", " 条字幕可保存。", "加载或粘贴另一个 SRT。它会与已提取的字幕合并，并按时间排序。",
        "选择 SRT 文件", "清除", "如果第二个轨道是 SRT，其字幕会合并并按时间排序。纯文本会附加在每个已提取字幕的下方。", "正在提取轨道 ",
        " / ", "无法提取任何字幕轨。请一次尝试一个。", "已保存 ", " 个文件",
        " 到“下载”：", " 个字幕轨", " 无法读取。", " SRT 文件",
        " 已保存到“下载”。", " SRT 文件", "已提取 ", " 条字幕，来自 ",
        " 如需保留其他语言，请使用“全部提取” ", " 个字幕轨。", "无法从该字幕轨读取文本。", "错误：",
        "已保存：下载/", " 条字幕）", "已保存到下载/", "已保存到“下载”",
        "保存失败。", "已加载：", "无法读取该文件：", "已保存到所选位置。",
        "文件已保存。", "无法写入文件。请重试。", " 到所选文件夹：", " 已保存。",
        "此视频中未找到字幕轨。", " 个已找到。", "已清除第二个字幕。", "已选择：",
        "尚未提取任何内容。请运行“提取字幕”...", "从本机选择视频开始。", "", "语言已更改。",
        "语言：English  •  Tiếng Việt", "字幕轨 ", "（图像）", " 条字幕",
        "向视频添加新的 SRT", "3. 向视频添加新的 SRT。", "未知语言", "正在加载预览...",
        "预览不可用。此设备可能不支持该视频编码。仍可提取字幕。", "顶部", "末尾", "全屏",
        "完成", "字幕开头", "字幕结尾", "清除字幕？",
        "这将删除此编辑器中的文本。", "取消", "选择其他视频", "设置",
        "语言", "外观", "浅色", "深色",
        "应用", "偏好设置已更新。", "播放", "暂停",
        "拖动视频", "关闭", "复制", "已复制",
        "源文本已更改", "替换", "粘贴 SRT 文本以与已提取的字幕合并...", "勾选一个或多个文本字幕轨进行提取。每个轨道都保存为单独的 SRT。",
        "已选字幕轨", "提取所选字幕轨", "全选", "取消全选",
        "滚动字幕轨列表查看更多。", "正在提取字幕", "已提取的字幕轨", "无法提取所选字幕轨。原有字幕已保留。",
        "部分字幕轨无法读取", "选择要编辑的已提取字幕轨", "将已提取的字幕轨保存到文件夹", "添加 SRT",
        "无法打开文件夹选择器。", "正在保存已提取的字幕轨", "已保存的文件", "部分文件无法保存",
        "你已在第一页。", "找到字幕轨", "无法读取字幕轨。请重试。", "字体",
        "默认字体", "字体预览", "请先导入视频。", "字幕轨名称",
        "字幕语言代码", "您的 SRT 语言对话", "例如：vi / vie、en / eng", "输出文件名",
        "名称和语言只保存在文件名中。SRT 文件无法包含内嵌的轨道元数据。", "请输入字幕轨名称和有效的语言代码。", "保存此添加的 SRT", "与已提取的字幕一起使用",
        "请输入字幕轨名称。", "请使用语言代码，例如 vi/vie 或 en/eng。", "请先粘贴或加载有效的 SRT 文本。", "请先提取字幕，或将此 SRT 单独保存。",
        "无法打开文件选择器。", "正在保存 SRT", "SRT 已保存", "将 SRT 内嵌到 MKV / MP4",
        "导出视频会内嵌字幕轨名称和语言。仅导出 SRT 时，这些信息保存在文件名中。原始视频会保留。", "视频导出正在进行中。", "请选择其他输出文件。原始视频不能被覆盖。", "准备导出视频",
        "正在准备本地视频副本...", "正在读取视频信息...", "正在内嵌字幕", "正在内嵌字幕轨...",
        "正在检查新视频...", "正在保存新视频...", "视频已保存", "视频已保存，包含新的字幕轨。",
        "视频导出失败", "视频导出失败。原始视频未被更改。", "此设备上视频引擎不可用。", "仅支持 MKV 和 MP4 视频。",
        "存储空间不足，无法导出此视频。", "无法创建临时存储。", "无法打开视频。", "无法读取此视频。请尝试导入本机的 MKV 或 MP4 文件。",
        "导入的文件没有视频轨。", "无法内嵌字幕。此视频可能包含无法复制到其容器中的轨道。", "无法验证新视频。", "无法验证字幕元数据。",
        "无法验证字幕格式。", "无法写入输出视频。", "已将 TXT 识别为 SRT。请保存为新的 .srt 文件或将其内嵌到视频中。", "字幕时间超出视频范围。请先调整 SRT 时间戳。",
        "无法验证字幕文本。", "字幕轨", "不显示字幕", "轨道",
        "音轨", "播放速度", "画面比例", "全屏",
        "退出全屏", "适应", "填充", "拉伸",
        "后退 10 秒", "前进 10 秒", "音量", "亮度",
        "没有音轨",
    };

    private static final String[] JA = {
        "ファイルを保存", "ヘルプ", "動画を読み込む", "字幕を抽出",
        "字幕を編集", "結果をダウンロードに保存", "形式と注意", "MKV または MP4 を選択",
        "内蔵トラックを読み取る", "SRT テキストを確認", "SRT を貼り付けまたは読み込み", "動画字幕ツール",
        "字幕の抽出・結合・保存", "  ·  MKV / MP4  ·  SRT 出力", "先に動画を選択してください。", "書き出せるテキスト字幕がありません。",
        "抽出中 ", " トラック...", "このトラックは画像字幕（PGS / VobSub）のためテキストに変換できません。", "まだ保存するものがありません。先に字幕を抽出してください。",
        "字幕ファイルの保存先フォルダを選択してください。", "抽出", "保存中 ", "字幕トラックを読み取り中...",
        " 件のキュー（SRT 形式）", "まだ抽出していません", "抽出した字幕", "保存前にテキスト（タイムコードを含む）を編集できます。",
        "字幕がありません。動画を選び「字幕を抽出」を実行してください。", "保存", "このテキストで保存画面へ移動します。", "ファイル保存へ移動",
        "この動画の他の言語", "言語をタップするとエディタに抽出します。", "この字幕を保存", "この字幕を SRT として保存",
        "名前を付けて保存...（フォルダを選択）", "すべての言語を保存（", " ファイル）", "内蔵字幕を抽出",
        "動画内の字幕トラックが下に表示されます。希望の言語を選んでください。", "動画", "動画が選択されていません", "まず「動画を読み込む」から動画を選択してください。",
        "トラックを読み取り中...", "字幕トラックを読み取る", "トラックは自動で表示されます。表示されない場合は「字幕トラックを読み取る」をタップしてください。", " 件の字幕トラック",
        " 件見つかりました", "抽出中...", "選択したトラックを抽出", "すべて抽出 ",
        " トラックをダウンロードへ", "この動画には画像字幕（PGS / VobSub）しかありません。テキストに変換できません。", "結果", " 件の字幕を抽出しました",
        "字幕を確認", "形式", "動画：MKV（Matroska）と MP4。", "読み込み可能な字幕：SRT、ASS/SSA、WebVTT、mov_text。",
        "非対応：PGS と VobSub（画像のため）。", "使い方", "1. 動画を読み込みます。", "2. 内蔵字幕トラックを抽出します。",
        "4. その字幕、または「すべて抽出」でファイル全体を保存します。", "「字幕を編集」には「この字幕を保存」と「名前を付けて保存...」ボタンがあります。", "注意", "すべて端末内で処理され、何もアップロードされません。",
        "保存はシステムのダウンロードフォルダを使うため、ストレージ権限は不要です。", "動画ファイル", "端末から MKV または MP4 ファイルを選択してください。", "選択したファイル",
        "ファイルが選択されていません", "端末から動画を選択", "字幕抽出へ進む", "プレビュー",
        "テキスト字幕のみ：SRT、ASS/SSA、WebVTT、mov_text。PGS や VobSub などの画像字幕はテキストに変換できません。", "字幕ファイルを保存", "結果は SRT ファイルとしてダウンロードに保存されます。", "ファイル名",
        "すべて保存 ", " 言語トラック", "上の字幕のみ保存", "ダウンロードに保存",
        "すべてのトラックを保存、フォルダを選択...", " 件のテキストトラック。各言語は個別のファイルとして保存されます。", " 件のキューを保存できます。", "別の SRT を読み込むか貼り付けます。抽出した字幕と結合し、時間順に並べ替えます。",
        "SRT ファイルを選択", "クリア", "2 番目のトラックが SRT の場合、キューは結合され時間順に並びます。プレーンテキストは各キューの下に追加されます。", "トラックを抽出中 ",
        " / ", "トラックを抽出できませんでした。1 つずつ試してください。", "保存しました ", " ファイル",
        " をダウンロードへ：", " トラック", " を読み取れませんでした。", " SRT ファイル",
        " をダウンロードに保存しました。", " SRT ファイル", "抽出しました ", " 件のキュー（",
        " 他の言語も残すには「すべて抽出」を使用してください ", " トラック。", "そのトラックからテキストを読み取れませんでした。", "エラー：",
        "保存先：ダウンロード/", " 件のキュー）", "ダウンロードに保存しました/", "ダウンロードに保存しました",
        "保存に失敗しました。", "読み込み完了：", "そのファイルを読み取れませんでした：", "選択した場所に保存しました。",
        "ファイルを保存しました。", "ファイルを書き込めませんでした。もう一度お試しください。", " を選択したフォルダへ：", " を保存しました。",
        "この動画に字幕トラックが見つかりません。", " 件見つかりました。", "2 番目の字幕をクリアしました。", "選択中：",
        "まだ抽出していません。「字幕を抽出」を実行してください...", "端末から動画を選んで開始してください。", "", "言語を変更しました。",
        "言語：English  •  Tiếng Việt", "トラック ", "（画像）", " 件のキュー",
        "動画に新しい SRT を追加", "3. 動画に新しい SRT を追加します。", "不明な言語", "プレビューを読み込み中...",
        "プレビューを利用できません。この端末は動画コーデックに対応していない可能性があります。字幕の抽出は可能です。", "先頭", "最後", "全画面",
        "完了", "字幕の先頭", "字幕の末尾", "字幕をクリアしますか？",
        "このエディタのテキストが削除されます。", "キャンセル", "別の動画を選択", "設定",
        "言語", "外観", "ライト", "ダーク",
        "適用", "設定を更新しました。", "再生", "一時停止",
        "動画をシーク", "閉じる", "コピー", "コピーしました",
        "元のテキストが変更されました", "置き換え", "抽出した字幕と結合する SRT テキストを貼り付け...", "抽出するテキストトラックを 1 つ以上選択してください。各トラックは個別の SRT になります。",
        "選択したトラック", "選択したトラックを抽出", "すべて選択", "すべて解除",
        "トラック一覧をスクロールするとさらに表示されます。", "字幕を抽出中", "抽出したトラック", "選択したトラックを抽出できませんでした。既存の字幕は保持されています。",
        "一部のトラックを読み取れませんでした", "編集する抽出済みトラックを選択", "抽出したトラックをフォルダに保存", "SRT を追加",
        "フォルダ選択を開けませんでした。", "抽出したトラックを保存中", "保存したファイル", "一部のファイルを保存できませんでした",
        "最初の画面です。", "字幕トラックが見つかりました", "字幕トラックを読み取れませんでした。もう一度お試しください。", "フォント",
        "既定のフォント", "フォントのプレビュー", "先に動画を読み込んでください。", "トラック名",
        "字幕の言語コード", "SRT 言語の会話", "例：vi / vie、en / eng", "出力ファイル名",
        "名前と言語はファイル名にのみ保存されます。SRT ファイルにトラックのメタデータは埋め込めません。", "トラック名と有効な言語コードを入力してください。", "追加した SRT を保存", "抽出した字幕と一緒に使う",
        "トラック名を入力してください。", "vi/vie や en/eng などの言語コードを使用してください。", "有効な SRT テキストを貼り付けるか読み込んでください。", "先に字幕を抽出するか、この SRT を別途保存してください。",
        "ファイル選択を開けませんでした。", "SRT を保存中", "SRT を保存しました", "SRT を MKV / MP4 に埋め込む",
        "動画の書き出しではトラック名と言語が埋め込まれます。SRT のみの書き出しではファイル名に保存されます。元の動画は保持されます。", "動画の書き出しが既に進行中です。", "別の出力ファイルを選択してください。元の動画は上書きできません。", "動画の書き出しを準備中",
        "ローカル動画のコピーを準備中...", "動画情報を読み取り中...", "字幕を埋め込み中", "字幕トラックを埋め込み中...",
        "新しい動画を確認中...", "新しい動画を保存中...", "動画を保存しました", "新しい字幕トラック付きで動画を保存しました。",
        "動画の書き出しに失敗しました", "動画の書き出しに失敗しました。元の動画は変更されていません。", "この端末では動画エンジンを利用できません。", "MKV と MP4 の動画のみ対応しています。",
        "空き容量が不足しているため、この動画を書き出せません。", "一時ストレージを作成できませんでした。", "動画を開けませんでした。", "この動画を読み取れませんでした。端末内の MKV または MP4 ファイルを読み込んでください。",
        "読み込んだファイルに動画トラックがありません。", "字幕を埋め込めませんでした。この動画にはコンテナにコピーできないトラックが含まれている可能性があります。", "新しい動画を検証できませんでした。", "字幕のメタデータを検証できませんでした。",
        "字幕の形式を検証できませんでした。", "出力動画を書き込めませんでした。", "TXT を SRT として認識しました。新しい .srt ファイルとして保存するか、動画に埋め込んでください。", "字幕のタイミングが動画の範囲外です。先に SRT のタイムスタンプを調整してください。",
        "字幕テキストを検証できませんでした。", "字幕トラック", "字幕なし", "トラック",
        "音声トラック", "再生速度", "アスペクト比", "全画面",
        "全画面を終了", "フィット", "フィル", "ストレッチ",
        "10 秒戻る", "10 秒進む", "音量", "明るさ",
        "音声トラックがありません",
    };

    private static final String[] KO = {
        "파일 저장", "도움말", "동영상 가져오기", "자막 추출",
        "자막 편집", "결과를 다운로드에 저장", "형식 및 참고", "MKV 또는 MP4 선택",
        "내장 트랙 읽기", "SRT 텍스트 검토", "SRT 붙여넣기 또는 불러오기", "동영상 자막 도구",
        "자막 추출, 병합 및 저장", "  ·  MKV / MP4  ·  SRT 출력", "먼저 동영상을 선택하세요.", "내보낼 텍스트 자막 트랙이 없습니다.",
        "추출 중 ", "개 트랙...", "이 트랙은 이미지 기반(PGS / VobSub)이라 텍스트로 변환할 수 없습니다.", "아직 저장할 내용이 없습니다. 먼저 자막을 추출하세요.",
        "자막 파일을 저장할 폴더를 선택하세요.", "추출", "저장 중 ", "자막 트랙 읽는 중...",
        "개 자막(SRT 형식)", "아직 추출된 항목 없음", "추출된 자막", "저장하기 전에 텍스트(타임코드 포함)를 편집할 수 있습니다.",
        "자막이 없습니다. 동영상을 선택하고 먼저 자막 추출을 실행하세요.", "저장", "이 텍스트로 저장 화면으로 이동합니다.", "파일 저장으로 이동",
        "이 동영상의 다른 언어", "언어를 탭하면 편집기로 추출됩니다.", "이 자막 저장", "이 자막을 SRT로 저장",
        "다른 이름으로 저장...(폴더 선택)", "모든 언어 저장 (", "개 파일)", "내장 자막 추출",
        "동영상의 모든 자막 트랙이 아래에 표시됩니다. 원하는 언어를 선택하세요.", "동영상", "동영상 선택 안 됨", "먼저 동영상 가져오기에서 동영상을 선택하세요.",
        "트랙 읽는 중...", "자막 트랙 읽기", "트랙이 자동으로 표시됩니다. 아무것도 없으면 자막 트랙 읽기를 탭하세요.", "개 자막 트랙",
        "개 발견됨", "추출 중...", "선택한 트랙 추출", "모두 추출 ",
        "개 트랙을 다운로드로", "이 동영상에는 이미지 기반 자막(PGS / VobSub)만 있어 텍스트로 변환할 수 없습니다.", "결과", "개 자막 추출됨",
        "자막 검토", "형식", "동영상: MKV(Matroska) 및 MP4.", "읽을 수 있는 자막: SRT, ASS/SSA, WebVTT, mov_text.",
        "지원 안 됨: PGS 및 VobSub(이미지 자막).", "사용 방법", "1. 동영상을 가져옵니다.", "2. 내장 자막 트랙을 추출합니다.",
        "4. 해당 자막을 저장하거나 '모두 추출'로 전체 파일을 저장합니다.", "자막 편집에는 '이 자막 저장'과 '다른 이름으로 저장...' 버튼이 있습니다.", "참고", "모두 휴대폰에서 처리되며 아무것도 업로드되지 않습니다.",
        "저장은 시스템 다운로드 폴더를 사용하며 저장 권한이 필요 없습니다.", "동영상 파일", "기기에서 MKV 또는 MP4 파일을 선택하세요.", "선택한 파일",
        "파일 선택 안 됨", "기기에서 동영상 선택", "자막 추출 계속", "미리보기",
        "텍스트 자막만 지원: SRT, ASS/SSA, WebVTT, mov_text. PGS, VobSub 같은 이미지 자막은 텍스트로 변환할 수 없습니다.", "자막 파일 저장", "결과는 SRT 파일로 다운로드에 저장됩니다.", "파일 이름",
        "모두 저장 ", "개 언어 트랙", "위 자막만 저장", "다운로드에 저장",
        "모든 트랙 저장, 폴더 선택...", "개 텍스트 트랙. 각 언어는 별도 파일로 저장됩니다.", "개 자막 저장 준비됨.", "다른 SRT를 불러오거나 붙여넣으세요. 추출한 자막과 병합되어 시간순으로 정렬됩니다.",
        "SRT 파일 선택", "지우기", "두 번째 트랙이 SRT이면 자막이 병합되어 시간순으로 정렬됩니다. 일반 텍스트는 각 자막 아래에 추가됩니다.", "트랙 추출 중 ",
        " / ", "트랙을 추출할 수 없습니다. 한 번에 하나씩 시도하세요.", "저장됨 ", "개 파일",
        "을 다운로드에:", "개 트랙", "을 읽을 수 없습니다.", " SRT 파일",
        "이 다운로드에 저장되었습니다.", " SRT 파일", "추출됨 ", "개 자막: ",
        " 다른 언어를 유지하려면 모두 추출을 사용하세요 ", "개 트랙.", "해당 트랙에서 텍스트를 읽을 수 없습니다.", "오류: ",
        "저장됨: 다운로드/", "개 자막)", "다운로드에 저장됨/", "다운로드에 저장됨",
        "저장 실패.", "불러옴: ", "해당 파일을 읽을 수 없습니다: ", "선택한 위치에 저장했습니다.",
        "파일이 저장되었습니다.", "파일을 쓸 수 없습니다. 다시 시도하세요.", "을 선택한 폴더에:", " 저장됨.",
        "이 동영상에서 자막 트랙을 찾을 수 없습니다.", "개 발견됨.", "두 번째 자막을 지웠습니다.", "선택됨: ",
        "아직 추출된 항목이 없습니다. 자막 추출을 실행하세요...", "시작하려면 기기에서 동영상을 선택하세요.", "", "언어가 변경되었습니다.",
        "언어: English  •  Tiếng Việt", "트랙 ", " (이미지)", "개 자막",
        "동영상에 새 SRT 추가", "3. 동영상에 새 SRT를 추가합니다.", "알 수 없는 언어", "미리보기 불러오는 중...",
        "미리보기를 사용할 수 없습니다. 이 기기가 동영상 코덱을 지원하지 않을 수 있습니다. 자막 추출은 가능합니다.", "처음", "끝", "전체 화면",
        "완료", "자막 시작", "자막 끝", "자막을 지울까요?",
        "이 편집기의 텍스트가 삭제됩니다.", "취소", "다른 동영상 선택", "설정",
        "언어", "모양", "라이트", "다크",
        "적용", "설정이 업데이트되었습니다.", "재생", "일시정지",
        "동영상 탐색", "닫기", "복사", "복사됨",
        "원본 텍스트 변경됨", "바꾸기", "추출한 자막과 병합할 SRT 텍스트를 붙여넣으세요...", "추출할 텍스트 트랙을 하나 이상 선택하세요. 각 트랙은 별도 SRT로 저장됩니다.",
        "선택한 트랙", "선택한 트랙 추출", "전체 선택", "전체 해제",
        "트랙 목록을 스크롤하면 더 볼 수 있습니다.", "자막 추출 중", "추출된 트랙", "선택한 트랙을 추출할 수 없습니다. 기존 자막은 유지되었습니다.",
        "일부 트랙을 읽을 수 없습니다", "편집할 추출된 트랙 선택", "추출된 트랙을 폴더에 저장", "SRT 추가",
        "폴더 선택기를 열 수 없습니다.", "추출된 트랙 저장 중", "저장된 파일", "일부 파일을 저장할 수 없습니다",
        "첫 화면입니다.", "자막 트랙 발견됨", "자막 트랙을 읽을 수 없습니다. 다시 시도하세요.", "글꼴",
        "기본 글꼴", "글꼴 미리보기", "먼저 동영상을 가져오세요.", "트랙 이름",
        "자막 언어 코드", "SRT 언어 대사", "예: vi / vie, en / eng", "출력 파일 이름",
        "이름과 언어는 파일 이름에만 저장됩니다. SRT 파일에는 트랙 메타데이터를 포함할 수 없습니다.", "트랙 이름과 유효한 언어 코드를 입력하세요.", "추가한 SRT 저장", "추출한 자막과 함께 사용",
        "트랙 이름을 입력하세요.", "vi/vie 또는 en/eng 같은 언어 코드를 사용하세요.", "먼저 유효한 SRT 텍스트를 붙여넣거나 불러오세요.", "먼저 자막을 추출하거나 이 SRT를 따로 저장하세요.",
        "파일 선택기를 열 수 없습니다.", "SRT 저장 중", "SRT 저장됨", "SRT를 MKV / MP4에 삽입",
        "동영상 내보내기는 트랙 이름과 언어를 삽입합니다. SRT만 내보내면 파일 이름에 저장됩니다. 원본 동영상은 유지됩니다.", "동영상 내보내기가 이미 진행 중입니다.", "다른 출력 파일을 선택하세요. 원본 동영상은 덮어쓸 수 없습니다.", "동영상 내보내기 준비 중",
        "로컬 동영상 사본 준비 중...", "동영상 정보 읽는 중...", "자막 삽입 중", "자막 트랙 삽입 중...",
        "새 동영상 확인 중...", "새 동영상 저장 중...", "동영상 저장됨", "새 자막 트랙과 함께 동영상이 저장되었습니다.",
        "동영상 내보내기 실패", "동영상 내보내기에 실패했습니다. 원본 동영상은 변경되지 않았습니다.", "이 기기에서는 동영상 엔진을 사용할 수 없습니다.", "MKV 및 MP4 동영상만 지원됩니다.",
        "저장 공간이 부족하여 이 동영상을 내보낼 수 없습니다.", "임시 저장소를 만들 수 없습니다.", "동영상을 열 수 없습니다.", "이 동영상을 읽을 수 없습니다. 기기의 MKV 또는 MP4 파일을 가져오세요.",
        "가져온 파일에 동영상 트랙이 없습니다.", "자막을 삽입할 수 없습니다. 이 동영상에는 컨테이너로 복사할 수 없는 트랙이 있을 수 있습니다.", "새 동영상을 확인할 수 없습니다.", "자막 메타데이터를 확인할 수 없습니다.",
        "자막 형식을 확인할 수 없습니다.", "출력 동영상을 쓸 수 없습니다.", "TXT를 SRT로 인식했습니다. 새 .srt 파일로 저장하거나 동영상에 삽입하세요.", "자막 시간이 동영상 범위를 벗어났습니다. 먼저 SRT 타임스탬프를 조정하세요.",
        "자막 텍스트를 확인할 수 없습니다.", "자막 트랙", "자막 없음", "트랙",
        "오디오 트랙", "재생 속도", "화면 비율", "전체 화면",
        "전체 화면 종료", "맞춤", "채우기", "늘이기",
        "10초 뒤로", "10초 앞으로", "볼륨", "밝기",
        "오디오 트랙 없음",
    };

    private static final String[] VI = {
        "Lưu tệp", "Trợ giúp", "Nhập video", "Trích xuất phụ đề",
        "Sửa phụ đề", "Lưu kết quả vào Tải xuống", "Định dạng và lưu ý", "Chọn tệp MKV hoặc MP4",
        "Đọc các luồng phụ đề có sẵn", "Xem lại nội dung SRT", "Dán hoặc mở tệp SRT", "Công cụ phụ đề video",
        "Trích xuất, ghép và lưu phụ đề", "  ·  MKV / MP4  ·  Xuất SRT", "Hãy chọn video trước.", "Không có phụ đề văn bản để xuất.",
        "Đang trích xuất ", " luồng...", "Luồng này là phụ đề ảnh (PGS / VobSub), không thể chuyển thành văn bản.", "Chưa có dữ liệu để lưu. Hãy trích xuất phụ đề trước.",
        "Chọn thư mục để lưu các tệp phụ đề.", "Trích xuất", "Đang lưu ", "Đang đọc các luồng phụ đề...",
        " mục phụ đề ở định dạng SRT", "Chưa trích xuất phụ đề", "Phụ đề đã trích xuất", "Bạn có thể sửa nội dung và mốc thời gian trước khi lưu.",
        "Chưa có phụ đề. Hãy chọn video rồi trích xuất phụ đề.", "Lưu", "Chuyển đến màn hình lưu với nội dung này.", "Đến màn hình lưu tệp",
        "Các ngôn ngữ khác trong video", "Chạm vào một ngôn ngữ để trích xuất vào trình sửa.", "Lưu phụ đề này", "Lưu phụ đề này thành SRT",
        "Lưu thành... (chọn thư mục)", "Lưu tất cả ngôn ngữ (", " tệp)", "Trích xuất phụ đề có sẵn",
        "Các luồng phụ đề trong video được liệt kê bên dưới. Hãy chọn ngôn ngữ bạn muốn.", "Video", "Chưa chọn video", "Hãy chọn video ở mục Nhập video trước.",
        "Đang đọc các luồng...", "Đọc các luồng phụ đề", "Các luồng được liệt kê tự động. Nếu chưa thấy, hãy chạm Đọc các luồng phụ đề.", " luồng phụ đề",
        " được tìm thấy", "Đang trích xuất...", "Trích xuất luồng đã chọn", "Trích xuất tất cả ",
        " luồng vào Tải xuống", "Video này chỉ có phụ đề ảnh (PGS / VobSub), không thể chuyển thành văn bản.", "Kết quả", " mục phụ đề đã trích xuất",
        "Xem lại phụ đề", "Định dạng", "Video: MKV (Matroska) và MP4.", "Phụ đề hỗ trợ: SRT, ASS/SSA, WebVTT, mov_text.",
        "Không hỗ trợ PGS và VobSub vì đây là phụ đề ảnh.", "Cách sử dụng", "1. Nhập một video.", "2. Trích xuất luồng phụ đề có sẵn.",
        "4. Lưu phụ đề đã chọn hoặc dùng Trích xuất tất cả để lưu mọi luồng.", "Mục Sửa phụ đề có nút Lưu phụ đề này và Lưu thành...", "Lưu ý", "Mọi xử lý diễn ra trên điện thoại, không tải dữ liệu lên mạng.",
        "Lưu vào thư mục Tải xuống của hệ thống, không cần quyền bộ nhớ trên Android 10 trở lên.", "Tệp video", "Chọn tệp MKV hoặc MP4 trên thiết bị này.", "Tệp đã chọn",
        "Chưa chọn tệp", "Chọn video trên thiết bị", "Tiếp tục trích xuất phụ đề", "Xem trước",
        "Chỉ hỗ trợ phụ đề văn bản: SRT, ASS/SSA, WebVTT và mov_text. Không thể chuyển phụ đề ảnh PGS hoặc VobSub thành văn bản.", "Lưu tệp phụ đề", "Kết quả được lưu thành tệp SRT trong thư mục Tải xuống.", "Tên tệp",
        "Lưu tất cả ", " luồng ngôn ngữ", "Chỉ lưu phụ đề ở trên", "Lưu vào Tải xuống",
        "Lưu mọi luồng, chọn thư mục...", " luồng văn bản trong video này. Mỗi ngôn ngữ được lưu thành một tệp riêng.", " mục phụ đề sẵn sàng để lưu.", "Mở hoặc dán một SRT khác. Nội dung được ghép với phụ đề đã trích xuất và sắp xếp theo thời gian.",
        "Chọn tệp SRT", "Xóa", "Nếu nội dung thứ hai là SRT, các mục sẽ được ghép và sắp xếp theo thời gian. Văn bản thuần sẽ được thêm dưới mỗi mục phụ đề đã trích xuất.", "Đang trích xuất luồng ",
        " / ", "Không trích xuất được luồng nào. Hãy thử từng luồng một.", "Đã lưu ", " tệp",
        " vào Tải xuống:", " luồng", " không đọc được.", " tệp SRT",
        " đã lưu vào Tải xuống.", " tệp SRT", "Đã trích xuất ", " mục từ ",
        " Để giữ các ngôn ngữ khác, dùng Trích xuất tất cả ", " luồng.", "Không đọc được văn bản từ luồng này.", "Lỗi: ",
        "Đã lưu: Downloads/", " mục)", "Đã lưu vào Downloads/", "Đã lưu vào Tải xuống",
        "Lưu thất bại.", "Đã mở: ", "Không đọc được tệp: ", "Đã lưu vào vị trí bạn chọn.",
        "Đã lưu tệp.", "Không ghi được tệp. Hãy thử lại.", " vào thư mục bạn chọn:", " đã lưu.",
        "Không tìm thấy luồng phụ đề trong video này.", " được tìm thấy.", "Đã xóa phụ đề thứ hai.", "Đã chọn: ",
        "Chưa có phụ đề. Hãy chạy Trích xuất phụ đề...", "Chọn một video trên thiết bị để bắt đầu.", "", "Đã đổi ngôn ngữ.",
        "Ngôn ngữ: Tiếng Việt  •  English", "Luồng ", " (ảnh)", " mục phụ đề",
        "Thêm SRT mới vào video", "3. Thêm SRT mới vào video.", "Không xác định", "Đang tải xem trước...",
        "Không thể xem trước. Thiết bị có thể không hỗ trợ codec video này. Bạn vẫn có thể trích xuất phụ đề.", "Đầu", "Cuối", "Toàn màn hình",
        "Xong", "Đầu phụ đề", "Cuối phụ đề", "Xóa nội dung phụ đề?",
        "Nội dung trong ô phụ đề này sẽ bị xóa.", "Hủy", "Chọn video khác", "Cài đặt",
        "Ngôn ngữ", "Giao diện", "Sáng", "Tối",
        "Áp dụng", "Đã cập nhật cài đặt.", "Phát", "Tạm dừng",
        "Tua video", "Đóng", "Sao chép", "Đã sao chép",
        "Nội dung gốc đã thay đổi", "Thay thế", "Dán nội dung SRT để ghép với phụ đề đã trích xuất...", "Chọn một hoặc nhiều luồng văn bản để trích xuất. Mỗi luồng được giữ thành một SRT riêng.",
        "Luồng đã chọn", "Trích xuất các luồng đã chọn", "Chọn tất cả", "Bỏ chọn tất cả",
        "Cuộn danh sách để xem thêm các luồng.", "Đang trích xuất phụ đề", "Luồng đã trích xuất", "Không trích xuất được các luồng đã chọn. Phụ đề hiện có được giữ nguyên.",
        "Một số luồng không đọc được", "Chọn luồng đã trích xuất để sửa", "Lưu các luồng đã trích xuất vào thư mục", "Thêm SRT",
        "Không mở được trình chọn thư mục.", "Đang lưu các luồng đã trích xuất", "Tệp đã lưu", "Một số tệp không lưu được",
        "Bạn đang ở màn hình đầu tiên.", "Luồng phụ đề tìm thấy", "Không đọc được luồng phụ đề. Hãy thử lại.", "Phông chữ",
        "Phông chữ mặc định", "Xem thử phông chữ", "Vui lòng nhập video trước.", "Tên track",
        "Mã ngôn ngữ phụ đề", "Hội thoại theo ngôn ngữ SRT của bạn", "Ví dụ: vi / vie, en / eng", "Tên tệp đầu ra",
        "Tên và ngôn ngữ chỉ được lưu trong tên tệp. SRT không chứa metadata của track trong video.", "Nhập tên track và mã ngôn ngữ hợp lệ.", "Lưu riêng SRT vừa thêm", "Dùng cùng phụ đề đã trích xuất",
        "Vui lòng nhập tên track.", "Dùng mã ngôn ngữ như vi/vie hoặc en/eng.", "Hãy dán hoặc mở nội dung SRT hợp lệ trước.", "Hãy trích xuất phụ đề trước hoặc lưu riêng SRT này.",
        "Không mở được trình chọn tệp.", "Đang lưu SRT", "Đã lưu SRT", "Nhúng SRT vào MKV / MP4",
        "Xuất video sẽ nhúng tên track và ngôn ngữ. Xuất riêng SRT chỉ lưu thông tin trong tên tệp. Video gốc được giữ nguyên.", "Đang có một tác vụ xuất video.", "Hãy chọn tệp đầu ra khác. Không thể ghi đè video gốc.", "Chuẩn bị xuất video",
        "Đang chuẩn bị bản video cục bộ...", "Đang đọc thông tin video...", "Đang nhúng phụ đề", "Đang nhúng track phụ đề...",
        "Đang kiểm tra video mới...", "Đang lưu video mới...", "Đã lưu video", "Đã lưu video có track phụ đề mới.",
        "Xuất video thất bại", "Xuất video thất bại. Video gốc không bị thay đổi.", "Bộ xử lý video không hoạt động trên thiết bị này.", "Chỉ hỗ trợ video MKV và MP4.",
        "Không đủ dung lượng trống để xuất video này.", "Không tạo được vùng lưu trữ tạm.", "Không mở được video.", "Không đọc được video này. Hãy thử nhập tệp MKV hoặc MP4 trên thiết bị.",
        "Tệp đã nhập không có luồng video.", "Không nhúng được phụ đề. Video có thể chứa luồng không thể sao chép vào container đầu ra.", "Không xác minh được video mới.", "Không xác minh được tên và ngôn ngữ track phụ đề.",
        "Không xác minh được định dạng track phụ đề.", "Không ghi được video đầu ra.", "Đã nhận diện TXT là SRT. Lưu thành tệp .srt mới hoặc nhúng vào video.", "Thời gian phụ đề nằm ngoài video. Hãy chỉnh mốc thời gian SRT trước.",
        "Không đọc lại được đầy đủ nội dung và thời gian phụ đề sau khi nhúng.", "Track phụ đề", "Không hiện phụ đề", "Luồng",
        "Track âm thanh", "Tốc độ phát", "Tỷ lệ khung hình", "Toàn màn hình",
        "Thoát toàn màn hình", "Vừa khung", "Phủ kín", "Kéo giãn",
        "Lùi 10 giây", "Tiến 10 giây", "Âm lượng", "Độ sáng",
        "Không có track âm thanh",
    };

    private static final HashMap<String, Integer> INDEX = new HashMap<>();
    private static final HashMap<String, String> ANY_TO_EN = new HashMap<>();
    private static HashMap forward;
    private static String lang = "en";
    private Activity activity;

    static {
        for (int i = 0; i < EN.length; i++) {
            INDEX.put(EN[i], Integer.valueOf(i));
            ANY_TO_EN.put(EN[i], EN[i]);
            if (ZH[i].length() > 0) {
                ANY_TO_EN.put(ZH[i], EN[i]);
            }
            if (JA[i].length() > 0) {
                ANY_TO_EN.put(JA[i], EN[i]);
            }
            if (KO[i].length() > 0) {
                ANY_TO_EN.put(KO[i], EN[i]);
            }
            if (VI[i].length() > 0) {
                ANY_TO_EN.put(VI[i], EN[i]);
            }
        }
        rebuild();
    }

    private static String[] table(String str) {
        if ("zh".equals(str)) {
            return ZH;
        }
        if ("ja".equals(str)) {
            return JA;
        }
        if ("ko".equals(str)) {
            return KO;
        }
        if ("vi".equals(str)) {
            return VI;
        }
        return EN;
    }

    private static void rebuild() {
        String[] strArr = table(lang);
        HashMap hashMap = new HashMap();
        for (int i = 0; i < EN.length; i++) {
            hashMap.put(EN[i], strArr[i]);
        }
        forward = hashMap;
    }

    public static String validCode(String str) {
        if (str != null) {
            for (int i = 0; i < CODES.length; i++) {
                if (CODES[i].equals(str)) {
                    return str;
                }
            }
        }
        return "en";
    }

    public static String current() {
        return lang;
    }

    public static int index() {
        for (int i = 0; i < CODES.length; i++) {
            if (CODES[i].equals(lang)) {
                return i;
            }
        }
        return 0;
    }

    public static String name() {
        return NAMES[index()];
    }

    public static boolean isCjk() {
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }

    public static Locale locale() {
        return new Locale(lang);
    }

    /* Any language tag written in the current UI language, e.g. "en" -> "Vietnamese" on English UI. */
    public static String languageLabel(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        String str2 = str;
        try {
            str2 = Locale.forLanguageTag(str).getDisplayName(locale());
        } catch (Exception e) {
            str2 = str;
        }
        if (str2 == null || str2.length() == 0) {
            str2 = str;
        }
        if (str2.length() > 0 && str2.charAt(0) >= 'a' && str2.charAt(0) <= 'z') {
            str2 = Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
        }
        return str2;
    }

    /* Language names written in the current UI language: English UI shows "Vietnamese". */
    public static String displayName(String str) {
        int i = -1;
        for (int j = 0; j < CODES.length; j++) {
            if (CODES[j].equalsIgnoreCase(str)) {
                i = j;
            }
        }
        String str2 = languageLabel(str);
        if (i >= 0 && str2.equalsIgnoreCase(str)) {
            str2 = NAMES[i];
        }
        return str2;
    }

    /* Endonyms for the language picker: "中文", never translated; the English name is added
       in brackets only while the UI itself is English. */
    public static String[] languageOptions() {
        String[] strArr = new String[CODES.length];
        boolean englishUi = "en".equals(lang);
        for (int i = 0; i < CODES.length; i++) {
            strArr[i] = englishUi ? NAMES[i] + (i == 0 ? "" : " (" + EN_NAMES[i] + ")") : NAMES[i];
        }
        return strArr;
    }

    public static String[] languageNames() {
        return languageOptions();
    }

    public static String[] subtitleLabels() {
        String[] strArr = new String[SUBTITLE_CODES.length];
        for (int i = 0; i < SUBTITLE_CODES.length; i++) {
            strArr[i] = SUBTITLE_CODES[i] + " · " + displayName(SUBTITLE_CODES[i]);
        }
        return strArr;
    }

    public static int subtitleIndex(String str) {
        for (int i = 0; i < SUBTITLE_CODES.length; i++) {
            if (SUBTITLE_CODES[i].equalsIgnoreCase(str)) {
                return i;
            }
        }
        return 0;
    }

    public static void setLanguage(Activity activity, String str) {
        lang = validCode(str);
        SharedPreferences.Editor edit = activity.getSharedPreferences(PREFS, 0).edit();
        edit.putString(PREF_LANG, lang);
        edit.putBoolean(PREF_VI, "vi".equals(lang));
        edit.apply();
        rebuild();
    }

    public Localization(Activity activity) {
        this.activity = activity;
    }

    public static void footer(Activity activity, android.widget.LinearLayout linearLayout) {
        AppSettings.footer(activity, linearLayout);
    }

    public static void init(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS, 0);
        String string = sharedPreferences.getString(PREF_LANG, null);
        if (string == null) {
            string = sharedPreferences.getBoolean(PREF_VI, false) ? "vi" : "en";
        }
        lang = validCode(string);
        rebuild();
        Window window = activity.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(-1));
        if (Build.VERSION.SDK_INT >= 29) {
            window.getDecorView().setForceDarkAllowed(false);
            window.setNavigationBarContrastEnforced(false);
            window.setStatusBarContrastEnforced(false);
        }
    }

    public static void refreshLabels(View view) {
        walk(view);
    }

    public static String tr(String str) {
        Integer num = INDEX.get(str);
        return num == null ? str : table(lang)[num.intValue()];
    }

    private static void walk(View view) {
        if (view instanceof EditText) {
            return;
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            String cur = textView.getText().toString();
            if (cur.length() > 0) {
                String en = ANY_TO_EN.get(cur);
                Object obj = en == null ? null : forward.get(en);
                if (obj != null) {
                    textView.setText((String) obj);
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                walk(viewGroup.getChildAt(i));
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int next = (index() + 1) % CODES.length;
        setLanguage(this.activity, CODES[next]);
        walk(this.activity.getWindow().getDecorView());
        ((MainActivity) this.activity).refreshLanguage();
    }
}
