//
//  Utils+Extensions.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 19/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation


extension Encodable {
  /// Returns a JSON dictionary, with choice of minimal information
  func getDictionary() -> [String: Any]? {
    let encoder = JSONEncoder()

    guard let data = try? encoder.encode(self) else { return nil }
    return (try? JSONSerialization.jsonObject(with: data, options: .allowFragments)).flatMap { $0 as? [String: Any]
    }
  }
}

extension Decodable {
  /// Initialize from JSON Dictionary. Return nil on failure
  init?(dictionary value: [String:Any]){

    guard JSONSerialization.isValidJSONObject(value) else { return nil }
    guard let jsonData = try? JSONSerialization.data(withJSONObject: value, options: []) else { return nil }

    guard let newValue = try? JSONDecoder().decode(Self.self, from: jsonData) else { return nil }
    self = newValue
  }
}
