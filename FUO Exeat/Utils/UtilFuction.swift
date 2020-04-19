//
//  UtilFuction.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 19/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation

class UtilFunction {
    
    // This is the user type
    // Student ==> 0
    // Student coordinator ==> 1
    // Security ==> 2
    // 3 ==> HOD
    class func userTypeCodeToName(_ code:Int)-> String {
        if code == 0 {
            return Constants.User.STUDENT
        }else if code == 1 {
            return Constants.User.COORDINATOR
        }else if code == 2 {
            return Constants.User.SECURITY
        }else if code == 3 {
            return Constants.User.HOD
        }else{
            return Constants.User.STUDENT
        }
    }
    
    class func userTypeNameToCode(_ name:String)-> Int {
        if name == Constants.User.STUDENT {
            return 0
        }else if name == Constants.User.COORDINATOR {
            return 1
        }else if name == Constants.User.SECURITY {
            return 2
        }else if name == Constants.User.HOD {
            return 3
        }else{
            return 0
        }
    }
}
