import { Component, Input } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-spinner',
  imports: [MatProgressSpinnerModule],
  templateUrl: './spinner.html',
  styleUrl: './spinner.css'
})
export class Spinner {
  @Input() diameter: number = 40;
}
