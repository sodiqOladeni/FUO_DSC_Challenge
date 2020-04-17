//
//  ViewController.swift
//  FUO Exeat
//
//  Created by Sodiq on 15/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//

import UIKit

class SliderViewController: UIViewController {

    let backgroundImageView = UIImageView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    @IBAction func btnLogin(_ sender: UIButton) {
        self.performSegue(withIdentifier: "to_loginVC", sender: nil)
    }
    
    @IBAction func btnSignup(_ sender: UIButton) {
        self.performSegue(withIdentifier: "to_signupVC", sender: nil)
    }
    
    private func setBackground(){
    
        view.addSubview(backgroundImageView)
        
        backgroundImageView.translatesAutoresizingMaskIntoConstraints = false
        backgroundImageView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        backgroundImageView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        backgroundImageView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        backgroundImageView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        backgroundImageView.image = UIImage(named: "SlideImage1")
        backgroundImageView.contentMode = .scaleAspectFill
    
    }
}

