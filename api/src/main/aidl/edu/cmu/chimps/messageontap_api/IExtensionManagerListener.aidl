// IExtensionManagerCallback.aidl
package edu.cmu.chimps.messageontap_api;

import edu.cmu.chimps.messageontap_api.MessageData;

interface IExtensionManagerListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void onMessageReceived(in MessageData data);
}
