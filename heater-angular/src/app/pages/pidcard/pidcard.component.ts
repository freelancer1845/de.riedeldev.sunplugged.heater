import { Component, OnInit, Input, OnDestroy, AfterViewInit, TemplateRef, ContentChild, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { HeaterService, HeaterTopics, HeaterStatus } from '../../@core/data/heater.service';
import { NbThemeService } from '@nebular/theme';
import { NgxEchartsModule } from 'ngx-echarts';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PidvaluesdialogComponent } from './pidvaluesdialog/pidvaluesdialog.component';


@Component({
  selector: 'ngx-pidcard',
  templateUrl: './pidcard.component.html',
  styleUrls: ['./pidcard.component.scss']
})
export class PidcardComponent implements OnInit, OnDestroy, AfterViewInit {

  options: any = {};
  updateOptions: any = {};
  themeSubscription: any;

  currentTempData = [];
  targetTempData = [];
  powerData = [];

  constructor(private heaterService: HeaterService, private theme: NbThemeService, private modalService: NgbModal) {
  }

  status: HeaterStatus = new HeaterStatus();
  subscription: Subscription;

  showPidDialog() {
    const modelRef = this.modalService.open(PidvaluesdialogComponent, {size: 'sm', container: 'nb-layout'});
    var tempStatus = new HeaterStatus();
    Object.assign(tempStatus, this.status);
    modelRef.componentInstance.heaterStatus =tempStatus;

    modelRef.result.then(status  => {
  
        this.onSet(status);
    

    }, r => {});
  }

  @ViewChild("refVar") refVar: TemplateRef<PidvaluesdialogComponent>;


  ngOnInit() {
    this.subscription = this.heaterService.subscribeToHeater(this.topic, newStatus => this.handleNewHeaterStatus(newStatus));
    this.heaterService.getAllPreviousStates(this.topic).forEach((heaterStatus: HeaterStatus) => {
      this.currentTempData.push([heaterStatus.timestamp, heaterStatus.currentTemperature]);
      this.targetTempData.push([heaterStatus.timestamp, heaterStatus.targetTemperature]);
      this.powerData.push([heaterStatus.timestamp, heaterStatus.power]);
    });
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.themeSubscription.unsubscribe();
  }

  ngAfterViewInit() {
    this.themeSubscription = this.theme.getJsTheme().subscribe(config => {

      const colors: any = config.variables;
      const echarts: any = config.variables.echarts;

      this.options = {
        animation: false,
        backgroundColor: echarts.bg,
        color: [colors.danger, colors.primary, colors.info],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}',
          axisPointer: {
            animation: false
          },
        },
        legend: {
          left: 'left',
          data: ['Current Temp', 'Target Temp', 'Power'],
          textStyle: {
            color: echarts.textColor,
          },
        },
        xAxis: [
          {
            type: 'time',
            splitLine: {
              show: false
            }
            // axisTick: {
            //   alignWithLabel: true,
            // },
            // axisLine: {
            //   lineStyle: {
            //     color: echarts.axisLineColor,
            //   },
            // },
            // axisLabel: {
            //   textStyle: {
            //     color: echarts.textColor,
            //   },
            // },
          },
        ],
        yAxis: [
          {
            type: 'value',
            name: 'Temperature',
            boundaryGap: [0, '100%'],
            axisLine: {
              lineStyle: {
                color: echarts.axisLineColor,
              },
            },
            splitLine: {
              lineStyle: {
                color: echarts.splitLineColor,
              },
            },
            axisLabel: {
              textStyle: {
                color: echarts.textColor,
              },
            },
          },
          {
            type: 'value',
            name: 'Power',
            boundaryGap: [0, '100%'],
            axisLine: {
              lineStyle: {
                color: echarts.axisLineColor,
              },
            },
            splitLine: {
              lineStyle: {
                color: echarts.splitLineColor,
              },
            },
            axisLabel: {
              textStyle: {
                color: echarts.textColor,
              },
            },
          },
        ],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        series: [
          {
            id: '1',
            name: 'Current Temp',
            type: 'line',
            data: this.currentTempData,
            hoverAnimation: false,
          },
          {
            id: '2',
            name: 'Target Temp',
            type: 'line',
            data: this.targetTempData,
          },
          {
            id: '3',
            yAxisIndex: 1,
            name: 'Power',
            type: 'line',
            data: this.powerData,
          },
        ],
      };
    });
  }

  onSet(newStatus: HeaterStatus) {
    this.heaterService.pushNewStatus(this.topic, newStatus);
  }

  private handleNewHeaterStatus(newStatus: HeaterStatus) {
    this.status = newStatus;

    // this.currentTempData.push([newStatus.timestamp, newStatus.currentTemperature]);
    // this.targetTempData.push([newStatus.timestamp, newStatus.targetTemperature]);
    // this.powerData.push([newStatus.timestamp, newStatus.power]);

    this.currentTempData = [];
    this.targetTempData = [];
    this.powerData = [];
    this.heaterService.getAllPreviousStates(this.topic).forEach(state => {
      this.currentTempData.push([state.timestamp, state.currentTemperature]);
      this.targetTempData.push([state.timestamp, state.targetTemperature]);
      this.powerData.push([state.timestamp, state.power]);
    });

    this.updateOptions = {
      series: [{
        id: '1',
        data: this.currentTempData,
      },
      {
        id: '2',
        data: this.targetTempData,
      },
      {
        id: '3',
        data: this.powerData,
      },]
    };

    console.log("updated options");
  }

  @Input() title: string;
  @Input() topic: HeaterTopics;

}

