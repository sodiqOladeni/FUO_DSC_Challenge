//
//  ViewController.swift
//  FUO Exeat
//
//  Created by Sodiq on 15/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//

import UIKit

class SliderViewController: UIViewController {
    
    var userType = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.setNavigationBarHidden(true, animated: false)
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        navigationController?.setNavigationBarHidden(false, animated: false)
    }
    
    
    @IBAction func buttonLogin(_ sender: UIButton) {
        self.displayPaymentActionAlert()
    }
    
    @IBAction func buttonSignup(_ sender: UIButton) {
        self.performSegue(withIdentifier: Constants.Identifiers.navigateToSignupId, sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Identifiers.navigateToLoginId {
            let loginViewCOntroller = segue.destination as! LoginViewController
            loginViewCOntroller.userType = userType
        }
    }
    
    fileprivate func displayPaymentActionAlert(){
        let alert = UIAlertController(title: "Login as", message: "Identify your login identity to proceed", preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Student", style: .default, handler: { (alert) in
            self.userType = 0
            self.performSegue(withIdentifier: Constants.Identifiers.navigateToLoginId, sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "Security", style: .default, handler: { (alert) in
            self.userType = 2
            self.performSegue(withIdentifier: Constants.Identifiers.navigateToLoginId, sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "Student Coordinator", style: .default, handler: { (alert) in
            self.userType = 1
            self.performSegue(withIdentifier: Constants.Identifiers.navigateToLoginId, sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "HOD", style: .default, handler: { (alert) in
            self.userType = 3
            self.performSegue(withIdentifier: Constants.Identifiers.navigateToLoginId, sender: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler:nil))
        self.present(alert, animated: true)
    }
}

