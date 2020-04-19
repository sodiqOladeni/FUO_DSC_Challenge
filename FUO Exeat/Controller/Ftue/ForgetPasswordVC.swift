//
//  ForgetPasswordVC.swift
//  FUO Exeat
//
//  Created by Sodiq on 16/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//

import UIKit
class ForgetPasswordVC: UIViewController {
    
    @IBOutlet weak var emailForgetPassword: UITextField!
    var userType:Int!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    @IBAction func btnResetPassword(_ sender: Any) {
        verifyEmail()
    }
    
    @IBAction func btnTo_login(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
    
    @IBAction func btnTo_Signup(_ sender: Any) {
        performSegue(withIdentifier: Constants.Identifiers.navigateToSignupId, sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier ==  Constants.Identifiers.navigateToLoginId {
            let loginViewController = segue.destination as! LoginViewController
            loginViewController.userType = self.userType
        }else if segue.identifier ==  Constants.Identifiers.navigateToSignupId {
            let signupViewController = segue.destination as! SignupViewController
            signupViewController.userType = self.userType
        }
    }
    
    fileprivate func verifyEmail(){
        guard emailForgetPassword.text != nil else {
            showDialog(title: "Email field can't be blank", message: "Kindly provide your valid email address")
            return
        }
        if let email = emailForgetPassword.text{
            resetUserPassword(email)
        }
    }
    
    fileprivate func resetUserPassword(_ email:String){
        FirebaseClient.resetuserpassword(email: email) { (isSuccess, message) in
            if isSuccess {
                self.displayPasswordResetSuccessDialog(message)
            }else{
                self.showDialog(title: "Error occur", message: message)
            }
        }
    }
    
    fileprivate func displayPasswordResetSuccessDialog(_ message:String){
        let alertController = UIAlertController(title: "Check your email", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (alertAction) in
            self.navigationController?.popViewController(animated: true)
        }))
    }
}
