// IExtensionManager.aidl
package edu.cmu.chimps.messageontap_api;

import edu.cmu.chimps.messageontap_api.IExtensionManagerListener;
import edu.cmu.chimps.messageontap_api.ExtensionData;
import edu.cmu.chimps.messageontap_api.MessageData;

// Declare any non-default types here with import statements

interface IExtensionManager {
    oneway void updateInfo(in ExtensionData data);
    oneway void sendResponse(in MessageData data);

    void registerListener(IExtensionManagerListener cb);
    void unregisterListener(IExtensionManagerListener cb);
}
