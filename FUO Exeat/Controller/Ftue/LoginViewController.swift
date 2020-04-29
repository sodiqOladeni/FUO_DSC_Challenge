//
//  SliderViewController.swift
//  FUO Exeat
//
//  Created by Sodiq on 16/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//

import UIKit
import JGProgressHUD
import FirebaseAuth

class LoginViewController: UIViewController {
    
    var userType:Int! = 0
    var exeatUser:ExeatUser?
    private var progressHud:JGProgressHUD?
    @IBOutlet weak var loginEmailTextField: UITextField!
    @IBOutlet weak var loginPasswordTextField: UITextField!
    @IBOutlet weak var forgotPasswordLabel: UILabel!
    @IBOutlet weak var dontHaveAccountLabel: UILabel!
    @IBOutlet weak var labelUserLoginIdentity: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        let tapForgotPassword = UITapGestureRecognizer(target: self, action: #selector(LoginViewController.labelForgotPasswordTapped(sender:)))
        forgotPasswordLabel.isUserInteractionEnabled = true
        forgotPasswordLabel.addGestureRecognizer(tapForgotPassword)
        
        let tabSignup = UITapGestureRecognizer(target: self, action: #selector(LoginViewController.labelSignupTapped(sender:)))
        dontHaveAccountLabel.isUserInteractionEnabled = true
        dontHaveAccountLabel.addGestureRecognizer(tabSignup)
        updateIndetityLabelUserType()
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier ==  Constants.Identifiers.navigateToForgotPasswordId {
            let forgotPasswordViewController = segue.destination as! ForgetPasswordVC
            forgotPasswordViewController.userType = self.userType
        }else if segue.identifier ==  Constants.Identifiers.navigateToSignupId {
            let signupViewController = segue.destination as! SignupViewController
            signupViewController.userType = self.userType
        }
    }
    
    
    @IBAction func buttonLoginClicked(_ sender: UIButton) {
        checkFields()
    }
    
    @IBAction func labelForgotPasswordTapped(sender: UITapGestureRecognizer) {
        print("labelForgotPasswordTapped")
        performSegue(withIdentifier: Constants.Identifiers.navigateToForgotPasswordId, sender: nil)
    }
    
    @IBAction func labelSignupTapped(sender: UITapGestureRecognizer) {
        performSegue(withIdentifier: Constants.Identifiers.navigateToSignupId, sender: nil)
    }
    
    fileprivate func checkFields(){
        guard let email = loginEmailTextField.text, email.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Email field can't be blank", message: "Kindly provide your valid email address")
            return
        }
        
        guard let password = loginPasswordTextField.text, password.trimmingCharacters(in: .whitespacesAndNewlines).count > 0 else {
            showDialog(title: "Password field can't be blank", message: "Kindly provide your password to proceed ")
            return
        }
        
        loginUser(email: email, password: password)
        
    }
    
    fileprivate func loginUser(email:String, password:String){
        progressHud = startProgressDialog(progressMessage: "Please wait...")
        FirebaseClient.loginUser(userType: UtilFunction.userTypeCodeToName(userType), email: email, password: password) { (isSuccess, message, exeatUser) in
            self.stopProgressDialog(hud: self.progressHud)
            if isSuccess {
                self.navigateToMainScreen()
            }else{
                if message! == Constants.Payment.USER_NOT_PAY {
                    self.exeatUser = exeatUser
                    // self.displayPaymentActionAlert()
                    // Payment integration to be added to avoid login without payment
                    self.navigateToMainScreen()
                }else{
                    self.showDialog(title: "Error Occur", message: message!)
                }
            }
        }
    }
    
    fileprivate func displayPaymentActionAlert(){
        let alert = UIAlertController(title: "Continue to Payment", message: Constants.Payment.you_need_payment, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Make Payment", style: .default, handler: { (alertOkMakePayment) in
        // Goto Payment ViewController or trigger payment Action
            
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { (alertCancelNoPayment) in
            try? Auth.auth().signOut()
        }))
        self.present(alert, animated: true)
    }
    
    fileprivate func updateIndetityLabelUserType() {
        switch userType {
        case 0:
            labelUserLoginIdentity.text = "You are login in as Student"
        case 1:
            labelUserLoginIdentity.text = "You are login in as Student Coordinator"
        case 2:
            labelUserLoginIdentity.text = "You are login in as Security"
        case 3:
            labelUserLoginIdentity.text = "You are login in as HOD"
        default:
            labelUserLoginIdentity.text = "You are login in as Student"
        }
    }
    
    fileprivate func navigateToMainScreen(){
        let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let tabViewController = mainStoryboard.instantiateViewController(withIdentifier: Constants.Identifiers.mainTabBarController) as! UITabBarController
        self.present(tabViewController, animated: true, completion: nil)
    }
}
