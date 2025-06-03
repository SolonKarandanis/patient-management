import {
  animate,
  query,
  style,
  transition,
  trigger,
  stagger
} from '@angular/animations';

const listAnimation = trigger('listAnimation', [
  transition('* <=> *', [
    query(':enter',
      [style({ opacity: 0 }), stagger('60ms', animate('600ms ease-out', style({ opacity: 1 })))],
      { optional: true }
    ),
    query(':leave',
      animate('200ms', style({ opacity: 0 })),
      { optional: true}
    )
  ])
]);


// <ul [@listAnimation]="items.length" class="items">
// <li *ngFor="let item of items" class="item">
//   Item {{ item }}
// </li>
// </ul>
