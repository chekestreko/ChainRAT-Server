package com.nefi.chainrat.server.log;

import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.forms.frmMainController;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Log
{
    //region [Getter + Setter]
    public boolean ShowLog()
    {
        return showLog;
    }

    public void setShowLog(boolean showLog)
    {
        this.showLog = showLog;
    }

    public boolean ShowWarning() {
        return showWarning;
    }

    public void setShowWarning(boolean showWarning)
    {
        this.showWarning = showWarning;
    }

    public boolean ShowError()
    {
        return showError;
    }

    public void setShowError(boolean showError)
    {
        this.showError = showError;
    }

    public boolean AutoPrint() {
        return autoPrint;
    }

    public void setAutoPrint(boolean autoPrint) {
        this.autoPrint = autoPrint;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    //endregion

    private boolean showLog;
    private boolean showWarning;
    private boolean showError;
    private boolean autoPrint;
    private boolean autoFile;
    private File logFile;
    private String logFileName;
    private BufferedWriter writer = null;
    private List<LogEntry> entryList = new LinkedList<>();

    public static String newLine = System.getProperty("line.separator");

    public Log()
    {
        showLog = true;
        showWarning = true;
        showError = true;
        autoPrint = true;
        autoFile = true;

        logFileName = ("logs/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())) + ".txt";
        logFile = new File(logFileName);
    }

    public void d(Object sender, String msg, Thread thread){
        LogEntry myEntry = new LogEntry(sender, msg, LogType.LOG, thread);
        this.AddLog(myEntry);
    }
    public void d(Object sender, String msg){
        d(sender, msg, Thread.currentThread());
    }

    public void PrintToSystem(LogEntry entry)
    {
        System.out.println(entry.logString);
    }

    public void WriteToFile(LogEntry entry)
    {
        try
        {
            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(entry.logString + newLine);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public void WriteToTextfield(LogEntry entry){
        Platform.runLater(new Runnable() {
            public void run() {
                //Get instance of main Window
                frmMainController mainController = Main.getMainController();
                //Update Hashmap
                TextArea txt = mainController.tbLogOut;
                txt.appendText(entry.logString + Log.newLine);
            }
        });
    }

    public void AddLog(LogEntry entry){
        if(!entryList.contains(entry))
        {
            entryList.add(entry);
            if(autoPrint)
            {
                PrintToSystem(entry);
            }
            if(autoFile)
            {
                WriteToFile(entry);
            }
            WriteToTextfield(entry);
        }
    }

    public void RemoveLog(LogEntry entry){
        if(entryList.contains(entry))
            entryList.remove(entry);
    }

    public List<LogEntry> GetLogBySender(Object sender)
    {
        List<LogEntry> sortedEntries = new LinkedList<>();
        for (LogEntry e : entryList)
        {
            if(e.sender == sender)
                sortedEntries.add(e);
        }
        return  sortedEntries;
    }

    public List<LogEntry> GetLogByType(LogType type)
    {
        List<LogEntry> sortedEntries = new LinkedList<>();
        for (LogEntry e : entryList)
        {
            if(e.type == type)
                sortedEntries.add(e);
        }
        return  sortedEntries;
    }

    public void AddToTextbox(LogEntry entry, JTextField textBox){
        String oldLog = textBox.getText();
        String newLog = entry.logString;
        if(newLog != null)
            textBox.setText(oldLog + entry.logString + newLine);
    }




}
