import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Subscription, Observable } from 'rxjs';
import { StompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';

@Component({
  selector: 'ngx-iocard',
  templateUrl: './iocard.component.html',
  styleUrls: ['./iocard.component.scss']
})
export class IocardComponent implements OnInit, OnDestroy {


  private subscription: Subscription;

  constructor(private stomp_service: StompService) {
  }


  ngOnInit() {
    for (let field of this.fields) {
      field.subscription = this.stomp_service.subscribe('/topic/' + field.topic).subscribe((message: Message) => {
        if (field.isAnalog == true) {
          field.value = message.body;
        } else {
          field.value = (message.body == 'true');
     
        }
      });
      this.stomp_service.connectObservable.subscribe(value => {
        field.value = this.stomp_service.publish('/app/' + field.topic + '/get', '');
      });
        

    }
  }

  ngOnDestroy(): void {
    for (let field of this.fields) {
      field.subscription.unsubscribe();
    }
  }

  sendValue(field: IOField, value: any) {
    this.stomp_service.publish("/app/" + field.topic, value);
  }

  @Input() title: string;
  @Input() fields: IOField[];


}

export class IOField {
  name: string;
  value: any;
  topic: string;
  readOnly?: boolean = true;
  isAnalog?: boolean = false;
  subscription?: Subscription;
}
