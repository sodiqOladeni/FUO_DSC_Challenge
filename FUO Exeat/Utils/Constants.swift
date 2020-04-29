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
        static let navigateToMainViewControllerId = "to_mainViewController"
        static let navigateToCreateNewExeatRequest = "to_newExeatRequest"
        static let studentRequestCellId = "StudentRequestCell"
        static let ftueNavController = "Ftue_NavController"
        static let mainTabBarController = "Main_TabBarController"
        static let storyBoardName = "Main"
    }
    
    struct Payment{
        static let USER_NOT_PAY = "User_Never_Pay"
        static let HAS_USER_PAY = "Has_User_Pay"
        static let you_need_payment = "Login successful! to continue using the app, you need to make a one time payment of NGN 1,000. \n\nThe payment is one time and you won\'t be asked to pay again."
    }
    
    struct Firebase {
        static let FCM_TOKEN = "Fcm_Token"
        static let FEATURE_REQUEST_COLLECTION = "Feature_Requests_And_Error_Report"
        static let REQUEST_ID = "Request_Id"
        static let REQUESTER_USER_ID = "Requester_User_Id"
        static let ALL_REQUESTS = "All_Student_Requests"
        
        static let requestId = "requestUniqueId"
        static let requestType = "requestType"
        static let requestStatus = "requestStatus"
        static let hasHODApproved = "hasHODApproved"
        static let approveHOD = "approveHOD"
        static let hodApproveTime = "hodApproveTime"
        static let approveCoordinator = "approveCoordinator"
        static let declineOrApproveTime = "declineOrApproveTime"
        static let departureDate = "departureDate"
        static let departureTime = "departureTime"
        static let arrivalDate = "arrivalDate"
        static let arrivalTime = "arrivalTime"
        static let location = "location"
        static let purpose = "purpose"
        static let requestTime = "requestTime"
        static let gateDepartureTime = "gateDepartureTime"
        static let gateArrivalTime = "gateArrivalTime"
        static let user = "user"
        
        static let firstName = "firstName"
        static let lastName = "lastName"
        static let schoolId = "schoolId"
        static let email = "email"
        static let phoneNumber = "phoneNumber"
        static let imageUri = "imageUri"
        static let hasUserPay = "hasUserPay"
        static let userPaymentRef = "userPaymentRef"
        static let fcmToken = "fcmToken"
        static let userWho = "userWho"
        static let uniqueId = "uniqueId"
        static let password = "password"
        static let userCreatedByWho = "userCreatedByWho"
        static let userCreatedWhen = "userCreatedWhen"
        static let programme = "programme"
        static let college = "college"
        static let dept = "dept"
        static let entryYear = "entryYear"
        static let hallOfResidence = "hallOfResidence"
        static let hallRoomNumber = "hallRoomNumber"
    }
    
    struct User {
        static let STUDENT = "Student"
        static let SECURITY = "Security"
        static let COORDINATOR = "Student_Coordinator"
        static let HOD = "HOD"
    }
    
    struct App {
        static let feature_request_message = "We are always working on improving the system, report error or submit new feature request.\n\n Your bug fix or feature request might be included in the next update"
    }
    
    struct Key {
        static let EXEAT_USER = "Exeat_User"
    }
    
    struct RequestType {
        static let Regular_Exeat = "Regular Exeat"
        static let Vacation_Exeat = "Vacation Exeat"
        static let Emergency_Exeat = "Emergency Exeat"
    }
}
