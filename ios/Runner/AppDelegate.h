#import <Flutter/Flutter.h>
#import <UIKit/UIKit.h>
#import <GNSMessages.h>

static NSString * const kMyAPIKey = @"AIzaSyAjil3RxeOuO62vw8v9C-QbWiOwA1Spg1w";
@interface AppDelegate : FlutterAppDelegate
/**
 * @property
 * This class lets you check the permission state of Nearby for the app on the current device.  If
 * the user has not opted into Nearby, publications and subscriptions will not function.
 */
@property(nonatomic) GNSPermission *nearbyPermission;

/**
 * @property
 * The message manager lets you create publications and subscriptions.  They are valid only as long
 * as the manager exists.
 */
@property(nonatomic) GNSMessageManager *messageMgr;
@property(nonatomic) id<GNSPublication> publication;
@property(nonatomic) id<GNSSubscription> subscription;
@property(nonatomic) UINavigationController *navController;
@end
