package com.nefi.chainrat.server.log;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry
{
    public Date timeStamp;
    public String timeStampString;
    public String logString;
    public Object sender;
    public String msg;
    public LogType type;
    public Thread thread;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public LogEntry(Object sender, String msg, LogType type, Thread thread)
    {
        this.sender = sender;
        this.msg = msg;
        this.type = type;
        this.timeStamp = new Date();
        this.timeStampString = AddBraces(dateFormat.format(timeStamp));
        this.logString = GetLogString(sender, msg, type, thread);
        this.thread = thread;
    }

    private String AddBraces(String s){
        return "[" + s + "]";
    }

    private String GetLogString(Object sender, String msg, LogType type, Thread thread)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date timeStamp = new Date();
        String log = "";

        log += AddBraces(dateFormat.format(timeStamp));

        switch (type)
        {
            case LOG:
                log += "[LOG]";
                break;
            case WARNING:
                log += "[WARNING]";
                break;
            case ERROR:
                log += "[ERROR]";
                break;
            default:
                throw new NotImplementedException();
        }

        log += AddBraces(sender.getClass().getName());
        log += AddBraces(thread.getName());
        log += msg;

        return log;
    }


}

