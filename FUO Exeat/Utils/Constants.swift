//
//  Constants.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 18/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation

class Constants {
    struct Identifiers {
        static let navigateToSignupId = "to_signupVC"
        static let navigateToLoginId = "to_loginVC"
        static let navigateToForgotPasswordId = "to_forgetPasswordVC"
        static let navigateToCardPaymentId = "to_cardPaymentViewController"
        static let navigateToMainViewControllerId = "to_mainViewController"
    }
    
    struct Payment{
        static let USER_NOT_PAY = "User_Never_Pay"
        static let HAS_USER_PAY = "Has_User_Pay"
        static let you_need_payment = "Login successful! to continue using the app, you need to make a one time payment of NGN 1,000. \n\nThe payment is one time and you won\'t be asked to pay again."
    }
    
    struct Firebase {
        static let FCM_TOKEN = "Fcm_Token"
    }
    
    struct User {
        static let STUDENT = "Student"
        static let SECURITY = "Security"
        static let COORDINATOR = "Student_Coordinator"
        static let HOD = "HOD"
    }
}
