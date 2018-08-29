import { Component, OnInit, Input } from '@angular/core';
import { HeaterStatus } from '../../../@core/data/heater.service';

@Component({
  selector: 'ngx-pidvaluesdialog',
  templateUrl: './pidvaluesdialog.component.html',
  styleUrls: ['./pidvaluesdialog.component.scss']
})
export class PidvaluesdialogComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  @Input() heaterStatus: HeaterStatus;

  

}
