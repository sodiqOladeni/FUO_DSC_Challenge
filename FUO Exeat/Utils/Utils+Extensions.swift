//
//  Utils+Extensions.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 19/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation
import UIKit


extension UIImageView {
    func load(url: URL) {
        DispatchQueue.global().async { [weak self] in
            if let data = try? Data(contentsOf: url) {
                if let image = UIImage(data: data) {
                    DispatchQueue.main.async {
                        self?.image = image
                    }
                }
            }
        }
    }
}
