#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.
    _messageMgr = [[GNSMessageManager alloc]
                    initWithAPIKey:kMyAPIKey
                    paramsBlock: ^(GNSMessageManagerParams *params) {
                      // This is called when microphone permission is enabled or disabled by the user.
                      params.microphonePermissionErrorHandler = ^(BOOL hasError) {
                        if (hasError) {
                         // showMessage(@"Nearby works better if microphone use is allowed");
                        }
                      };
                      // This is called when Bluetooth permission is enabled or disabled by the user.
                      params.bluetoothPermissionErrorHandler = ^(BOOL hasError) {
                        if (hasError) {
                         // showMessage(@"Nearby works better if Bluetooth use is allowed");
                        }
                      };
                      // This is called when Bluetooth is powered on or off by the user.
                      params.bluetoothPowerErrorHandler = ^(BOOL hasError) {
                        if (hasError) {
                          //showMessage(@"Nearby works better if Bluetooth is turned on");
                        }
                      };
                    }];
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
