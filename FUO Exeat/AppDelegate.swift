//
//  AppDelegate.swift
//  FUO Exeat
//
//  Created by Sodiq on 15/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//

import UIKit
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        FirebaseApp.configure()
        
        if #available(iOS 13.0, *) { } else {
            let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
            var rootViewController:UIViewController!
            
            if Auth.auth().currentUser == nil {
                rootViewController = mainStoryboard.instantiateViewController(identifier: "Ftue_NavController") as! UINavigationController
            }else {
                rootViewController = mainStoryboard.instantiateViewController(identifier: "Main_TabBarController") as! UITabBarController
                
            }
            
            self.window = UIWindow(frame: UIScreen.main.bounds)
            self.window!.rootViewController = rootViewController
            self.window!.makeKeyAndVisible()
        }
        return true
    }
    
}

