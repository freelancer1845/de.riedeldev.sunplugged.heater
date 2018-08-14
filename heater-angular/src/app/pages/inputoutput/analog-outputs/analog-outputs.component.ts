import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';

@Component({
  selector: 'ngx-analog-outputs',
  templateUrl: './analog-outputs.component.html',
  styleUrls: ['./analog-outputs.component.scss']
})
export class AnalogOutputsComponent implements OnInit {

  title = "Analog Outputs";
  fields: IOField[] = [
  ];

  constructor() { }

  ngOnInit() {
    for (let i = 0; i < 4; i++) {
      this.fields.push({ name: "AO " + i, value: false, topic: 'aoaccess/' + i, isAnalog: true, readOnly: false });
    }
  }

}
