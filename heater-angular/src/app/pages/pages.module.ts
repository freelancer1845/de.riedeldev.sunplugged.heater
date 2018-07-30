import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardModule } from './dashboard/dashboard.module';
import { PagesRoutingModule } from './pages-routing.module';
import { ThemeModule } from '../@theme/theme.module';
import { MiscellaneousModule } from './miscellaneous/miscellaneous.module';
import { InputoutputComponent } from './inputoutput/inputoutput.component';
import { DigitalOutputsComponent } from './inputoutput/digital-outputs/digital-outputs.component';
import { DigitalInputsComponent } from './inputoutput/digital-inputs/digital-inputs.component';
import { AnalogOutputsComponent } from './inputoutput/analog-outputs/analog-outputs.component';
import { AnalogInputsComponent } from './inputoutput/analog-inputs/analog-inputs.component';
import { IocardComponent } from './inputoutput/iocard/iocard.component';

const PAGES_COMPONENTS = [
  PagesComponent,
];

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    DashboardModule,
    MiscellaneousModule,
  ],
  declarations: [
    ...PAGES_COMPONENTS,
    InputoutputComponent,
    DigitalOutputsComponent,
    DigitalInputsComponent,
    AnalogOutputsComponent,
    AnalogInputsComponent,
    IocardComponent,
  ],
})
export class PagesModule {
}
