package com.antonkarpenko.ffmpegkit;

import java.util.List;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public class FFmpegKit {
    static {
        AbiDetect.class.getName();
        FFmpegKitConfig.class.getName();
    }

    private FFmpegKit() {
    }

    public static FFmpegSession executeWithArguments(String[] arguments) {
        FFmpegSession session = FFmpegSession.create(arguments);
        FFmpegKitConfig.ffmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession execute(String command) {
        return executeWithArguments(FFmpegKitConfig.parseArguments(command));
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback) {
        return executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback);
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        return executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback, statisticsCallback);
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(FFmpegKitConfig.parseArguments(command), completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static void cancel() {
        FFmpegKitConfig.nativeFFmpegCancel(0L);
    }

    public static void cancel(long sessionId) {
        FFmpegKitConfig.nativeFFmpegCancel(sessionId);
    }

    public static List<FFmpegSession> listSessions() {
        return FFmpegKitConfig.getFFmpegSessions();
    }
}
