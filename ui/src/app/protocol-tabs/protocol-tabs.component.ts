import {Component} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'protocol-tabs',
  templateUrl: 'protocol-tabs.component.html',
  styleUrls: ['protocol-tabs.component.css'],
})
export class ProtocolTabsComponent {
  tabs = ['Protocolo 1'];
  selected = new FormControl(0);

  addTab() {
    this.tabs.push('Protocolo ' + (this.tabs.length + 1));

    this.selected.setValue(this.tabs.length - 2);
  }

  removeTab(index: number) {
    this.tabs.splice(index, 1);
  }
}
