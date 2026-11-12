package com.antonkarpenko.ffmpegkit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class FFmpegKitConfig {
    static final String FFMPEG_KIT_NAMED_PIPE_PREFIX = "fk_pipe_";
    static final String TAG = "ffmpeg-kit";
    private static Level activeLogLevel;
    private static int asyncConcurrencyLimit;
    private static ExecutorService asyncExecutorService;
    private static FFmpegSessionCompleteCallback globalFFmpegSessionCompleteCallback;
    private static FFprobeSessionCompleteCallback globalFFprobeSessionCompleteCallback;
    private static LogCallback globalLogCallback;
    private static LogRedirectionStrategy globalLogRedirectionStrategy;
    private static MediaInformationSessionCompleteCallback globalMediaInformationSessionCompleteCallback;
    private static StatisticsCallback globalStatisticsCallback;
    private static final SparseArray<SAFProtocolUrl> safFileDescriptorMap;
    private static final SparseArray<SAFProtocolUrl> safIdMap;
    private static final List<Session> sessionHistoryList;
    private static final Object sessionHistoryLock;
    private static final Map<Long, Session> sessionHistoryMap;
    private static int sessionHistorySize;
    private static final AtomicInteger uniqueIdGenerator;

    private static native void disableNativeRedirection();

    private static native void enableNativeRedirection();

    private static native String getNativeBuildDate();

    private static native String getNativeFFmpegVersion();

    static native int getNativeLogLevel();

    private static native String getNativeVersion();

    private static native void ignoreNativeSignal(int i);

    public static native int messagesInTransmit(long j);

    static native void nativeFFmpegCancel(long j);

    private static native int nativeFFmpegExecute(long j, String[] strArr);

    static native int nativeFFprobeExecute(long j, String[] strArr);

    private static native int registerNewNativeFFmpegPipe(String str);

    private static native int setNativeEnvironmentVariable(String str, String str2);

    private static native void setNativeLogLevel(int i);

    static class SAFProtocolUrl {
        private final ContentResolver contentResolver;
        private final String openMode;
        private ParcelFileDescriptor parcelFileDescriptor;
        private final Integer safId;
        private final Uri uri;

        public SAFProtocolUrl(Integer safId, Uri uri, String openMode, ContentResolver contentResolver) {
            this.safId = safId;
            this.uri = uri;
            this.openMode = openMode;
            this.contentResolver = contentResolver;
        }

        public Integer getSafId() {
            return this.safId;
        }

        public Uri getUri() {
            return this.uri;
        }

        public String getOpenMode() {
            return this.openMode;
        }

        public ContentResolver getContentResolver() {
            return this.contentResolver;
        }

        public void setParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) {
            this.parcelFileDescriptor = parcelFileDescriptor;
        }

        public ParcelFileDescriptor getParcelFileDescriptor() {
            return this.parcelFileDescriptor;
        }
    }

    static {
        android.util.Log.i(TAG, "Loading ffmpeg-kit.");
        boolean nativeFFmpegTriedAndFailed = NativeLoader.loadFFmpeg();
        Abi.class.getName();
        FFmpegKit.class.getName();
        FFprobeKit.class.getName();
        NativeLoader.loadFFmpegKit(nativeFFmpegTriedAndFailed);
        uniqueIdGenerator = new AtomicInteger(1);
        activeLogLevel = Level.from(NativeLoader.loadLogLevel());
        asyncConcurrencyLimit = 10;
        asyncExecutorService = Executors.newFixedThreadPool(asyncConcurrencyLimit);
        sessionHistorySize = 10;
        sessionHistoryMap = new LinkedHashMap<Long, Session>() { // from class: com.antonkarpenko.ffmpegkit.FFmpegKitConfig.1
            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<Long, Session> eldest) {
                return size() > FFmpegKitConfig.sessionHistorySize;
            }
        };
        sessionHistoryList = new LinkedList();
        sessionHistoryLock = new Object();
        globalLogCallback = null;
        globalStatisticsCallback = null;
        globalFFmpegSessionCompleteCallback = null;
        globalFFprobeSessionCompleteCallback = null;
        globalMediaInformationSessionCompleteCallback = null;
        safIdMap = new SparseArray<>();
        safFileDescriptorMap = new SparseArray<>();
        globalLogRedirectionStrategy = LogRedirectionStrategy.PRINT_LOGS_WHEN_NO_CALLBACKS_DEFINED;
        android.util.Log.i(TAG, String.format("Loaded ffmpeg-kit-%s-%s-%s-%s.", NativeLoader.loadPackageName(), NativeLoader.loadAbi(), NativeLoader.loadVersion(), NativeLoader.loadBuildDate()));
    }

    private FFmpegKitConfig() {
    }

    public static void enableRedirection() {
        enableNativeRedirection();
    }

    public static void disableRedirection() {
        disableNativeRedirection();
    }

    private static void log(long sessionId, int levelValue, byte[] logMessage) {
        Level level = Level.from(levelValue);
        String text = new String(logMessage);
        Log log = new Log(sessionId, level, text);
        boolean globalCallbackDefined = false;
        boolean sessionCallbackDefined = false;
        LogRedirectionStrategy activeLogRedirectionStrategy = globalLogRedirectionStrategy;
        if ((activeLogLevel == Level.AV_LOG_QUIET && levelValue != Level.AV_LOG_STDERR.getValue()) || levelValue > activeLogLevel.getValue()) {
            return;
        }
        Session session = getSession(sessionId);
        if (session != null) {
            activeLogRedirectionStrategy = session.getLogRedirectionStrategy();
            session.addLog(log);
            if (session.getLogCallback() != null) {
                sessionCallbackDefined = true;
                try {
                    session.getLogCallback().apply(log);
                } catch (Exception e) {
                }
            }
        }
        LogCallback globalLogCallbackFunction = globalLogCallback;
        if (globalLogCallbackFunction != null) {
            globalCallbackDefined = true;
            try {
                globalLogCallbackFunction.apply(log);
            } catch (Exception e2) {
            }
        }
        switch (activeLogRedirectionStrategy) {
            case NEVER_PRINT_LOGS:
                return;
            case PRINT_LOGS_WHEN_GLOBAL_CALLBACK_NOT_DEFINED:
                if (globalCallbackDefined) {
                    return;
                }
                break;
            case PRINT_LOGS_WHEN_SESSION_CALLBACK_NOT_DEFINED:
                if (sessionCallbackDefined) {
                    return;
                }
                break;
            case PRINT_LOGS_WHEN_NO_CALLBACKS_DEFINED:
                if (globalCallbackDefined || sessionCallbackDefined) {
                    return;
                }
                break;
        }
        switch (level) {
            case AV_LOG_QUIET:
                break;
            case AV_LOG_TRACE:
            case AV_LOG_DEBUG:
                android.util.Log.d(TAG, text);
                break;
            case AV_LOG_INFO:
                android.util.Log.i(TAG, text);
                break;
            case AV_LOG_WARNING:
                android.util.Log.w(TAG, text);
                break;
            case AV_LOG_ERROR:
            case AV_LOG_FATAL:
            case AV_LOG_PANIC:
                android.util.Log.e(TAG, text);
                break;
            default:
                android.util.Log.v(TAG, text);
                break;
        }
    }

    private static void statistics(long sessionId, int videoFrameNumber, float videoFps, float videoQuality, long size, double time, double bitrate, double speed) {
        Statistics statistics = new Statistics(sessionId, videoFrameNumber, videoFps, videoQuality, size, time, bitrate, speed);
        Session session = getSession(sessionId);
        if (session != null && session.isFFmpeg()) {
            FFmpegSession ffmpegSession = (FFmpegSession) session;
            ffmpegSession.addStatistics(statistics);
            if (ffmpegSession.getStatisticsCallback() != null) {
                try {
                    ffmpegSession.getStatisticsCallback().apply(statistics);
                } catch (Exception e) {
                }
            }
        }
        StatisticsCallback globalStatisticsCallbackFunction = globalStatisticsCallback;
        if (globalStatisticsCallbackFunction != null) {
            try {
                globalStatisticsCallbackFunction.apply(statistics);
            } catch (Exception e2) {
            }
        }
    }

    public static int setFontconfigConfigurationPath(String path) {
        return setNativeEnvironmentVariable("FONTCONFIG_PATH", path);
    }

    public static void setFontDirectory(Context context, String fontDirectoryPath, Map<String, String> fontNameMapping) {
        setFontDirectoryList(context, Collections.singletonList(fontDirectoryPath), fontNameMapping);
    }

    public static void setFontDirectoryList(Context context, List<String> fontDirectoryList, Map<String, String> fontNameMapping) {
        File cacheDir = context.getCacheDir();
        int validFontNameMappingCount = 0;
        File tempConfigurationDirectory = new File(cacheDir, "fontconfig");
        if (!tempConfigurationDirectory.exists()) {
            boolean tempFontConfDirectoryCreated = tempConfigurationDirectory.mkdirs();
            android.util.Log.d(TAG, String.format("Created temporary font conf directory: %s.", Boolean.valueOf(tempFontConfDirectoryCreated)));
        }
        File fontConfiguration = new File(tempConfigurationDirectory, "fonts.conf");
        if (fontConfiguration.exists()) {
            boolean fontConfigurationDeleted = fontConfiguration.delete();
            android.util.Log.d(TAG, String.format("Deleted old temporary font configuration: %s.", Boolean.valueOf(fontConfigurationDeleted)));
        }
        StringBuilder fontNameMappingBlock = new StringBuilder("");
        if (fontNameMapping != null && fontNameMapping.size() > 0) {
            fontNameMapping.entrySet();
            for (Map.Entry<String, String> mapping : fontNameMapping.entrySet()) {
                String fontName = mapping.getKey();
                String mappedFontName = mapping.getValue();
                if (fontName != null && mappedFontName != null && fontName.trim().length() > 0 && mappedFontName.trim().length() > 0) {
                    fontNameMappingBlock.append("    <match target=\"pattern\">\n");
                    fontNameMappingBlock.append("        <test qual=\"any\" name=\"family\">\n");
                    fontNameMappingBlock.append(String.format("            <string>%s</string>\n", fontName));
                    fontNameMappingBlock.append("        </test>\n");
                    fontNameMappingBlock.append("        <edit name=\"family\" mode=\"assign\" binding=\"same\">\n");
                    fontNameMappingBlock.append(String.format("            <string>%s</string>\n", mappedFontName));
                    fontNameMappingBlock.append("        </edit>\n");
                    fontNameMappingBlock.append("    </match>\n");
                    validFontNameMappingCount++;
                }
            }
        }
        StringBuilder fontConfigBuilder = new StringBuilder();
        fontConfigBuilder.append("<?xml version=\"1.0\"?>\n");
        fontConfigBuilder.append("<!DOCTYPE fontconfig SYSTEM \"fonts.dtd\">\n");
        fontConfigBuilder.append("<fontconfig>\n");
        fontConfigBuilder.append("    <dir prefix=\"cwd\">.</dir>\n");
        for (String fontDirectoryPath : fontDirectoryList) {
            fontConfigBuilder.append("    <dir>");
            fontConfigBuilder.append(fontDirectoryPath);
            fontConfigBuilder.append("</dir>\n");
        }
        fontConfigBuilder.append((CharSequence) fontNameMappingBlock);
        fontConfigBuilder.append("</fontconfig>\n");
        AtomicReference<FileOutputStream> reference = new AtomicReference<>();
        try {
            {
                FileOutputStream outputStream = new FileOutputStream(fontConfiguration);
                reference.set(outputStream);
                outputStream.write(fontConfigBuilder.toString().getBytes());
                outputStream.flush();
                android.util.Log.d(TAG, String.format("Saved new temporary font configuration with %d font name mappings.", Integer.valueOf(validFontNameMappingCount)));
                setFontconfigConfigurationPath(tempConfigurationDirectory.getAbsolutePath());
                for (String fontDirectoryPath2 : fontDirectoryList) {
                    android.util.Log.d(TAG, String.format("Font directory %s registered successfully.", fontDirectoryPath2));
                }
                if (reference.get() != null) {
                    reference.get().close();
                }
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            if (reference.get() != null) {
                try {
                    reference.get().close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public static String registerNewFFmpegPipe(Context context) {
        File cacheDir = context.getCacheDir();
        File pipesDir = new File(cacheDir, "pipes");
        if (!pipesDir.exists()) {
            boolean pipesDirCreated = pipesDir.mkdirs();
            if (!pipesDirCreated) {
                android.util.Log.e(TAG, String.format("Failed to create pipes directory: %s.", pipesDir.getAbsolutePath()));
                return null;
            }
        }
        String newFFmpegPipePath = MessageFormat.format("{0}{1}{2}{3}", pipesDir, File.separator, FFMPEG_KIT_NAMED_PIPE_PREFIX, Integer.valueOf(uniqueIdGenerator.getAndIncrement()));
        closeFFmpegPipe(newFFmpegPipePath);
        int rc = registerNewNativeFFmpegPipe(newFFmpegPipePath);
        if (rc == 0) {
            return newFFmpegPipePath;
        }
        android.util.Log.e(TAG, String.format("Failed to register new FFmpeg pipe %s. Operation failed with rc=%d.", newFFmpegPipePath, Integer.valueOf(rc)));
        return null;
    }

    public static void closeFFmpegPipe(String ffmpegPipePath) {
        File file = new File(ffmpegPipePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static List<String> getSupportedCameraIds(Context context) {
        List<String> detectedCameraIdList = new ArrayList<>();
        detectedCameraIdList.addAll(CameraSupport.extractSupportedCameraIds(context));
        return detectedCameraIdList;
    }

    public static String getFFmpegVersion() {
        return getNativeFFmpegVersion();
    }

    public static String getVersion() {
        if (isLTSBuild()) {
            return String.format("%s-lts", getNativeVersion());
        }
        return getNativeVersion();
    }

    public static boolean isLTSBuild() {
        return AbiDetect.isNativeLTSBuild();
    }

    public static String getBuildDate() {
        return getNativeBuildDate();
    }

    public static void printToLogcat(int logPriority, String string) {
        String remainingString = string;
        do {
            if (remainingString.length() <= 4000) {
                android.util.Log.println(logPriority, TAG, remainingString);
                remainingString = "";
            } else {
                int index = remainingString.substring(0, 4000).lastIndexOf(10);
                if (index < 0) {
                    android.util.Log.println(logPriority, TAG, remainingString.substring(0, 4000));
                    remainingString = remainingString.substring(4000);
                } else {
                    android.util.Log.println(logPriority, TAG, remainingString.substring(0, index));
                    remainingString = remainingString.substring(index);
                }
            }
        } while (remainingString.length() > 0);
    }

    public static int setEnvironmentVariable(String variableName, String variableValue) {
        return setNativeEnvironmentVariable(variableName, variableValue);
    }

    public static void ignoreSignal(Signal signal) {
        ignoreNativeSignal(signal.getValue());
    }

    public static void ffmpegExecute(FFmpegSession ffmpegSession) {
        ffmpegSession.startRunning();
        try {
            int returnCode = nativeFFmpegExecute(ffmpegSession.getSessionId(), ffmpegSession.getArguments());
            ffmpegSession.complete(new ReturnCode(returnCode));
        } catch (Exception e) {
            ffmpegSession.fail(e);
        }
    }

    public static void ffprobeExecute(FFprobeSession ffprobeSession) {
        ffprobeSession.startRunning();
        try {
            int returnCode = nativeFFprobeExecute(ffprobeSession.getSessionId(), ffprobeSession.getArguments());
            ffprobeSession.complete(new ReturnCode(returnCode));
        } catch (Exception e) {
            ffprobeSession.fail(e);
        }
    }

    public static void getMediaInformationExecute(MediaInformationSession mediaInformationSession, int waitTimeout) {
        mediaInformationSession.startRunning();
        try {
            int returnCodeValue = nativeFFprobeExecute(mediaInformationSession.getSessionId(), mediaInformationSession.getArguments());
            ReturnCode returnCode = new ReturnCode(returnCodeValue);
            mediaInformationSession.complete(returnCode);
            if (returnCode.isValueSuccess()) {
                List<Log> allLogs = mediaInformationSession.getAllLogs(waitTimeout);
                StringBuilder ffprobeJsonOutput = new StringBuilder();
                int allLogsSize = allLogs.size();
                for (int i = 0; i < allLogsSize; i++) {
                    Log log = allLogs.get(i);
                    if (log.getLevel() == Level.AV_LOG_STDERR) {
                        ffprobeJsonOutput.append(log.getMessage());
                    }
                }
                MediaInformation mediaInformation = MediaInformationJsonParser.fromWithError(ffprobeJsonOutput.toString());
                mediaInformationSession.setMediaInformation(mediaInformation);
            }
        } catch (Exception e) {
            mediaInformationSession.fail(e);
        }
    }

    public static void asyncFFmpegExecute(FFmpegSession ffmpegSession) {
        AsyncFFmpegExecuteTask asyncFFmpegExecuteTask = new AsyncFFmpegExecuteTask(ffmpegSession);
        Future<?> future = asyncExecutorService.submit(asyncFFmpegExecuteTask);
        ffmpegSession.setFuture(future);
    }

    public static void asyncFFmpegExecute(FFmpegSession ffmpegSession, ExecutorService executorService) {
        AsyncFFmpegExecuteTask asyncFFmpegExecuteTask = new AsyncFFmpegExecuteTask(ffmpegSession);
        Future<?> future = executorService.submit(asyncFFmpegExecuteTask);
        ffmpegSession.setFuture(future);
    }

    public static void asyncFFprobeExecute(FFprobeSession ffprobeSession) {
        AsyncFFprobeExecuteTask asyncFFmpegExecuteTask = new AsyncFFprobeExecuteTask(ffprobeSession);
        Future<?> future = asyncExecutorService.submit(asyncFFmpegExecuteTask);
        ffprobeSession.setFuture(future);
    }

    public static void asyncFFprobeExecute(FFprobeSession ffprobeSession, ExecutorService executorService) {
        AsyncFFprobeExecuteTask asyncFFmpegExecuteTask = new AsyncFFprobeExecuteTask(ffprobeSession);
        Future<?> future = executorService.submit(asyncFFmpegExecuteTask);
        ffprobeSession.setFuture(future);
    }

    public static void asyncGetMediaInformationExecute(MediaInformationSession mediaInformationSession, int waitTimeout) {
        AsyncGetMediaInformationTask asyncGetMediaInformationTask = new AsyncGetMediaInformationTask(mediaInformationSession, Integer.valueOf(waitTimeout));
        Future<?> future = asyncExecutorService.submit(asyncGetMediaInformationTask);
        mediaInformationSession.setFuture(future);
    }

    public static void asyncGetMediaInformationExecute(MediaInformationSession mediaInformationSession, ExecutorService executorService, int waitTimeout) {
        AsyncGetMediaInformationTask asyncGetMediaInformationTask = new AsyncGetMediaInformationTask(mediaInformationSession, Integer.valueOf(waitTimeout));
        Future<?> future = executorService.submit(asyncGetMediaInformationTask);
        mediaInformationSession.setFuture(future);
    }

    public static int getAsyncConcurrencyLimit() {
        return asyncConcurrencyLimit;
    }

    public static void setAsyncConcurrencyLimit(int asyncConcurrencyLimit2) {
        if (asyncConcurrencyLimit2 > 0) {
            asyncConcurrencyLimit = asyncConcurrencyLimit2;
            ExecutorService oldAsyncExecutorService = asyncExecutorService;
            asyncExecutorService = Executors.newFixedThreadPool(asyncConcurrencyLimit2);
            oldAsyncExecutorService.shutdown();
        }
    }

    public static void enableLogCallback(LogCallback logCallback) {
        globalLogCallback = logCallback;
    }

    public static void enableStatisticsCallback(StatisticsCallback statisticsCallback) {
        globalStatisticsCallback = statisticsCallback;
    }

    public static void enableFFmpegSessionCompleteCallback(FFmpegSessionCompleteCallback ffmpegSessionCompleteCallback) {
        globalFFmpegSessionCompleteCallback = ffmpegSessionCompleteCallback;
    }

    public static FFmpegSessionCompleteCallback getFFmpegSessionCompleteCallback() {
        return globalFFmpegSessionCompleteCallback;
    }

    public static void enableFFprobeSessionCompleteCallback(FFprobeSessionCompleteCallback ffprobeSessionCompleteCallback) {
        globalFFprobeSessionCompleteCallback = ffprobeSessionCompleteCallback;
    }

    public static FFprobeSessionCompleteCallback getFFprobeSessionCompleteCallback() {
        return globalFFprobeSessionCompleteCallback;
    }

    public static void enableMediaInformationSessionCompleteCallback(MediaInformationSessionCompleteCallback mediaInformationSessionCompleteCallback) {
        globalMediaInformationSessionCompleteCallback = mediaInformationSessionCompleteCallback;
    }

    public static MediaInformationSessionCompleteCallback getMediaInformationSessionCompleteCallback() {
        return globalMediaInformationSessionCompleteCallback;
    }

    public static Level getLogLevel() {
        return activeLogLevel;
    }

    public static void setLogLevel(Level level) {
        if (level != null) {
            activeLogLevel = level;
            setNativeLogLevel(level.getValue());
        }
    }

    static String extractExtensionFromSafDisplayName(String safDisplayName) {
        String rawExtension = safDisplayName;
        if (safDisplayName.lastIndexOf(".") >= 0) {
            rawExtension = safDisplayName.substring(safDisplayName.lastIndexOf("."));
        }
        try {
            return new StringTokenizer(rawExtension, " .").nextToken();
        } catch (Exception e) {
            return "raw";
        }
    }

    public static String getSafParameter(Context context, Uri uri, String openMode) {
        int columnIndex;
        String displayName = "unknown";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst() && (columnIndex = cursor.getColumnIndex("_display_name")) >= 0) {
                    displayName = cursor.getString(columnIndex);
                }
            } finally {
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        int safId = uniqueIdGenerator.getAndIncrement();
        safIdMap.put(safId, new SAFProtocolUrl(Integer.valueOf(safId), uri, openMode, context.getContentResolver()));
        return "saf:" + safId + "." + extractExtensionFromSafDisplayName(displayName);
    }

    public static String getSafParameterForRead(Context context, Uri uri) {
        return getSafParameter(context, uri, "r");
    }

    public static String getSafParameterForWrite(Context context, Uri uri) {
        return getSafParameter(context, uri, "w");
    }

    private static int safOpen(int safId) {
        SAFProtocolUrl safUrl = null;
        try {
            safUrl = safIdMap.get(safId);
        } catch (Throwable th) {
        }
        if (safUrl != null) {
            try {
                ParcelFileDescriptor parcelFileDescriptor = safUrl.getContentResolver().openFileDescriptor(safUrl.getUri(), safUrl.getOpenMode());
                safUrl.setParcelFileDescriptor(parcelFileDescriptor);
                int fd = parcelFileDescriptor.getFd();
                safFileDescriptorMap.put(fd, safUrl);
                return fd;
            } catch (java.io.FileNotFoundException e) {
                android.util.Log.e(TAG, String.format("SAF id %d could not be opened.", Integer.valueOf(safId)));
                return 0;
            }
        }
        android.util.Log.e(TAG, String.format("SAF id %d not found.", Integer.valueOf(safId)));
        return 0;
    }

    private static int safClose(int fileDescriptor) {
        try {
            SAFProtocolUrl safProtocolUrl = safFileDescriptorMap.get(fileDescriptor);
            if (safProtocolUrl == null) {
                android.util.Log.e(TAG, String.format("SAF fd %d not found.", Integer.valueOf(fileDescriptor)));
            } else {
                ParcelFileDescriptor parcelFileDescriptor = safProtocolUrl.getParcelFileDescriptor();
                if (parcelFileDescriptor == null) {
                    android.util.Log.e(TAG, String.format("ParcelFileDescriptor for SAF fd %d not found.", Integer.valueOf(fileDescriptor)));
                } else {
                    safFileDescriptorMap.delete(fileDescriptor);
                    safIdMap.delete(safProtocolUrl.getSafId().intValue());
                    parcelFileDescriptor.close();
                    return 1;
                }
            }
        } catch (Throwable th) {
        }
        return 0;
    }

    public static int getSessionHistorySize() {
        return sessionHistorySize;
    }

    public static void setSessionHistorySize(int sessionHistorySize2) {
        if (sessionHistorySize2 >= 1000) {
            throw new IllegalArgumentException("Session history size must not exceed the hard limit!");
        }
        if (sessionHistorySize2 > 0) {
            sessionHistorySize = sessionHistorySize2;
            deleteExpiredSessions();
        }
    }

    private static void deleteExpiredSessions() {
        while (sessionHistoryList.size() > sessionHistorySize) {
            try {
                Session expiredSession = sessionHistoryList.remove(0);
                if (expiredSession != null) {
                    sessionHistoryMap.remove(Long.valueOf(expiredSession.getSessionId()));
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }

    static void addSession(Session session) {
        synchronized (sessionHistoryLock) {
            boolean sessionAlreadyAdded = sessionHistoryMap.containsKey(Long.valueOf(session.getSessionId()));
            if (!sessionAlreadyAdded) {
                sessionHistoryMap.put(Long.valueOf(session.getSessionId()), session);
                sessionHistoryList.add(session);
                deleteExpiredSessions();
            }
        }
    }

    public static Session getSession(long sessionId) {
        Session session;
        synchronized (sessionHistoryLock) {
            session = sessionHistoryMap.get(Long.valueOf(sessionId));
        }
        return session;
    }

    public static Session getLastSession() {
        synchronized (sessionHistoryLock) {
            if (sessionHistoryList.size() > 0) {
                return sessionHistoryList.get(sessionHistoryList.size() - 1);
            }
            return null;
        }
    }

    public static Session getLastCompletedSession() {
        synchronized (sessionHistoryLock) {
            for (int i = sessionHistoryList.size() - 1; i >= 0; i--) {
                Session session = sessionHistoryList.get(i);
                if (session.getState() == SessionState.COMPLETED) {
                    return session;
                }
            }
            return null;
        }
    }

    public static List<Session> getSessions() {
        LinkedList linkedList;
        synchronized (sessionHistoryLock) {
            linkedList = new LinkedList(sessionHistoryList);
        }
        return linkedList;
    }

    public static void clearSessions() {
        synchronized (sessionHistoryLock) {
            sessionHistoryList.clear();
            sessionHistoryMap.clear();
        }
    }

    public static List<FFmpegSession> getFFmpegSessions() {
        LinkedList<FFmpegSession> list = new LinkedList<>();
        synchronized (sessionHistoryLock) {
            for (Session session : sessionHistoryList) {
                if (session.isFFmpeg()) {
                    list.add((FFmpegSession) session);
                }
            }
        }
        return list;
    }

    public static List<FFprobeSession> getFFprobeSessions() {
        LinkedList<FFprobeSession> list = new LinkedList<>();
        synchronized (sessionHistoryLock) {
            for (Session session : sessionHistoryList) {
                if (session.isFFprobe()) {
                    list.add((FFprobeSession) session);
                }
            }
        }
        return list;
    }

    public static List<MediaInformationSession> getMediaInformationSessions() {
        LinkedList<MediaInformationSession> list = new LinkedList<>();
        synchronized (sessionHistoryLock) {
            for (Session session : sessionHistoryList) {
                if (session.isMediaInformation()) {
                    list.add((MediaInformationSession) session);
                }
            }
        }
        return list;
    }

    public static List<Session> getSessionsByState(SessionState state) {
        LinkedList<Session> list = new LinkedList<>();
        synchronized (sessionHistoryLock) {
            for (Session session : sessionHistoryList) {
                if (session.getState() == state) {
                    list.add(session);
                }
            }
        }
        return list;
    }

    public static LogRedirectionStrategy getLogRedirectionStrategy() {
        return globalLogRedirectionStrategy;
    }

    public static void setLogRedirectionStrategy(LogRedirectionStrategy logRedirectionStrategy) {
        globalLogRedirectionStrategy = logRedirectionStrategy;
    }

    public static String sessionStateToString(SessionState state) {
        return state.toString();
    }

    public static String[] parseArguments(String command) {
        Character previousChar;
        List<String> argumentList = new ArrayList<>();
        StringBuilder currentArgument = new StringBuilder();
        boolean singleQuoteStarted = false;
        boolean doubleQuoteStarted = false;
        for (int i = 0; i < command.length(); i++) {
            if (i > 0) {
                previousChar = Character.valueOf(command.charAt(i - 1));
            } else {
                previousChar = null;
            }
            char currentChar = command.charAt(i);
            if (currentChar == ' ') {
                if (singleQuoteStarted || doubleQuoteStarted) {
                    currentArgument.append(currentChar);
                } else if (currentArgument.length() > 0) {
                    argumentList.add(currentArgument.toString());
                    currentArgument = new StringBuilder();
                }
            } else if (currentChar == '\'' && (previousChar == null || previousChar.charValue() != '\\')) {
                if (singleQuoteStarted) {
                    singleQuoteStarted = false;
                } else if (doubleQuoteStarted) {
                    currentArgument.append(currentChar);
                } else {
                    singleQuoteStarted = true;
                }
            } else if (currentChar == '\"' && (previousChar == null || previousChar.charValue() != '\\')) {
                if (doubleQuoteStarted) {
                    doubleQuoteStarted = false;
                } else if (singleQuoteStarted) {
                    currentArgument.append(currentChar);
                } else {
                    doubleQuoteStarted = true;
                }
            } else {
                currentArgument.append(currentChar);
            }
        }
        int i2 = currentArgument.length();
        if (i2 > 0) {
            argumentList.add(currentArgument.toString());
        }
        return (String[]) argumentList.toArray(new String[0]);
    }

    public static String argumentsToString(String[] arguments) {
        if (arguments == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(arguments[i]);
        }
        return stringBuilder.toString();
    }
}
