import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';

@Component({
  selector: 'ngx-analog-inputs',
  templateUrl: './analog-inputs.component.html',
  styleUrls: ['./analog-inputs.component.scss']
})
export class AnalogInputsComponent implements OnInit {

  title = "Analog Inputs";
  fields:IOField[] = [];

  constructor() {
    for (let i = 0; i < 4; i++) {
      this.fields.push({name: "AI " + i, value: false, topic: 'ai/' + i,isAnalog: true, readOnly: true});
    }
   }

  ngOnInit() {
  }

}
