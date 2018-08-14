import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'ngx-analogtext',
  templateUrl: './analogtext.component.html',
  styleUrls: ['./analogtext.component.scss']
})
export class AnalogtextComponent implements OnInit {

  constructor() { }

  value: string;

  ngOnInit() {
  }

  emitNewValue() {
    this.setvalue.emit(this.value);
  }
  @Input() name: string = "";
  @Input() isReadOnly: boolean = true;
  @Output() setvalue: EventEmitter<string> = new EventEmitter();
  @Input() placeholder: string = "";
}
