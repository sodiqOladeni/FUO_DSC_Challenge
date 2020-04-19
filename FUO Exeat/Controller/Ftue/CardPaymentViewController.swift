//
//  CardPaymentViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 17/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit

class CardPaymentViewController: UIViewController {

    var userType:Int!
    var exeatUser:ExeatUser!
    @IBOutlet weak var cardNumberTextField: UITextField!
    @IBOutlet weak var cardCvvTextField: UITextField!
    @IBOutlet weak var cardMMTextField: UITextField!
    @IBOutlet weak var cardYYTextField: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    @IBAction func buttonMakePayment(_ sender: UIButton) {
        
    }
    
}
