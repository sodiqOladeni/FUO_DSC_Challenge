//
//  FirebaseClient.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 18/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation
import FirebaseAuth
import FirebaseFirestore

class FirebaseClient {
    
    class func loginUser(userType:String, email:String, password:String, completionHandler: @escaping (Bool, String?, ExeatUser?) -> Void){
        Auth.auth().signIn(withEmail: email, password: email) { (authDataResult, authError) in
            print("Data => \(authDataResult)")
            print("Error => \(authError)")
            guard authError == nil else {
                DispatchQueue.main.async {
                    completionHandler(false, authError?.localizedDescription ?? "Error while logging in", nil)
                }
                return
            }
            
            
            if let authDataResult = authDataResult {
                
                if authDataResult.user.isEmailVerified {
                    fetchUserDataFromFirestore(userType:userType, email: authDataResult.user.email!) { (isFetched, message, exeatUser) in
                        guard authError == nil else {
                            DispatchQueue.main.async {
                                completionHandler(false, authError?.localizedDescription ?? "Error while fetching user data", nil)
                            }
                            return
                        }
                        
                        if let exeatUser = exeatUser {
                            if exeatUser.hasUserPay {
                                DispatchQueue.main.async {
                                    completionHandler(true, "User found", exeatUser)
                                }
                            }else{
                                DispatchQueue.main.async {
                                    completionHandler(false, Constants.Payment.USER_NOT_PAY, exeatUser)
                                }
                            }
                        }else{
                            
                            try? Auth.auth().signOut()
                            DispatchQueue.main.async {
                                completionHandler(false, "User does not exist please confirm you are on the right page or signup instead", exeatUser)
                            }
                            
                        }
                    }
                    
                }else{
                    
                    authDataResult.user.sendEmailVerification { (verificationError) in
                        guard verificationError == nil else {
                            DispatchQueue.main.async {
                                completionHandler(false, verificationError?.localizedDescription ?? "Error sending verification message", nil)
                            }
                            return
                        }
                        
                        DispatchQueue.main.async {
                            completionHandler(false, "Your email has not been verified, please check your email and verify your account", nil)
                        }
                    }
                }
            }
        }
    }
    
    class func fetchUserDataFromFirestore(userType:String, email:String, completionHandler: @escaping (Bool, String?, ExeatUser?)-> Void){
        let query = Firestore.firestore().collection(userType).whereField("email", isEqualTo: email).limit(to: 1)
        
        query.getDocuments { (querySnapShotDocument, error) in
            guard error == nil else{
                completionHandler(false, "Error fetching user's information", nil)
                return
            }
            
            if querySnapShotDocument!.isEmpty{
                completionHandler(false, "Error fetching user's information", nil)
            }else{
                //                do {
                //                    try <#throwing expression#>
                //                } catch <#pattern#> {
                //                    <#statements#>
                //                }
                completionHandler(true, "Good thing happen in shit", nil)
                print(querySnapShotDocument!.documents)
            }
        }
    }
    
    class func logoutUser() {
        try? Auth.auth().signOut()
    }
    
    class func resetuserpassword(email:String, completion: @escaping (Bool, String) -> Void){
        Auth.auth().sendPasswordReset(withEmail: email) { (error) in
            guard error == nil else {
                DispatchQueue.main.async {
                    completion(false, error?.localizedDescription ?? "Unknown error while reseting password")
                }
                return
            }
            
            DispatchQueue.main.async {
                completion(true, "Password reset link has been sent to your email")
            }
        }
    }
}
