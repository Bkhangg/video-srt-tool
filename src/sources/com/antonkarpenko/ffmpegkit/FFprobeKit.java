package com.antonkarpenko.ffmpegkit;

import java.util.List;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public class FFprobeKit {
    static {
        AbiDetect.class.getName();
        FFmpegKitConfig.class.getName();
    }

    private FFprobeKit() {
    }

    private static String[] defaultGetMediaInformationCommandArguments(String path) {
        return new String[]{"-v", "error", "-hide_banner", "-print_format", "json", "-show_format", "-show_streams", "-show_chapters", "-i", path};
    }

    public static FFprobeSession executeWithArguments(String[] arguments) {
        FFprobeSession session = FFprobeSession.create(arguments);
        FFmpegKitConfig.ffprobeExecute(session);
        return session;
    }

    public static FFprobeSession executeWithArgumentsAsync(String[] arguments, FFprobeSessionCompleteCallback completeCallback) {
        FFprobeSession session = FFprobeSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session);
        return session;
    }

    public static FFprobeSession executeWithArgumentsAsync(String[] arguments, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback) {
        FFprobeSession session = FFprobeSession.create(arguments, completeCallback, logCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session);
        return session;
    }

    public static FFprobeSession executeWithArgumentsAsync(String[] arguments, FFprobeSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFprobeSession session = FFprobeSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session, executorService);
        return session;
    }

    public static FFprobeSession executeWithArgumentsAsync(String[] arguments, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback, ExecutorService executorService) {
        FFprobeSession session = FFprobeSession.create(arguments, completeCallback, logCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session, executorService);
        return session;
    }

    public static FFprobeSession execute(String command) {
        return executeWithArguments(FFmpegKitConfig.parseArguments(command));
    }

    public static FFprobeSession executeAsync(String command, FFprobeSessionCompleteCallback completeCallback) {
        return executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback);
    }

    public static FFprobeSession executeAsync(String command, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback) {
        return executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback);
    }

    public static FFprobeSession executeAsync(String command, FFprobeSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFprobeSession session = FFprobeSession.create(FFmpegKitConfig.parseArguments(command), completeCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session, executorService);
        return session;
    }

    public static FFprobeSession executeAsync(String command, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback, ExecutorService executorService) {
        FFprobeSession session = FFprobeSession.create(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback);
        FFmpegKitConfig.asyncFFprobeExecute(session, executorService);
        return session;
    }

    public static MediaInformationSession getMediaInformation(String path) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path));
        FFmpegKitConfig.getMediaInformationExecute(session, AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
        return session;
    }

    public static MediaInformationSession getMediaInformation(String path, int waitTimeout) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path));
        FFmpegKitConfig.getMediaInformationExecute(session, waitTimeout);
        return session;
    }

    public static MediaInformationSession getMediaInformationAsync(String path, MediaInformationSessionCompleteCallback completeCallback) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path), completeCallback);
        FFmpegKitConfig.asyncGetMediaInformationExecute(session, AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
        return session;
    }

    public static MediaInformationSession getMediaInformationAsync(String path, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback, int waitTimeout) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path), completeCallback, logCallback);
        FFmpegKitConfig.asyncGetMediaInformationExecute(session, waitTimeout);
        return session;
    }

    public static MediaInformationSession getMediaInformationAsync(String path, MediaInformationSessionCompleteCallback completeCallback, ExecutorService executorService) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path), completeCallback);
        FFmpegKitConfig.asyncGetMediaInformationExecute(session, executorService, AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
        return session;
    }

    public static MediaInformationSession getMediaInformationAsync(String path, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback, ExecutorService executorService, int waitTimeout) {
        MediaInformationSession session = MediaInformationSession.create(defaultGetMediaInformationCommandArguments(path), completeCallback, logCallback);
        FFmpegKitConfig.asyncGetMediaInformationExecute(session, executorService, waitTimeout);
        return session;
    }

    public static MediaInformationSession getMediaInformationFromCommand(String command) {
        MediaInformationSession session = MediaInformationSession.create(FFmpegKitConfig.parseArguments(command));
        FFmpegKitConfig.getMediaInformationExecute(session, AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
        return session;
    }

    public static MediaInformationSession getMediaInformationFromCommandAsync(String command, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback, int waitTimeout) {
        return getMediaInformationFromCommandArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback, waitTimeout);
    }

    private static MediaInformationSession getMediaInformationFromCommandArgumentsAsync(String[] arguments, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback, int waitTimeout) {
        MediaInformationSession session = MediaInformationSession.create(arguments, completeCallback, logCallback);
        FFmpegKitConfig.asyncGetMediaInformationExecute(session, waitTimeout);
        return session;
    }

    public static List<FFprobeSession> listFFprobeSessions() {
        return FFmpegKitConfig.getFFprobeSessions();
    }

    public static List<MediaInformationSession> listMediaInformationSessions() {
        return FFmpegKitConfig.getMediaInformationSessions();
    }
}
