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
import FirebaseFirestoreSwift
import FirebaseStorage

class FirebaseClient {
    
    class func loginUser(userType:String, email:String, password:String, completionHandler: @escaping (Bool, String?, ExeatUser?) -> Void){
        Auth.auth().signIn(withEmail: email, password: password) { (authDataResult, authError) in
            
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
                    
                    FirebaseClient.sendVerificationEmail { (isSucess, message) in
                        if isSucess{
                            DispatchQueue.main.async {
                                completionHandler(false, message, nil)
                            }
                        }else{
                            DispatchQueue.main.async {
                                completionHandler(false, message, nil)
                            }
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
                let result = Result{
                    try querySnapShotDocument!.documents.compactMap{
                        try $0.data(as: ExeatUser.self)
                    }
                }
                switch result {
                case .success(let user):
                    saveToUserDefault(exeatUser: user[0])
                    completionHandler(true, "User information found", user[0])
                case .failure(let error):
                    completionHandler(false, "Decoding error \(error.localizedDescription)", nil)
                }
            }
        }
    }
    
    class func logoutUser(completion:@escaping ()->Void) {
        try? Auth.auth().signOut()
        let defaults = UserDefaults.standard
        defaults.dictionaryRepresentation().keys.forEach { key in
            defaults.removeObject(forKey: key)
        }
        completion()
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
    
    class func saveFeatureRequest(userRequestedFeature:String, user:ExeatUser, completionHandler: @escaping (Bool, String) ->Void) {
        let data = FeatureRequestsAndErrorReport(featureOrError: userRequestedFeature, user: user)
        let firebaseDoc = Firestore.firestore().collection(Constants.Firebase.FEATURE_REQUEST_COLLECTION).document()
        
        do {
            try firebaseDoc.setData(from: data){ (error) in
                if let error = error {
                    DispatchQueue.main.async {
                        completionHandler(false, error.localizedDescription)
                    }
                }else{
                    DispatchQueue.main.async {
                        completionHandler(true, "Your request was successfully recorded, Enjoy your day :)")
                    }
                }
            }
        } catch  {
            DispatchQueue.main.async {
                completionHandler(false, error.localizedDescription)
            }
        }
    }
    
    class func saveToUserDefault(exeatUser:ExeatUser) {
        let encodedExeatUser = try? JSONEncoder().encode(exeatUser)
        UserDefaults.standard.set(encodedExeatUser, forKey: Constants.Key.EXEAT_USER)
    }
    
    class func getUserFromUserDefault() ->ExeatUser? {
        let exeatUserEncoded = UserDefaults.standard.object(forKey: Constants.Key.EXEAT_USER) as! Data
        return try! JSONDecoder().decode(ExeatUser.self, from: exeatUserEncoded)
    }
    
    class func resetPassword(email:String, password:String, completionhandler: @escaping (Bool, String)->Void){
        let user = Auth.auth().currentUser
        let credential = EmailAuthProvider.credential(withEmail: email, password: password)
        
        if let user = user {
            user.reauthenticate(with: credential) { (authDateResult, error) in
                guard error == nil else {
                    DispatchQueue.main.async {
                        completionhandler(false, error!.localizedDescription)
                    }
                    return
                }
                user.updatePassword(to: password) { (error) in
                    guard error == nil else {
                        DispatchQueue.main.async {
                            completionhandler(false, error!.localizedDescription)
                        }
                        return
                    }
                    
                    DispatchQueue.main.async {
                        completionhandler(true, "Password has been successfully changed")
                    }
                }
            }
        }else{
            DispatchQueue.main.async {
                completionhandler(false, "User credential not found")
            }
        }
        
    }
    
    class func updateUserData(user:ExeatUser, completionHandler: @escaping (Bool, String) -> Void){
        let doc = Firestore.firestore().collection(UtilFunction.userTypeCodeToName(user.userWho)).document(user.uniqueId!)
        do {
            try doc.setData(from: user){ (error) in
                if let error = error {
                    DispatchQueue.main.async {
                        completionHandler(false, error.localizedDescription)
                    }
                }else{
                    saveToUserDefault(exeatUser: user)
                    DispatchQueue.main.async {
                        completionHandler(true, "Your profile has been successfully updated, Enjoy your day :)")
                    }
                }
            }
        } catch  {
            DispatchQueue.main.async {
                completionHandler(false, error.localizedDescription)
            }
        }
    }
    
    class func createAccountForNewUser(user:ExeatUser, completionHandler: @escaping (Bool, String) -> Void){
        var newUserCopy = user
        Auth.auth().createUser(withEmail: user.email, password: user.password) { (authDataResult, error) in
            
            guard error == nil else{
                DispatchQueue.main.async {
                    completionHandler(false, "Error while creating user account")
                }
                return
            }
            
            if let authDataResult = authDataResult{
                newUserCopy.uniqueId = authDataResult.user.uid
                saveUserImage(user: newUserCopy) { (isSuccess, message) in
                    if isSuccess {
                        DispatchQueue.main.async {
                            completionHandler(isSuccess, message)
                        }
                    }else{
                        DispatchQueue.main.async {
                            completionHandler(isSuccess, message)
                        }
                    }
                }
            }
        }
    }
    
    class func saveUserImage(user:ExeatUser, completionHandler: @escaping (Bool, String) -> Void){
        var newUserCopy = user
        let storageRef = Storage.storage().reference().child("FUO_Exaet_Main").child(UtilFunction.userTypeCodeToName(user.userWho))
            .child("Profile_Images").child(user.uniqueId!).child(" .jpg")
        
        storageRef.putFile(from: URL(fileURLWithPath: user.imageUri), metadata: nil) { (storageMetaData, error) in
            guard error == nil else {
                DispatchQueue.main.async {
                    completionHandler(false, error!.localizedDescription)
                }
                return
            }
            
            storageRef.downloadURL { (url, error) in
                guard let downloadURL = url else {
                    DispatchQueue.main.async {
                        completionHandler(false, error!.localizedDescription)
                    }
                    return
                }
                
                newUserCopy.imageUri = try! String(contentsOf: downloadURL)
                // Save userData to Firestore
                saveUserImage(user: newUserCopy) { (isSuccess, message) in
                    if isSuccess {
                        DispatchQueue.main.async {
                            completionHandler(isSuccess, message)
                        }
                    }else{
                        DispatchQueue.main.async {
                            completionHandler(isSuccess, message)
                        }
                    }
                }
            }
        }
    }
    
    class func saveUserData(user:ExeatUser, completionHandler: @escaping (Bool, String) -> Void){
        let docRef = Firestore.firestore().collection(UtilFunction.userTypeCodeToName(user.userWho)).document(user.uniqueId!)
        
        do {
            try docRef.setData(from: user){(error) in
                if let error = error{
                    DispatchQueue.main.async {
                        completionHandler(false, error.localizedDescription)
                    }
                }else{
                    
                    sendVerificationEmail { (isSuccess, message) in
                        if isSuccess {
                            DispatchQueue.main.async {
                                completionHandler(isSuccess, "User successfully created, please check your email to verify your account")
                            }
                        }else{
                            DispatchQueue.main.async {
                                completionHandler(isSuccess, "User successfully created but unable to send verification email")
                            }
                        }
                    }
                }
            }
        } catch {
            DispatchQueue.main.async {
                completionHandler(false, error.localizedDescription)
            }
        }
    }
    
    class func sendVerificationEmail(completionHandler: @escaping (Bool, String)->Void){
        Auth.auth().currentUser?.sendEmailVerification(completion: { (verificationError) in
            guard verificationError == nil else {
                DispatchQueue.main.async {
                    completionHandler(false, verificationError?.localizedDescription ?? "Error sending verification message")
                }
                return
            }
            // Sign the user out to verify their account
            try? Auth.auth().signOut()
            DispatchQueue.main.async {
                completionHandler(true, "Verification Email has been sent.")
            }
            
        })
    }
    
    class func submitNewRequest(request:Request, userMakingTheRequest:ExeatUser, completion:@escaping(Bool, String)->Void) {
        let firestoreRef = Firestore.firestore().collection(Constants.Firebase.ALL_REQUESTS).document()
        request.requestUniqueId = firestoreRef.documentID
        let requestToUpload:[String:Any] = [
            Constants.Firebase.requestId: request.requestUniqueId,
            Constants.Firebase.requestType: request.requestType,
            Constants.Firebase.requestStatus: request.requestStatus,
            Constants.Firebase.hasHODApproved:request.hasHODApproved,
            Constants.Firebase.approveHOD: request.approveHOD,
            Constants.Firebase.hodApproveTime: request.hodApproveTime,
            Constants.Firebase.approveCoordinator: request.approveCoordinator,
            Constants.Firebase.declineOrApproveTime: request.declineOrApproveTime,
            Constants.Firebase.departureDate: request.departureDate,
            Constants.Firebase.departureTime: request.departureTime,
            Constants.Firebase.arrivalDate: request.arrivalDate,
            Constants.Firebase.arrivalTime: request.arrivalTime,
            Constants.Firebase.location: request.location,
            Constants.Firebase.purpose: request.purpose,
            Constants.Firebase.requestTime: request.requestTime,
            Constants.Firebase.gateDepartureTime: request.gateDepartureTime,
            Constants.Firebase.gateArrivalTime: request.gateArrivalTime,
            
            Constants.Firebase.user: [
                Constants.Firebase.firstName:userMakingTheRequest.firstName,
                Constants.Firebase.lastName:userMakingTheRequest.lastName,
                Constants.Firebase.schoolId:userMakingTheRequest.schoolId,
                Constants.Firebase.email:userMakingTheRequest.email,
                Constants.Firebase.phoneNumber:userMakingTheRequest.phoneNumber,
                Constants.Firebase.imageUri:userMakingTheRequest.imageUri,
                Constants.Firebase.hasUserPay:userMakingTheRequest.hasUserPay,
                Constants.Firebase.userPaymentRef:userMakingTheRequest.userPaymentRef,
                Constants.Firebase.fcmToken:userMakingTheRequest.fcmToken,
                Constants.Firebase.userWho:userMakingTheRequest.userWho,
                Constants.Firebase.uniqueId:userMakingTheRequest.uniqueId,
                Constants.Firebase.password:userMakingTheRequest.password,
                Constants.Firebase.userCreatedByWho:userMakingTheRequest.userCreatedByWho,
                Constants.Firebase.userCreatedWhen:userMakingTheRequest.userCreatedWhen,
                Constants.Firebase.programme:userMakingTheRequest.programme,
                Constants.Firebase.college:userMakingTheRequest.college,
                Constants.Firebase.dept:userMakingTheRequest.dept,
                Constants.Firebase.entryYear:userMakingTheRequest.entryYear,
                Constants.Firebase.hallOfResidence:userMakingTheRequest.hallOfResidence,
                Constants.Firebase.hallRoomNumber:userMakingTheRequest.hallRoomNumber
            ]
        ]
        
        firestoreRef.setData(requestToUpload, completion: { (error) in
            guard error == nil else{
                DispatchQueue.main.async {
                    completion(false, error!.localizedDescription)
                }
                return
            }
            
            DispatchQueue.main.async {
                completion(true, "Exeat request has been successfully submitted")
            }
        })
    }
}
