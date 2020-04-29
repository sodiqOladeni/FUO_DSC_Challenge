//
//  StudentRequestCell.swift
//  FUO Exeat
//
//  Created by mac on 23/01/2020.
//  Copyright © 2020 NotZero Technologies. All rights reserved.
//

import UIKit
import BEMCheckBox

class StudentRequestCell: UITableViewCell {

    @IBOutlet weak var requestStatusLabel: UILabel!
    @IBOutlet weak var requestDateLabel: UILabel!
    @IBOutlet weak var departureDateLabel: UILabel!
    @IBOutlet weak var arrivalDateLabel: UILabel!
    @IBOutlet weak var departureTimeLabel: UILabel!
    @IBOutlet weak var arrivalTimeLabel: UILabel!
    @IBOutlet weak var requestTypeLabel: UILabel!
    @IBOutlet weak var hodCoirmationCheckBox: BEMCheckBox!
    @IBOutlet weak var requestPurposeTextView: UITextView!
    @IBOutlet weak var requestLocationTextView: UITextView!
    
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        hodCoirmationCheckBox.boxType = .square
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
