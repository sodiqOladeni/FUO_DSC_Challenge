//
//  UIViewController+Extension.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 18/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import Foundation
import JGProgressHUD

extension UIViewController{
    
    func showDialog(title:String, message: String) {
            let alertVC = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alertVC.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
            self.present(alertVC, animated: true, completion: nil)
    }
    
    func isValidURL(_ string:String?) -> Bool {
        if let safeString = string {
            if let url = NSURL(string: safeString) {
                return UIApplication.shared.canOpenURL(url as URL)
            }
        }
        return false
    }
    
    @objc func tapDone(sender: Any) {
        self.view.endEditing(true)
    }
    
    func startProgressDialog(progressMessage:String) ->JGProgressHUD {
        var hud:JGProgressHUD?
        if hud == nil {
            hud = JGProgressHUD(style: .dark)
            hud!.textLabel.text = progressMessage
        }
        hud!.show(in: self.view)
        return hud!
    }
    
    func stopProgressDialog(hud:JGProgressHUD?){
        if let hud = hud{
            hud.dismiss()
        }
    }
}

extension UITextView {
    
    func addDoneButton(title: String, target: Any, selector: Selector) {
        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: UIScreen.main.bounds.size.width,height: 44.0))
        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        let barButton = UIBarButtonItem(title: title, style: .plain, target: target, action: selector)
        toolBar.setItems([flexible, barButton], animated: false)
        self.inputAccessoryView = toolBar
    }
}
