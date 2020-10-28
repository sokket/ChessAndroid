package ru.oceancraft.chess;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.oceancraft.chess.game.LogLine;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<List<LogLine>> logs = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public void addMessage(Message message) {
        List<Message> list = messages.getValue();
        if (list == null)
            list = new ArrayList<>();
        list.add(message);
        messages.postValue(list);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void clearMessages() {
        List<Message> list = messages.getValue();
        if (list == null)
            list = new ArrayList<>();
        else
            list.clear();
        messages.postValue(list);
    }

    public void addLogLine(LogLine logLine) {
        List<LogLine> list = logs.getValue();
        if (list == null)
            list = new ArrayList<>();
        list.add(logLine);
        logs.postValue(list);
    }

    public void clearLogs() {
        List<LogLine> list = logs.getValue();
        if (list == null)
            list = new ArrayList<>();
        else
            list.clear();
        logs.postValue(list);
    }

    public LiveData<List<LogLine>> getLogs() {
        return logs;
    }
}
