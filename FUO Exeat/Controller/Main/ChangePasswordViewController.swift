//
//  ChangePasswordViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 21/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import JGProgressHUD

class ChangePasswordViewController: UIViewController {

    @IBOutlet weak var oldPasswordTextField: UITextField!
    @IBOutlet weak var newPasswordTextField: UITextField!
    @IBOutlet weak var confirmNewPassword: UITextField!
    private var hud:JGProgressHUD?
    private var userEmail:String!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        userEmail = "sodiqoladeni@gmail.com" //FirebaseClient.getUserFromUserDefault().email
    }
    

    @IBAction func buttonChangePasswordAction(_ sender: Any) {
        checkFields()
    }
    
    fileprivate func checkFields(){
        guard let oldPassword = oldPasswordTextField.text, oldPassword.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Old Password")
            return
        }
        
        guard let newPassword = newPasswordTextField.text, newPassword.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your valid New Password")
            return
        }
        
        guard let confirmPassword = confirmNewPassword.text, confirmPassword.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly confirm your New Password")
            return
        }
        
        if newPassword != confirmPassword {
            showDialog(title: "New Password fields must match", message: "Kindly retype your new Password in both fields")
            newPasswordTextField.text = ""
            confirmNewPassword.text = ""
            return
        }
        updateNewPassword(email: userEmail, password: confirmPassword)
    }
    
    
    fileprivate func updateNewPassword(email:String, password:String){
        hud = startProgressDialog(progressMessage: "Please wait...")
        FirebaseClient.resetPassword(email: email, password: password) { (isSuccess, message) in
            self.stopProgressDialog(hud: self.hud)
            if isSuccess {
                self.showDialog(title: "Password Changed", message: message)
            }else{
                self.showDialog(title: "Error Occur", message: message)
            }
        }
    }
}
