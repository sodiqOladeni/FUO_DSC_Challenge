//
//  NewRequestViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import JGProgressHUD

class NewRequestViewController: UIViewController {
    
    private var dataController:DataController!
    @IBOutlet weak var departureDateAndTimePicker: UIDatePicker!
    @IBOutlet weak var arrivalDateAndTimePicker: UIDatePicker!
    @IBOutlet weak var locationTextView: UITextView!
    @IBOutlet weak var purposeTextView: UITextView!
    @IBOutlet weak var submitRequestButton: UIButton!
    private var hud:JGProgressHUD?
    private let dateFormatter = DateFormatter()
    private var userExeatTypeSelected:String?
    private var user:ExeatUser?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // user = FirebaseClient.getUserFromUserDefault()
    }
    
    @IBAction func exeatTypeButtonClicked(_ sender: UIButton) {
        displayExeatTypeSelection()
    }
    
    @IBAction func makeExeatRequestButtonClicked(_ sender: UIButton) {
        checkInfoFields()
    }
    
    fileprivate func checkInfoFields(){
        guard let user = user else { return }
        
        guard let exeatType = userExeatTypeSelected else {
            showDialog(title: "Select Exeat Type", message: "Exeat type must be selected to procceed")
            return
        }
        
        guard let location = locationTextView.text, location.trimmingCharacters(in: .whitespaces).count > 0 else {
            showDialog(title: "Field can't be empty", message: "Exeat location must provided")
            return
        }

        guard let purpose = purposeTextView.text, purpose.trimmingCharacters(in: .whitespaces).count > 0 else {
            showDialog(title: "Field can't be empty", message: "Exeat location must provided")
            return
        }
        
        let depertureDateAndTime = departureDateAndTimePicker.date
        let arrivalDateAndTime = arrivalDateAndTimePicker.date
        
        let newRequest = Request(context: dataController.viewContext)
        newRequest.requestType = exeatType
        newRequest.requestStatus = DiffExeatStatus.PENDING.rawValue
        newRequest.hasHODApproved = false
        
        dateFormatter.dateFormat = "E, dd MMM yyyy"
        newRequest.departureDate = dateFormatter.string(from: depertureDateAndTime)
        newRequest.arrivalDate = dateFormatter.string(from: arrivalDateAndTime)
        dateFormatter.dateFormat = "HH:mm SS"
        newRequest.departureTime = dateFormatter.string(from: depertureDateAndTime)
        newRequest.arrivalTime = dateFormatter.string(from: arrivalDateAndTime)
        newRequest.location = location
        newRequest.purpose = purpose
        newRequest.requestTime = Int64(Date().timeIntervalSince1970)
        
        // Setup request user
        newRequest.userFirstName = user.firstName
        newRequest.userLastName = user.lastName
        newRequest.userWho = Int32(user.userWho)
        newRequest.userSchoolId = user.schoolId
        newRequest.userEmail = user.email
        newRequest.userPhoneNumber = user.phoneNumber
        newRequest.userImageUri = user.imageUri
        newRequest.userHasUserPay = user.hasUserPay
        newRequest.userPaymentRef = user.userPaymentRef
        newRequest.userFcmToken = user.fcmToken
        newRequest.userUniqueId = user.uniqueId
        newRequest.userCreatedByWho = user.userCreatedByWho
        newRequest.userCreatedWhen = user.userCreatedWhen!
        newRequest.userProgramme = user.programme
        newRequest.userCollege = user.college
        newRequest.userDept = user.dept
        newRequest.userEntryYear = user.entryYear
        newRequest.userHallOfResidence = user.hallOfResidence
        newRequest.userHallRoomNumber = user.hallRoomNumber
        
        submitExeatRequest(request: newRequest, user: user)
    }
    
    fileprivate func submitExeatRequest(request:Request, user:ExeatUser){
        hud = startProgressDialog(progressMessage: "Please wait...")
        submitRequestButton.isEnabled = false
        FirebaseClient.submitNewRequest(request: request, userMakingTheRequest: user) { (isSuccess, message) in
            self.stopProgressDialog(hud: self.hud)
            self.submitRequestButton.isEnabled = false
            
            if isSuccess {
                let alertController = UIAlertController(title: "Request Submitted", message: message, preferredStyle: .alert)
                alertController.addAction(UIAlertAction(title: "Ok", style: .default, handler: {_ in
                    self.navigationController?.popViewController(animated: true)
                }))
            }else{
                self.showDialog(title: "Error occur", message: message)
            }
        }
    }
    
    fileprivate func displayExeatTypeSelection(){
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alertController.addAction(UIAlertAction(title: Constants.RequestType.Regular_Exeat, style: .default, handler: { (_) in
            self.userExeatTypeSelected = Constants.RequestType.Regular_Exeat
        }))
        
        alertController.addAction(UIAlertAction(title: Constants.RequestType.Vacation_Exeat, style: .default, handler: { (_) in
            self.userExeatTypeSelected = Constants.RequestType.Vacation_Exeat
        }))
        
        alertController.addAction(UIAlertAction(title: Constants.RequestType.Emergency_Exeat, style: .default, handler: { (_) in
            self.userExeatTypeSelected = Constants.RequestType.Emergency_Exeat
        }))
        alertController.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        self.present(alertController, animated: true, completion: nil)
    }
}
