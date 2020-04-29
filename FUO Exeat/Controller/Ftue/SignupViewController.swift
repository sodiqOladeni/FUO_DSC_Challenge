//
//  SignupViewController.swift
//  FUO Exeat
//
//  Created by Sodiq on 16/10/2019.
//  Copyright © 2019 NotZero Technologies. All rights reserved.
//
import UIKit
import JGProgressHUD

class SignupViewController: UIViewController {
    
    @IBOutlet weak var userPictureImageView: UIImageView!
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
    @IBOutlet weak var userPassword: UITextField!
    @IBOutlet weak var userConfirmPassword: UITextField!
    private var exeatUser:ExeatUser?
    private var hud:JGProgressHUD?
    var userType:Int = 0
    private let imagePicker = UIImagePickerController()
    private var userImagePath:String? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        imagePicker.delegate = self
        imagePicker.allowsEditing = false
        imagePicker.sourceType = .photoLibrary
        
        userPictureImageView.layer.masksToBounds = true
        userPictureImageView.layer.cornerRadius = userPictureImageView.bounds.width / 2
    }
    
    @IBAction func createAccountButton(_ sender: UIButton) {
        checkFields()
    }
    
    @IBAction func addPictureButton(_ sender: UIButton) {
        present(imagePicker, animated: true, completion: nil)
    }
    
    @IBAction func notStudentCannotSignup(_ sender: UIButton) {
        navigationController?.popViewController(animated: true)
    }
    
    @IBAction func alreadyHaveAnAccountLoginButton(_ sender: UIButton) {
        performSegue(withIdentifier: Constants.Identifiers.navigateToLoginId, sender: nil)
    }
    
    fileprivate func checkFields(){
        
        guard let safeImagePath = userImagePath  else {
            showDialog(title: "Your Image need to be added", message: "Kindly select your image to continue with the signup")
            return
        }
        
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
        
        guard let email = emailAddressTextField.text, matricNumber.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide valid Email")
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
        
        guard let newPassword = userPassword.text, newPassword.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly provide your valid New Password")
            return
        }
        
        guard let confirmPassword = userConfirmPassword.text, confirmPassword.trimmingCharacters(in: .whitespacesAndNewlines).count > 0  else {
            showDialog(title: "Field can't be blank", message: "Kindly confirm your New Password")
            return
        }
        
        if newPassword != confirmPassword {
            showDialog(title: "The two Password fields must match", message: "Kindly retype your Password in both fields")
            userPassword.text = ""
            userConfirmPassword.text = ""
            return
        }
        
        exeatUser = ExeatUser(
            firstName: firstName,
            lastName: lastName,
            schoolId: matricNumber,
            email: email,
            phoneNumber: phoneNumber,
            imageUri: safeImagePath,
            hasUserPay: true,
            fcmToken: "fcm_token_not_yet_captured",
            userWho: userType,
            password: confirmPassword,
            userCreatedByWho: "self",
            userCreatedWhen: Int64(Date().timeIntervalSince1970),
            college: college,
            dept: dept,
            entryYear: yearOfEntry,
            hallOfResidence: hostelHall,
            hallRoomNumber: hostelRoomNumber
        )
        
        
        if let exeatUser = exeatUser {
            createAccountForUser(user: exeatUser)
        }
    }
    
    fileprivate func createAccountForUser(user:ExeatUser){
        hud = startProgressDialog(progressMessage: "Please wait...")
        FirebaseClient.createAccountForNewUser(user: user) { (isSuccess, message) in
            self.stopProgressDialog(hud: self.hud)
            if isSuccess{
                let alertVC = UIAlertController(title: "User Created", message: message, preferredStyle: .alert)
                alertVC.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (alertAction) in
                    self.navigationController?.popViewController(animated: true)
                }))
                self.present(alertVC, animated: true, completion: nil)
            }else{
                self.showDialog(title: "Error Occur", message: message)
            }
        }
    }
}

extension SignupViewController:UINavigationControllerDelegate, UIImagePickerControllerDelegate{
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        let image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
        let imagePath = info[UIImagePickerController.InfoKey.imageURL] as! URL
        userPictureImageView.image = image
        userImagePath = try? String(contentsOf: imagePath)
        dismiss(animated: true, completion: nil)
    }
}
