import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';
import { range } from 'rxjs';

@Component({
  selector: 'ngx-digital-outputs',
  templateUrl: './digital-outputs.component.html',
  styleUrls: ['./digital-outputs.component.scss']
})
export class DigitalOutputsComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    for (let i = 1; i < 20; i++) {
      this.fields.push({name: "DO " + i, topic: "doaccess/" + i, value: false})
    }
  }

  title = "Digital Outputs"
  fields: IOField[] = [];
  // fields: IOField[] = [
  //   {
  //     name: "PLC Start",
  //     topic: "plcStart",
  //     value: false
  //   },

  //   {
  //     name: "PLC Run",
  //     topic: "plcRun",
  //     value: false
  //   },
  //   {
  //     name: "Horn",
  //     topic: "horn",
  //     value: false
  //   }
  // ];

}

