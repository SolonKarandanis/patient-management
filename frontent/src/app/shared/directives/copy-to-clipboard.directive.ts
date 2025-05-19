import {Directive, ElementRef, inject, input} from '@angular/core';

@Directive({
  selector: '[appCopyToClipboard]',
  standalone:true,
  host:{
    '(click)': 'copyToClipboard()',
    '[title]': 'tooltipText()',
    '[style.cursor]': '"grab"'
  }
})
export class CopyToClipboardDirective {
  private readonly elRef = inject(ElementRef);
  tooltipText = input('Click to copy to clipboard');

  copyToClipboard():void{
    const text = this.elRef.nativeElement.textContent;
    navigator.clipboard.writeText(text);
  }

}
