package com.solstice.rssreader;

import java.util.List;

/*
 * Interface to allow AsyncTaskCaller to access MainActivity UI
 */
public interface AsyncTaskCaller {
    public void onTaskCompleted(List<Entry> entries);
}
