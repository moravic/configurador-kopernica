import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CreateEmployeeComponent } from './create-employee/create-employee.component';
import { EmployeeDetailsComponent } from './employee-details/employee-details.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { ParticipantListComponent } from './participant-list/participant-list.component';
import { PreguntaListComponent } from './pregunta-list/pregunta-list.component';
import { EstimuloListComponent } from './estimulo-list/estimulo-list.component';
import { HttpClientModule } from '@angular/common/http';
import { UpdateEmployeeComponent } from './update-employee/update-employee.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppMaterialModule } from './app-material.module';
import { StudyComboComponent } from './study-combo/study-combo.component';
import { ProjectComboComponent } from './project-combo/project-combo.component';
import { ProjectTypeRadioComponent } from './projectType-radio/projectType-radio.component';
import { ParticipantDataStoreService } from './participant-list/participant-list.service';
import { PreguntaDataStoreService } from './pregunta-list/pregunta-list.service';
import { EstimuloDataStoreService } from './estimulo-list/estimulo-list.service';
import { MatDialogOkComponent } from './mat-dialog-ok/mat-dialog-ok.component';
import { ProtocolTabsComponent } from './protocol-tabs/protocol-tabs.component';
import { SegmentComponent } from './segment/segment.component';
import { BlockProtocolComponent } from './block-protocol/block-protocol.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateEmployeeComponent,
    EmployeeDetailsComponent,
    EmployeeListComponent,
    UpdateEmployeeComponent,
    ParticipantListComponent,
    PreguntaListComponent,
    EstimuloListComponent,
    ProjectComboComponent,
    StudyComboComponent,
    ProjectTypeRadioComponent,
    MatDialogOkComponent,
    ProtocolTabsComponent,
    SegmentComponent,
    BlockProtocolComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppMaterialModule,
    ReactiveFormsModule
  ],
  providers: [
    ParticipantDataStoreService,
    PreguntaDataStoreService,
    EstimuloDataStoreService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }