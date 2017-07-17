// IExtensionManager.aidl
package edu.cmu.chimps.messageontap_api;

import edu.cmu.chimps.messageontap_api.IExtensionManager;
import edu.cmu.chimps.messageontap_api.ExtensionData;
import edu.cmu.chimps.messageontap_api.MessageData;

interface IExtension {
    void onMessageReceived(in MessageData data);
    ExtensionData getExtensionData();

    void registerManager(IExtensionManager cb);
    void unregisterManager(IExtensionManager cb);
}
