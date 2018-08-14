import { Injectable } from '@angular/core';
import { StompService } from '@stomp/ng2-stompjs';
import { Subscription, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class HeaterService {

  private maxStates = 120;

  private savedStates: { [id: string]: HeaterStatus[] } = {};


  constructor(private stompservice: StompService) { }

  pushNewStatus(topic: HeaterTopics, newStatus: HeaterStatus) {
    this.stompservice.publish("/app/heater/" + topic, JSON.stringify(newStatus));
  }

  getCurrentState(topic: HeaterTopics) {
    this.stompservice.connectObservable.subscribe(value => this.stompservice.publish("/app/heater/" + topic + "/get", ''));
  }

  getAllPreviousStates(topic: HeaterTopics): HeaterStatus[] {
    if (this.savedStates[topic] === undefined) {
      return [];
    }
    return this.savedStates[topic];
  }


  subscribeToHeater(topic: HeaterTopics, callback: (status: HeaterStatus) => void): Subscription {
    var subscription = this.getObservableFor(topic).subscribe(callback);
    this.stompservice.connectObservable.subscribe(value => this.stompservice.publish("/app/heater/" + topic + "/get", ''));
    return subscription;
  }

  private cachedObservables: { [id: string]: Observable<HeaterStatus> } = {};

  private getObservableFor(topic: HeaterTopics): Observable<HeaterStatus> {
    if (this.cachedObservables[topic] === undefined) {
      this.cachedObservables[topic] = this.stompservice.subscribe("/topic/heater/" + topic).pipe(map((message: Message) => {
        var object = JSON.parse(message.body);
        if (object.timestamp === undefined) {
          object['timestamp'] = Date.now();
        }
        this.addNewStateToSavedOnes(topic, <HeaterStatus>object);
        return <HeaterStatus>object;
      }));
    }
    return this.cachedObservables[topic];
  }

  private addNewStateToSavedOnes(topic: HeaterTopics, status: HeaterStatus) {
    if (this.savedStates[topic] === undefined) {
      this.savedStates[topic] = [];
    }
    const length = this.savedStates[topic].push(status);
    if (length > this.maxStates) {
      this.savedStates[topic].shift();
    }
  }

}

export enum HeaterTopics {
  PRE_HEATER_ONE = "preHeaterOne",
  PRE_HEATER_TWO ="preHeaterTwo",
  MAIN_HEATER_ONE = "mainHeaterOne",
  MAIN_HEATER_TWO= "mainHeaterTwo",
  MAIN_HEATER_THREE= "mainHeaterThree",
}

export class HeaterStatus {
  timestamp?: Date;
  on?: boolean;
  controlling?: boolean;
  power?: number;
  targetTemperature?: number;
  currentTemperature?: number;
  p?: number;
  i?: number;
  d?: number;
}