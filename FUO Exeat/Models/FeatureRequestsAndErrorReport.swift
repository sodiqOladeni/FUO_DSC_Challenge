//
//  FeatureRequestsAndErrorReport.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation
struct FeatureRequestsAndErrorReport:Codable {
    let featureOrError:String
    let user:ExeatUser
}
