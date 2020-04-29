//
//  RequestsViewController.swift
//  FUO Exeat
//
//  Created by sodiqOladeni on 20/04/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import CoreData

class RequestsViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var labelEmptyRequest: UILabel!
    private var dataController:DataController!
    private var fetchController:NSFetchedResultsController<Request>!
    private let dateFormatter = DateFormatter()
    private var requestStatus:String = DiffExeatStatus.COMPLETED.rawValue
    
    deinit {
        fetchController = nil
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        dateFormatter.dateFormat = "E, dd MMM yyyy' :'HH:mm"
//        tableView.delegate = self
//        setupFetchController()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if let indexPath = tableView.indexPathForSelectedRow {
            tableView.deselectRow(at: indexPath, animated: false)
            tableView.reloadRows(at: [indexPath], with: .fade)
        }
    }
    
    
    
    @IBAction func segmentalControlClickAction(_ sender: UISegmentedControl) {
        switch sender.selectedSegmentIndex {
        case 0:
            requestStatus = DiffExeatStatus.COMPLETED.rawValue
        case 1:
            requestStatus = DiffExeatStatus.PENDING.rawValue
        case 2:
            requestStatus = DiffExeatStatus.APPROVED.rawValue
        case 3:
            requestStatus = DiffExeatStatus.DECLINED.rawValue
        case 4:
            requestStatus = DiffExeatStatus.OUT_SCHOOL.rawValue
        default:
            requestStatus = DiffExeatStatus.COMPLETED.rawValue
        }
        tableView.reloadData()
    }
    
    fileprivate func allRequestType() -> String{
        return "\(DiffExeatStatus.PENDING.rawValue),\(DiffExeatStatus.APPROVED.rawValue),\(DiffExeatStatus.DECLINED.rawValue),\(DiffExeatStatus.OUT_SCHOOL.rawValue),\(DiffExeatStatus.COMPLETED.rawValue)"
    }
    
    fileprivate func setupFetchController() {
        let fetchRequest:NSFetchRequest<Request> = Request.fetchRequest()
        
        let predicate = NSPredicate(format: "requestStatus == %@", requestStatus)
        fetchRequest.predicate = predicate
        
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

extension RequestsViewController: UITableViewDelegate, UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return fetchController.sections?.count ?? 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return fetchController.sections?[section].numberOfObjects ?? 1
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

extension RequestsViewController:NSFetchedResultsControllerDelegate{
    
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
