package edu.cmu.chimps.iamhome.listeners;

import android.view.accessibility.AccessibilityNodeInfo;

public interface NodeInfoListener {
    void nodeInfoReceived(AccessibilityNodeInfo selectingView);
}
