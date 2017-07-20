package edu.cmu.chimps.iamhome;

import android.view.accessibility.AccessibilityNodeInfo;

public interface NodeInfoListener {
    void nodeInfoReceived(AccessibilityNodeInfo selectingView);
}
