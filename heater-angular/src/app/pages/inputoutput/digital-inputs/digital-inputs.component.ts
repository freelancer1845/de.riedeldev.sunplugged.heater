import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';

@Component({
  selector: 'ngx-digital-inputs',
  templateUrl: './digital-inputs.component.html',
  styleUrls: ['./digital-inputs.component.scss']
})
export class DigitalInputsComponent implements OnInit {

  title = "Digital Inputs";
  fields : IOField[] = [];
  // fields : IOField[] = [
  //   {
  //     name: "PLC All Fine",
  //     value: false,
  //     topic: "plcAllFine",
  //     readOnly: true
  //   },
  //   {
  //     name: "Is Overtemperature",
  //     value: false;
  //     topic: "isOvertemperature",
  //     readOnly: true
  //   },
  //   {
  //     name: "Panel Interlock One",
  //     value: false,
  //     topic: "panelInterlockOne",
  //     readOnly: true
  //   },
  //   {
  //     name: "Panel Interlock Two",
  //     value: false,
  //     topic: "panelInterlockTwo",
  //     readOnly: true
  //   },
  //   {
  //     name: "Panel Interlock Three",
  //     value: false,
  //     topic: "panelInterlockThree",
  //     readOnly: true
  //   },
  //   {
  //     name: "Cover Alarm Top",
  //     value: false,
  //     topic: "coverAlarmTop",
  //     readOnly: true
  //   },
  //   {
  //     name: "Cover Alarm Bottom",
  //     value: false,
  //     topic: "coverAlarmBottom",
  //     readOnly: true
  //   },
  //   {
  //     name: "Panel Interlock Alarm",
  //     value: false,
  //     topic: "panelInterlockAlarm",
  //     readOnly: true
  //   }

  // ];

  constructor() { }

  ngOnInit() {
    for (let i = 0; i < 8; i++) {
      this.fields.push({name: "DI " + i, value: false, topic: 'di/' + i, readOnly: true});
    }
  }

}
