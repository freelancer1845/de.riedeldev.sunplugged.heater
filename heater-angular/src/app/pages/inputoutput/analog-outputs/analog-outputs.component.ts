import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';

@Component({
  selector: 'ngx-analog-outputs',
  templateUrl: './analog-outputs.component.html',
  styleUrls: ['./analog-outputs.component.scss']
})
export class AnalogOutputsComponent implements OnInit {

  title = "Analog Outputs";
  fields:IOField[] = [
    {
      name: "Power Pre Heater One",
      value: "0",
      topic: "aoaccess/1",
      isAnalog: true
    }
    
  ];

  constructor() { }

  ngOnInit() {
  }

}
