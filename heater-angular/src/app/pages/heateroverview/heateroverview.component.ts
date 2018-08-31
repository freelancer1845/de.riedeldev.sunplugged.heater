import { Component, OnInit } from '@angular/core';
import { HeaterTopics } from '../../@core/data/heater.service';

@Component({
  selector: 'ngx-heateroverview',
  templateUrl: './heateroverview.component.html',
  styleUrls: ['./heateroverview.component.scss']
})
export class HeateroverviewComponent implements OnInit {

  constructor() { }

  heaters = [{
    'name': 'Pre Heater One',
    'topic': HeaterTopics.PRE_HEATER_ONE,
  },
  {
    'name': 'Pre Heater Two',
    'topic': HeaterTopics.PRE_HEATER_TWO,
  },
  {
    'name': 'Main Heater One',
    'topic': HeaterTopics.MAIN_HEATER_ONE,
  },
  // {
  //   'name': 'Main Heater Two',
  //   'topic': HeaterTopics.MAIN_HEATER_TWO,
  // },
  {
    'name': 'Main Heater Three',
    'topic': HeaterTopics.MAIN_HEATER_THREE,
  },

  ];

  ngOnInit() {
  }

}
