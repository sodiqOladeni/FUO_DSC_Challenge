//
//  ExeatUser.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 18/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation

struct  ExeatUser: Codable {
    var firstName: String
    var lastName: String
    var schoolId: String
    var email: String
    var phoneNumber: String
    var imageUri: String
    var hasUserPay:Bool = true
    var userPaymentRef:String? = nil
    var fcmToken:String = "token_has_not_been_generated"
    // This is the user type
    // 0 ==> Student
    // 1 ==> Student coordinator
    // 2 ==> Security
    // 3 ==> HOD
    var userWho: Int
    var uniqueId: String? = nil
    var password: String
    var userCreatedByWho: String? = nil
    var userCreatedWhen:Int64? = nil
    //Non general information
    //These information only applies to student
    let programme: String = "Bsc"
    var college: String? = nil
    var dept: String? = nil
    var entryYear: String? = nil
    var hallOfResidence: String? = nil
    var hallRoomNumber: String? = nil
}
