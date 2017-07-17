// IExtensionManagerCallback.aidl
package edu.cmu.chimps.messageontap_api;

import edu.cmu.chimps.messageontap_api.MessageData;

interface IExtensionManager {

    void sendResponse(in MessageData data);

}
