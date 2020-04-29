//
//  SettingsViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import JGProgressHUD

class SettingsViewController: UIViewController {
    
    @IBOutlet weak var notificationSwitch: UISwitch!
    private var hud:JGProgressHUD?
    private var exeatUser:ExeatUser?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        exeatUser = FirebaseClient.getUserFromUserDefault()
    }
    
    
    @IBAction func buttonSubmitFeatureRequest(_ sender: Any) {
        displayFeatureRequestAlert()
    }
    
    @IBAction func buttonLogout(_ sender: Any) {
        print("buttonLogout")
        FirebaseClient.logoutUser {
            self.navigateToFtueScreen()
            print("navigateToFtueScreen")
        }
    }
    
    fileprivate func displayFeatureRequestAlert(){
        let alertController = UIAlertController(title: "", message: Constants.App.feature_request_message, preferredStyle: .alert)
        alertController.addTextField { (textField : UITextField!) in
            textField.placeholder = "We love to hear from you!"
        }
        
        alertController.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        alertController.addAction(UIAlertAction(title: "Submit", style: .default, handler: { (uiAlertAtion) in
            let userInput = alertController.textFields?[0].text

            guard let safeUserInput = userInput, safeUserInput.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
                self.showDialog(title: "Email field can't be blank", message: "Kindly provide your valid email address")
                return
            }

            self.submitUserFeatureOrBugReport(userInput: safeUserInput)
        }))
        self.present(alertController, animated: true)
    }
    
    fileprivate func submitUserFeatureOrBugReport(userInput:String){
        guard let user = exeatUser else {return}
        hud = startProgressDialog(progressMessage: "Please wait ...")
        FirebaseClient.saveFeatureRequest(userRequestedFeature: userInput, user: user) { (isSuccess, message) in
            self.stopProgressDialog(hud: self.hud)
            if isSuccess {
                self.showDialog(title: "Report saved", message: message)
            }else{
                self.showDialog(title: "Error occur", message: message)
            }
        }
    }
    
    fileprivate func navigateToFtueScreen(){
        let mainStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let tabViewController = mainStoryboard.instantiateViewController(withIdentifier: Constants.Identifiers.ftueNavController) as! UINavigationController
        tabViewController.modalPresentationStyle = .fullScreen
        self.present(tabViewController, animated: true, completion: nil)
    }
}
