package org.bigbluebutton.conference;

import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;

public interface ISharedObjectFactory {
	boolean hasSharedObject(String soName, IScope scope);
	boolean createSharedObject(String soName, IScope scope, boolean persistent);
	ISharedObject getSharedObject(String soName, IScope scope);
	boolean clearSharedObjects(String soName, IScope scope);
}
