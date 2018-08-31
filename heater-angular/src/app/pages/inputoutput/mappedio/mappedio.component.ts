import { Component, OnInit } from '@angular/core';
import { IOField } from '../iocard/iocard.component';

@Component({
  selector: 'ngx-mappedio',
  templateUrl: './mappedio.component.html',
  styleUrls: ['./mappedio.component.scss']
})
export class MappedioComponent implements OnInit {
  title = "Mapped IO";
  fields: IOField[] = [
  ];
  constructor() { }

  ngOnInit() {
    this.fields = [
      {
        name: "PLC All fine",
        readOnly: true,
        isAnalog: false,
        topic: "plcAllFine",
        value: false
      },
      {
        name: "PLC Start",
        isAnalog: false,
        readOnly: false,
        topic: "plcstart",
        value: false
      },
      {
        name: "PLC Run",
        isAnalog: false,
        readOnly: false,
        topic: "plcrun",
        value: false
      },
      {
        name: "Horn",
        isAnalog: false,
        readOnly: false,
        topic: "horn",
        value: false
      },
      {
        name: "Overtemperature",
        readOnly: true,
        value: false,
        topic: "isOvertemperature",
      }, 
      {
        name: "Panel Intelock Alarm",
        readOnly: true,
        value: false,
        topic: "panelInterlockAlarm",
      }

    ];
  }

}
