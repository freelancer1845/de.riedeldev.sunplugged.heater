import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'ngx-digitalfield',
  templateUrl: './digitalfield.component.html',
  styleUrls: ['./digitalfield.component.scss']
})
export class DigitalfieldComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  @Input() state: boolean;
  @Output() emitter: EventEmitter<boolean> = new EventEmitter();

}
