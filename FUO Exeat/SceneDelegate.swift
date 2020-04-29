//
//  SceneDelegate.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 17/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import FirebaseAuth
import Firebase


class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    var dataController = DataController(modelName: "FUO_Exeat")
    private var fireStoreListener:ListenerRegistration!
    private var firestoreDB:Firestore!
    private var user:ExeatUser?
    
    deinit {
        fireStoreListener.remove()
    }
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        
        let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
        var rootViewController:UIViewController!
        dataController.load()
        firestoreDB = Firestore.firestore()
        
        if Auth.auth().currentUser == nil {
            rootViewController = mainStoryboard.instantiateViewController(withIdentifier: "Ftue_NavController") as! UINavigationController
        } else {
            rootViewController = mainStoryboard.instantiateViewController(withIdentifier: "Main_TabBarController") as! UITabBarController
            let homeDashboardNavController = rootViewController.children.first as! UINavigationController
            let homeDashboardViewController = homeDashboardNavController.topViewController as! HomeDashboardViewController
            homeDashboardViewController.dataController = dataController
            
            user = FirebaseClient.getUserFromUserDefault()
            configureFirebaseListener(user: user)
        }
        
        self.window = UIWindow(windowScene: windowScene)
        self.window!.rootViewController = rootViewController
        self.window!.makeKeyAndVisible()
    }
    
    
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // Save changes in the application's managed object context when the application transitions to the background.
        saveViewContext()
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        saveViewContext()
    }
    
    func saveViewContext(){
        try? dataController.viewContext.save()
    }
    
    fileprivate func configureFirebaseListener(user:ExeatUser?){
        guard let safeUser = user else { return }
        fireStoreListener = Firestore.firestore().collection(Constants.Firebase.ALL_REQUESTS)
            .whereField("user.uniqueId", isEqualTo: safeUser.uniqueId!).addSnapshotListener({ (querySnapshot, error) in
                guard let snapshot = querySnapshot else { return }
                
                snapshot.documentChanges.forEach { diff in
                    if (diff.type == .added) {
                        self.createSnapshotFromCoredataRequest(snapshot: snapshot)
                        print("New city: \(diff.document.data())")
                    }
                    
                    if (diff.type == .modified) {
                        print("Modified city: \(diff.document.data())")
                    }
                    
                    if (diff.type == .removed) {
                        print("Removed city: \(diff.document.data())")
                    }
                }
                
                do{
                    try self.dataController.viewContext.save()
                }catch let e {
                    print(e.localizedDescription)
                }
            })
    }
    
    fileprivate func createSnapshotFromCoredataRequest(snapshot:QuerySnapshot){
        var _ = Request(context: dataController.viewContext)
    }
}
