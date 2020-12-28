import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
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
import { GeneralComboComponent } from './general-combo/general-combo.component';
import { ParticipantDataStoreService } from './participant-list/participant-list.service';
import { PreguntaDataStoreService } from './pregunta-list/pregunta-list.service';
import { EstimuloDataStoreService } from './estimulo-list/estimulo-list.service';

@NgModule({
  declarations: [
    AppComponent,
    CreateEmployeeComponent,
    EmployeeDetailsComponent,
    EmployeeListComponent,
    UpdateEmployeeComponent,
    ParticipantListComponent,
    GeneralComboComponent,
    PreguntaListComponent,
    EstimuloListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppMaterialModule
  ],
  providers: [
    ParticipantDataStoreService,
    PreguntaDataStoreService,
    EstimuloDataStoreService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }