import { Component, OnInit, Input } from '@angular/core';
import { HeaterStatus } from '../../../@core/data/heater.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'ngx-pidvaluesdialog',
  templateUrl: './pidvaluesdialog.component.html',
  styleUrls: ['./pidvaluesdialog.component.scss']
})
export class PidvaluesdialogComponent implements OnInit {

  constructor(private activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  onSubmit() {
    console.log(this.heaterStatus);
    this.activeModal.close(this.heaterStatus);
  }

  onCancel() {
    this.activeModal.dismiss();
  }

  @Input() heaterStatus: HeaterStatus;

  

}
