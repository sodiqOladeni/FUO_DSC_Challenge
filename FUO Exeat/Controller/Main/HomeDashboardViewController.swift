//
//  HomeDashboardViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import CoreData
import FirebaseFirestore
import FirebaseFirestoreSwift

class HomeDashboardViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var labelEmptyRequest: UILabel!
    var dataController:DataController!
    private var fetchController:NSFetchedResultsController<Request>!
    private let dateFormatter = DateFormatter()
    private var user:ExeatUser!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        * EEEE - Display the long letter version of the weekday
//        * MMM - Display the letter abbreviation of the nmotny
//        * dd-yyyy - day in month and full year numerically
//        * HH:mm - Hours and minutes in 24hr format
        dateFormatter.dateFormat = "E, dd MMM yyyy' :'HH:mm"
        
        user = FirebaseClient.getUserFromUserDefault()
        tableView.delegate = self
        setupFetchController()
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if let indexPath = tableView.indexPathForSelectedRow {
            tableView.deselectRow(at: indexPath, animated: false)
            tableView.reloadRows(at: [indexPath], with: .fade)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        fetchController = nil
    }
    
    @IBAction func itemCreateNewRequest(_ sender: UIBarButtonItem) {
        performSegue(withIdentifier: Constants.Identifiers.navigateToCreateNewExeatRequest, sender: nil)
    }
    
    fileprivate func setupFetchController() {
        let fetchRequest:NSFetchRequest<Request> = Request.fetchRequest()
        let sortDescriptor = NSSortDescriptor(key: "requestTime", ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        fetchController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        fetchController.delegate = self
        
        do {
            try fetchController.performFetch()
        } catch {
            fatalError("The fetch could not be performed")
        }
    }

}

extension HomeDashboardViewController: UITableViewDelegate, UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return fetchController.sections?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return fetchController.sections?[section].numberOfObjects ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let aRequest = fetchController.object(at: indexPath)
        let aCell = tableView.dequeueReusableCell(withIdentifier: Constants.Identifiers.studentRequestCellId, for: indexPath) as! StudentRequestCell
        
        // Configure Cell
        aCell.requestStatusLabel.text = aRequest.requestStatus
        aCell.requestDateLabel.text = dateFormatter.string(from: Date(timeIntervalSince1970:TimeInterval(aRequest.requestTime)))
        aCell.departureDateLabel.text = aRequest.departureDate
        aCell.arrivalDateLabel.text = aRequest.arrivalDate
        aCell.departureTimeLabel.text = aRequest.departureTime
        aCell.arrivalTimeLabel.text = aRequest.arrivalTime
        aCell.requestTypeLabel.text = aRequest.requestType
        aCell.hodCoirmationCheckBox.setOn(aRequest.hasHODApproved, animated: true)
        aCell.requestPurposeTextView.text = aRequest.purpose
        aCell.requestLocationTextView.text = aRequest.location
        
        return aCell
    }
    
}

extension HomeDashboardViewController:NSFetchedResultsControllerDelegate{
    
    func controllerWillChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
        tableView.beginUpdates()
    }
    
    func controllerDidChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
        tableView.endUpdates()
    }
    
    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange anObject: Any, at indexPath: IndexPath?, for type: NSFetchedResultsChangeType, newIndexPath: IndexPath?) {
        switch type {
        case .insert:
            tableView.insertRows(at: [indexPath!], with: .fade)
        case .delete:
            tableView.deleteRows(at: [indexPath!], with: .fade)
        case .update:
            tableView.reloadRows(at: [indexPath!], with: .fade)
        case .move:
            tableView.moveRow(at: indexPath!, to: newIndexPath!)
        @unknown default:
            fatalError("")
        }
    }
    
    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange sectionInfo: NSFetchedResultsSectionInfo, atSectionIndex sectionIndex: Int, for type: NSFetchedResultsChangeType) {
        
        let indexSet = IndexSet(integer: sectionIndex)
        switch type {
        case .insert: tableView.insertSections(indexSet, with: .fade)
        case .delete: tableView.deleteSections(indexSet, with: .fade)
        case .update, .move:
            fatalError("Invalid change type in controller(_:didChange:atSectionIndex:for:). Only .insert or .delete should be possible.")
        @unknown default:
            fatalError("")
        }
    }
}
