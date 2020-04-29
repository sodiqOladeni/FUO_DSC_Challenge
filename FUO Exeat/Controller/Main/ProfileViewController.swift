//
//  ProfileViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import JGProgressHUD

class ProfileViewController: UIViewController {
    
    @IBOutlet weak var userProfileImage: UIImageView!
    @IBOutlet weak var firstNameTextField: UITextField!
    @IBOutlet weak var lastNameTextField: UITextField!
    @IBOutlet weak var matricNumberTextField: UITextField!
    @IBOutlet weak var emailAddressTextField: UITextField!
    @IBOutlet weak var phoneNumberTextField: UITextField!
    @IBOutlet weak var collegeTextField: UITextField!
    @IBOutlet weak var deptTextField: UITextField!
    @IBOutlet weak var yearOfEntry: UITextField!
    @IBOutlet weak var hostelHall: UITextField!
    @IBOutlet weak var hostelRoomNumber: UITextField!
    private var exeatUser:ExeatUser?
    private var hud:JGProgressHUD?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        userProfileImage.layer.masksToBounds = true
        userProfileImage.layer.cornerRadius = userProfileImage.bounds.width / 2
        
        exeatUser = FirebaseClient.getUserFromUserDefault()
        if let exeatUser = exeatUser{
            displayUserData(exeatUser: exeatUser)
        }
    }
    
    
    fileprivate func displayUserData(exeatUser:ExeatUser){
        userProfileImage.load(url: URL(string: exeatUser.imageUri)!)
        firstNameTextField.text = exeatUser.firstName
        lastNameTextField.text = exeatUser.lastName
        matricNumberTextField.text = exeatUser.schoolId
        emailAddressTextField.text = exeatUser.email
        phoneNumberTextField.text = exeatUser.phoneNumber
        collegeTextField.text = exeatUser.college
        deptTextField.text = exeatUser.dept
        yearOfEntry.text = exeatUser.entryYear
        hostelHall.text = exeatUser.hallOfResidence
        hostelRoomNumber.text = exeatUser.hallRoomNumber
    }
    
    
    @IBAction func buttonUserUpdateProfile(_ sender: UIButton) {
        checkFields()
    }
    
    fileprivate func checkFields(){
        guard let firstName = firstNameTextField.text, firstName.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your First Name")
            return
        }
        
        guard let lastName = lastNameTextField.text, lastName.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Last Name")
            return
        }
        
        guard let matricNumber = matricNumberTextField.text, matricNumber.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Matric Number")
            return
        }
        
        guard let phoneNumber = phoneNumberTextField.text, phoneNumber.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Phone Number")
            return
        }
        
        guard let college = collegeTextField.text, college.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your College")
            return
        }
        
        guard let dept = deptTextField.text, dept.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Department")
            return
        }
        
        guard let yearOfEntry = yearOfEntry.text, yearOfEntry.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Year of Entry")
            return
        }
        
        guard let hostelHall = hostelHall.text, hostelHall.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Hall of Residence")
            return
        }
        
        guard let hostelRoomNumber = hostelRoomNumber.text, hostelRoomNumber.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your Room Number")
            return
        }
        
        exeatUser?.firstName = firstName
        exeatUser?.lastName = lastName
        exeatUser?.schoolId = matricNumber
        exeatUser?.phoneNumber = phoneNumber
        exeatUser?.college = college
        exeatUser?.dept = dept
        exeatUser?.entryYear = yearOfEntry
        exeatUser?.hallOfResidence = hostelHall
        exeatUser?.hallRoomNumber = hostelRoomNumber
        
        if let exeatUser = exeatUser {
            updateUserProfile(user: exeatUser)
        }
    }
    
    fileprivate func updateUserProfile(user:ExeatUser){
        hud = startProgressDialog(progressMessage: "Please wait...")
        FirebaseClient.updateUserData(user: user) { (isSuccess, message) in
            self.stopProgressDialog(hud: self.hud)
            if isSuccess {
                self.showDialog(title: "Profile Updated", message: message)
            }else{
                self.showDialog(title: "Error Occur", message: message)
            }
        }
    }
}
